import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class InitWeaponsScript {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        String constructorEnd = "this.color = color;\n" +
            "            this.projSpeed = d2;\n" +
            "        }";
            
        String logic = "this.color = color;\n" +
            "            this.projSpeed = d2;\n" +
            "            \n" +
            "            String nLower = this.name.toLowerCase();\n" +
            "            if (nLower.contains(\"martelo\")) { this.typeName = \"martelo\"; this.ammoType = -1; this.maxAmmo = 1; }\n" +
            "            else if (nLower.contains(\"glock\") || nLower.contains(\"pistola\")) { this.typeName = \"glock\"; this.ammoType = 0; this.maxAmmo = 15; }\n" +
            "            else if (nLower.contains(\"revolver\") || nLower.contains(\"magnum\") || nLower.contains(\"eagle\")) { this.typeName = \"revolver\"; this.ammoType = 0; this.maxAmmo = 6; }\n" +
            "            else if (nLower.contains(\"smg\") || nLower.contains(\"p90\")) { this.typeName = \"smg\"; this.ammoType = 1; this.maxAmmo = 30; }\n" +
            "            else if (nLower.contains(\"uzi\")) { this.typeName = \"miniuzi\"; this.ammoType = 1; this.maxAmmo = 30; }\n" +
            "            else if (nLower.contains(\"ak-47\") || nLower.contains(\"m16\") || nLower.contains(\"fuzil\") || nLower.contains(\"m4\")) { this.typeName = \"ak47\"; this.ammoType = 2; this.maxAmmo = 30; }\n" +
            "            else if (nLower.contains(\"minigun\")) { this.typeName = \"minigun\"; this.ammoType = 2; this.maxAmmo = 100; }\n" +
            "            else if (nLower.contains(\"escopeta\")) { this.typeName = \"shotgun\"; this.ammoType = 3; this.maxAmmo = 8; }\n" +
            "            else if (nLower.contains(\"rpg\")) { this.typeName = \"rpg\"; this.ammoType = 4; this.maxAmmo = 1; }\n" +
            "            else if (nLower.contains(\"granada\")) { this.typeName = \"lancagranadas\"; this.ammoType = 4; this.maxAmmo = 1; }\n" +
            "            else if (nLower.contains(\"chamas\")) { this.typeName = \"lancachamas\"; this.ammoType = 5; this.maxAmmo = 50; }\n" +
            "            else { this.typeName = \"glock\"; this.ammoType = 0; this.maxAmmo = 10; }\n" +
            "            this.ammo = this.maxAmmo;\n" +
            "        }";
            
        code = code.replace(constructorEnd, logic);
        
        // Let's add Martelo to the starting weapons
        String initPlayer = "this.player = new Player(this, 400.0, 300.0);\n" +
            "        this.player.weapon1 = this.comuns[0];";
        String newInitPlayer = "this.player = new Player(this, 400.0, 300.0);\n" +
            "        this.player.weapon1 = new Weapon(this, \"Martelo\", \"Comum\", 25, 400L, 1, 0.0, java.awt.Color.GRAY, 0.0);\n" +
            "        this.player.weapon2 = this.comuns[0];\n" +
            "        this.player.activeSlot = 2;";
        code = code.replace(initPlayer, newInitPlayer);

        Files.write(f.toPath(), code.getBytes());
    }
}
