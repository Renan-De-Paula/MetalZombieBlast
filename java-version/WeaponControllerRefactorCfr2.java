import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class WeaponControllerRefactorCfr2 {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        // Fix weaponFrames missing
        code = code.replace("public static BufferedImage imgPlayerIdle;", "public static java.util.Map<String, java.awt.image.BufferedImage[]> weaponFrames = new java.util.HashMap();\n    public static java.awt.image.BufferedImage imgPlayerIdle;");
        code = code.replace("weaponFrames.put(w, frames);", "ZombieGame.weaponFrames.put(w, frames);");
        
        // Fix Weapon ammo missing
        code = code.replace("class Weapon {\n        String name;\n        String typeName;", "class Weapon {\n        String name;\n        String typeName;\n        int maxAmmo;\n        int ammo;");
        code = code.replace("this.typeName = \"glock\"; // default fallback", "this.typeName = \"glock\";\n            this.maxAmmo = 30;\n            this.ammo = 30;");
        
        // Fix blArray in player draw
        String oldDraw = "if (blArray[1]) { // Aiming (Right click)\n" +
            "                frame = 1;\n" +
            "                if (blArray[0]) frame = 2; // Shooting (Left click)\n" +
            "            }";
        String newDraw = "if (ZombieGame.this.isAiming) {\n" +
            "                frame = 1;\n" +
            "                if (ZombieGame.this.isMousePressed) frame = 2;\n" +
            "            }";
        code = code.replace(oldDraw, newDraw);
        
        code = code.replace("if (weaponFrames.containsKey(activeWeapon.typeName)) {", "if (ZombieGame.weaponFrames.containsKey(activeWeapon.typeName)) {");
        code = code.replace("BufferedImage[] frames = weaponFrames.get(activeWeapon.typeName);", "java.awt.image.BufferedImage[] frames = ZombieGame.weaponFrames.get(activeWeapon.typeName);");
        
        // Add isAiming boolean
        code = code.replace("boolean isMousePressed = false;", "boolean isMousePressed = false;\n    boolean isAiming = false;");
        
        // Update mouse methods to track right click
        String mousePressed = "public void mousePressed(MouseEvent mouseEvent) {\n" +
            "        if (mouseEvent.getButton() == 1) {\n" +
            "            this.isMousePressed = true;\n" +
            "        }";
        String newMousePressed = mousePressed + "\n        if (mouseEvent.getButton() == 3) {\n            this.isAiming = true;\n        }";
        code = code.replace(mousePressed, newMousePressed);
        
        String mouseReleased = "public void mouseReleased(MouseEvent mouseEvent) {\n" +
            "        if (mouseEvent.getButton() == 1) {\n" +
            "            this.isMousePressed = false;\n" +
            "        }";
        String newMouseReleased = mouseReleased + "\n        if (mouseEvent.getButton() == 3) {\n            this.isAiming = false;\n        }";
        code = code.replace(mouseReleased, newMouseReleased);
        
        // Fix player ammo variables inside ZombieGame.java
        // (Wait, we already added `int ammo, maxAmmo` inside `class Weapon`)
        
        // Fix player movement update penalty
        code = code.replace("double currSpeed = blArray[1] ? this.speed * 0.7 : this.speed;", "double currSpeed = ZombieGame.this.isAiming ? this.speed * 0.7 : this.speed;");
        
        // Make sure mouseAttack consumes ammo!
        String mouseAttackStr = "this.player.lastShotTime = System.currentTimeMillis();\n" +
            "            this.spawnProjectiles";
        String newMouseAttackStr = "Weapon aw = this.player.activeSlot == 1 ? this.player.weapon1 : this.player.weapon2;\n" +
            "            if (aw.ammo <= 0) {\n" +
            "                if (!this.player.isReloading) {\n" +
            "                    this.player.isReloading = true;\n" +
            "                    this.player.reloadStartTime = System.currentTimeMillis();\n" +
            "                }\n" +
            "                return;\n" +
            "            }\n" +
            "            aw.ammo--;\n" +
            "            this.player.lastShotTime = System.currentTimeMillis();\n" +
            "            this.spawnProjectiles";
        code = code.replace(mouseAttackStr, newMouseAttackStr);
        
        Files.write(f.toPath(), code.getBytes());
    }
}
