import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class WeaponControllerRefactor {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        // 1. Add weapon frame loading to Core
        String staticVars = "public static BufferedImage imgPlayerIdle;";
        String newStaticVars = "public static java.util.Map<String, BufferedImage[]> weaponFrames = new java.util.HashMap();\n    " + staticVars;
        code = code.replace(staticVars, newStaticVars);
        
        String imageLoading = "imgPlayerShoot = ImageIO.read(new File(\"img/player_atirando_arma_levantada.png\"));";
        String newImageLoading = imageLoading + "\n" +
            "            String[] weapons = {\"revolver\", \"glock\", \"ak47\", \"shotgun\", \"minigun\", \"rpg\", \"lancachamas\", \"lancagranadas\", \"smg\", \"miniuzi\"};\n" +
            "            for (String w : weapons) {\n" +
            "                BufferedImage[] frames = new BufferedImage[6];\n" +
            "                for (int i = 0; i < 6; i++) {\n" +
            "                    File wf = new File(\"img/weapon_\" + w + \"_f\" + i + \".png\");\n" +
            "                    if (wf.exists()) frames[i] = ImageIO.read(wf);\n" +
            "                }\n" +
            "                weaponFrames.put(w, frames);\n" +
            "            }";
        code = code.replace(imageLoading, newImageLoading);
        
        // 2. Modify Weapon class to hold type name
        String oldWeaponStr = "class Weapon {\n        String name;";
        String newWeaponStr = "class Weapon {\n        String name;\n        String typeName;";
        code = code.replace(oldWeaponStr, newWeaponStr);
        
        code = code.replace("this.name = string;", "this.name = string;\n            this.typeName = \"glock\"; // default fallback");
        
        // 3. Update Player draw logic to use WeaponController states
        // Current draw:
        // boolean bl2 = System.currentTimeMillis() - this.lastShotTime < 150L;
        // BufferedImage bufferedImage = bl2 ? imgPlayerShoot : imgPlayerIdle;
        String oldDraw = "boolean bl2 = System.currentTimeMillis() - this.lastShotTime < 150L;\n" +
            "            BufferedImage bufferedImage = bl2 ? imgPlayerShoot : imgPlayerIdle;";
        String newDraw = "Weapon activeWeapon = this.activeSlot == 1 ? this.weapon1 : this.weapon2;\n" +
            "            int frame = 0; // 0=Idle\n" +
            "            if (blArray[1]) { // Aiming (Right click)\n" +
            "                frame = 1;\n" +
            "                if (blArray[0]) frame = 2; // Shooting (Left click)\n" +
            "            }\n" +
            "            if (System.currentTimeMillis() - this.lastShotTime < 150L) frame = 2;\n" +
            "            if (this.isReloading) {\n" +
            "                long rElapsed = System.currentTimeMillis() - this.reloadStartTime;\n" +
            "                frame = rElapsed < 1000 ? 3 : 4;\n" +
            "            } else if (activeWeapon.ammo <= 0) frame = 5;\n" +
            "            \n" +
            "            BufferedImage bufferedImage = imgPlayerIdle;\n" +
            "            if (weaponFrames.containsKey(activeWeapon.typeName)) {\n" +
            "                BufferedImage[] frames = weaponFrames.get(activeWeapon.typeName);\n" +
            "                if (frames[frame] != null) bufferedImage = frames[frame];\n" +
            "            }";
        code = code.replace(oldDraw, newDraw);
        
        // Player speed penalty while aiming
        code = code.replace("if (blArray[37] || blArray[65]) d3 = -this.speed;", "double currSpeed = blArray[1] ? this.speed * 0.7 : this.speed;\n            if (blArray[37] || blArray[65]) d3 = -currSpeed;");
        code = code.replace("if (blArray[39] || blArray[68]) d3 = this.speed;", "if (blArray[39] || blArray[68]) d3 = currSpeed;");
        code = code.replace("if (blArray[38] || blArray[87]) d4 = -this.speed;", "if (blArray[38] || blArray[87]) d4 = -currSpeed;");
        code = code.replace("if (blArray[40] || blArray[83]) d4 = this.speed;", "if (blArray[40] || blArray[83]) d4 = currSpeed;");
        
        // Add reload fields to player
        String playerFields = "long lastShotTime = 0L;";
        String newPlayerFields = playerFields + "\n        boolean isReloading = false;\n        long reloadStartTime = 0L;";
        code = code.replace(playerFields, newPlayerFields);
        
        // Add reloading key 'R' (82) logic
        String oldKey82 = "if (blArray[82]) {\n" +
            "            this.weapon1.ammo = this.weapon1.maxAmmo;\n" +
            "            this.weapon2.ammo = this.weapon2.maxAmmo;\n" +
            "        }";
        String newKey82 = "if (blArray[82] && !this.player.isReloading) {\n" +
            "            this.player.isReloading = true;\n" +
            "            this.player.reloadStartTime = System.currentTimeMillis();\n" +
            "        }";
        code = code.replace(oldKey82, newKey82);
        
        // Process Reloading in Player.update
        String oldUpdateEnd = "if (canMoveY) this.y = d;\n" +
            "        }";
        String newUpdateEnd = "if (canMoveY) this.y = d;\n" +
            "            if (this.isReloading && System.currentTimeMillis() - this.reloadStartTime > 1500L) {\n" +
            "                this.isReloading = false;\n" +
            "                Weapon aw = this.activeSlot == 1 ? this.weapon1 : this.weapon2;\n" +
            "                aw.ammo = aw.maxAmmo;\n" +
            "            }\n" +
            "        }";
        code = code.replace(oldUpdateEnd, newUpdateEnd);
        
        // Block shooting while reloading
        code = code.replace("if (this.player.activeSlot == 1) {", "if (this.player.isReloading) return;\n        if (this.player.activeSlot == 1) {");

        Files.write(f.toPath(), code.getBytes());
    }
}
