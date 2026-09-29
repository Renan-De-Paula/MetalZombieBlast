import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class WeaponWeightRefactor {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        // 1. Add Weight to Weapon class
        String weaponInit = "this.typeName = \"glock\";\n" +
            "            this.maxAmmo = 30;\n" +
            "            this.ammo = 30;\n" +
            "            \n" +
            "            String nLower = this.name.toLowerCase();";
        String newWeaponInit = weaponInit + "\n" +
            "            this.weight = 1.0;\n";
        code = code.replace(weaponInit, newWeaponInit);
        
        String weaponVars = "int maxAmmo = 30;\n" +
            "        int ammoType = 0;";
        String newWeaponVars = weaponVars + "\n        double weight = 1.0;";
        code = code.replace(weaponVars, newWeaponVars);
        
        // 2. Setup Weight and Knife in Constructor
        String typeChecks = "if (nLower.contains(\"martelo\")) { this.typeName = \"martelo\"; this.ammoType = -1; this.maxAmmo = 1; }";
        String newTypeChecks = typeChecks + "\n" +
            "            else if (nLower.contains(\"faca\")) { this.typeName = \"faca\"; this.ammoType = -1; this.maxAmmo = 1; this.weight = 1.0; }\n";
        code = code.replace(typeChecks, newTypeChecks);
        
        // Adjust weights
        code = code.replace("this.typeName = \"ak47\"; this.ammoType = 2; this.maxAmmo = 30; }", "this.typeName = \"ak47\"; this.ammoType = 2; this.maxAmmo = 30; this.weight = 0.8; }");
        code = code.replace("this.typeName = \"minigun\"; this.ammoType = 2; this.maxAmmo = 100; }", "this.typeName = \"minigun\"; this.ammoType = 2; this.maxAmmo = 100; this.weight = 0.5; }");
        code = code.replace("this.typeName = \"shotgun\"; this.ammoType = 3; this.maxAmmo = 8; }", "this.typeName = \"shotgun\"; this.ammoType = 3; this.maxAmmo = 8; this.weight = 0.8; }");
        code = code.replace("this.typeName = \"rpg\"; this.ammoType = 4; this.maxAmmo = 1; }", "this.typeName = \"rpg\"; this.ammoType = 4; this.maxAmmo = 1; this.weight = 0.6; }");
        code = code.replace("this.typeName = \"lancagranadas\"; this.ammoType = 4; this.maxAmmo = 1; }", "this.typeName = \"lancagranadas\"; this.ammoType = 4; this.maxAmmo = 1; this.weight = 0.7; }");
        code = code.replace("this.typeName = \"lancachamas\"; this.ammoType = 5; this.maxAmmo = 50; }", "this.typeName = \"lancachamas\"; this.ammoType = 5; this.maxAmmo = 50; this.weight = 0.7; }");
        
        // 3. Update speed calculation in Player.update()
        String speedCalc = "double currSpeed = ZombieGame.this.isAiming ? this.speed * 0.7 : this.speed;";
        String newSpeedCalc = "Weapon aw = this.activeSlot == 1 ? this.weapon1 : this.weapon2;\n" +
            "            double wWeight = (aw != null) ? aw.weight : 1.0;\n" +
            "            double currSpeed = this.speed * wWeight;\n" +
            "            if (ZombieGame.this.isAiming) currSpeed *= 0.5;";
        code = code.replace(speedCalc, newSpeedCalc);

        Files.write(f.toPath(), code.getBytes());
    }
}
