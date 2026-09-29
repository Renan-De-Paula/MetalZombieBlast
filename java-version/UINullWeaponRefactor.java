import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class UINullWeaponRefactor {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        // Fix weapon1 color and name
        String oldUI = "graphics2D.setColor(this.player.weapon1.color);\n" +
            "        String string = \"1: \" + this.player.weapon1.name;";
        String newUI = "if (this.player.weapon1 == null) {\n" +
            "            graphics2D.setColor(java.awt.Color.GRAY);\n" +
            "            graphics2D.drawString(\"1: Vazio\", 10, 50);\n" +
            "        } else {\n" +
            "            graphics2D.setColor(this.player.weapon1.color);\n" +
            "            String string = \"1: \" + this.player.weapon1.name;\n";
        code = code.replace(oldUI, newUI);
        
        // Fix closing brace
        String oldEndUI = "graphics2D.drawString(string, 10, 50);\n" +
            "        if (this.playerLevel >= 5 || this.player.weapon2 != null) {";
        String newEndUI = "graphics2D.drawString(string, 10, 50);\n" +
            "        }\n" +
            "        if (this.playerLevel >= 5 || this.player.weapon2 != null) {";
        code = code.replace(oldEndUI, newEndUI);
        
        // Also fix Player.update where it might crash getting Weapon aw
        String updateUI = "if (aw != null && !aw.typeName.equals(\"martelo\")) {\n" +
            "            graphics2D.drawString(\"ARMA: \" + aw.name + \" | Muni: \" + aw.ammo + \"/\" + aw.maxAmmo + \" | Reserva: \" + this.globalAmmo[aw.ammoType], 300, 585);\n" +
            "        }";
        String newUpdateUI = "if (aw != null && aw.ammoType >= 0) {\n" +
            "            graphics2D.drawString(\"ARMA: \" + aw.name + \" | Muni: \" + aw.ammo + \"/\" + aw.maxAmmo + \" | Reserva: \" + this.globalAmmo[aw.ammoType], 300, 585);\n" +
            "        }";
        code = code.replace(updateUI, newUpdateUI);

        Files.write(f.toPath(), code.getBytes());
    }
}
