import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class UILogic {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        // Add HUD elements for Inventory
        String hudEnd = "graphics2D.drawString(\"Casa HP: \" + this.houseCore.health, 595, 585);";
        String newHudEnd = "graphics2D.drawString(\"Casa HP: \" + this.houseCore.health, 595, 585);\n" +
            "        Weapon aw = this.player.activeSlot == 1 ? this.player.weapon1 : this.player.weapon2;\n" +
            "        graphics2D.setColor(java.awt.Color.WHITE);\n" +
            "        graphics2D.setFont(new java.awt.Font(\"Arial\", 1, 14));\n" +
            "        if (aw != null && !aw.typeName.equals(\"martelo\")) {\n" +
            "            graphics2D.drawString(\"ARMA: \" + aw.name + \" | Muni: \" + aw.ammo + \"/\" + aw.maxAmmo + \" | Reserva: \" + this.globalAmmo[aw.ammoType], 300, 585);\n" +
            "        }\n" +
            "        graphics2D.setFont(new java.awt.Font(\"Arial\", 0, 12));\n" +
            "        graphics2D.drawString(\"Mad:\" + this.wood + \" Pregos:\" + this.nails + \" Ferro:\" + this.iron + \" Parf:\" + this.screws + \" Chum:\" + this.lead, 15, 80);";
        code = code.replace(hudEnd, newHudEnd);
        
        Files.write(f.toPath(), code.getBytes());
    }
}
