import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class HammerLogic {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        // 1. Add "martelo" to loaded weapons
        code = code.replace("\"rpg\", \"lancachamas\", \"lancagranadas\", \"smg\", \"miniuzi\"", "\"rpg\", \"lancachamas\", \"lancagranadas\", \"smg\", \"miniuzi\", \"martelo\"");
        
        // 2. Modify mouseAttack
        String shootBegin = "Weapon aw = this.player.activeSlot == 1 ? this.player.weapon1 : this.player.weapon2;\n" +
            "            if (aw.ammo <= 0) {";
        String newShootBegin = "Weapon aw = this.player.activeSlot == 1 ? this.player.weapon1 : this.player.weapon2;\n" +
            "            if (aw.typeName.equals(\"martelo\")) {\n" +
            "                this.player.lastShotTime = System.currentTimeMillis();\n" +
            "                java.awt.Rectangle hitBox = new java.awt.Rectangle((int)this.player.x - 30, (int)this.player.y - 30, 60, 60);\n" +
            "                for (Window w : this.windows) {\n" +
            "                    if (w.getBounds().intersects(hitBox) && w.health < 100) {\n" +
            "                        if (this.wood >= 1 && this.nails >= 1) {\n" +
            "                            this.wood--; this.nails--;\n" +
            "                            w.health = Math.min(100, w.health + 20);\n" +
            "                            this.damageTexts.add(new DamageText(w.centerX, w.centerY, 20));\n" +
            "                        }\n" +
            "                    }\n" +
            "                }\n" +
            "                for (Door d : this.doors) {\n" +
            "                    if (d.getBounds().intersects(hitBox)) {\n" +
            "                        // We don't have Door health yet, but we could add it.\n" +
            "                        if (this.iron >= 1 && this.screws >= 1) { this.iron--; this.screws--; }\n" +
            "                    }\n" +
            "                }\n" +
            "                return;\n" +
            "            }\n" +
            "            if (aw.ammo <= 0) {";
        code = code.replace(shootBegin, newShootBegin);

        Files.write(f.toPath(), code.getBytes());
    }
}
