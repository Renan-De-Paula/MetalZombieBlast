import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class WeaponWeightRefactorFix {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        // 1. Add Weight to Weapon class variables
        String weaponVars = "int ammoType;";
        String newWeaponVars = "int ammoType;\n        double weight = 1.0;";
        code = code.replace(weaponVars, newWeaponVars);
        
        // 2. Fix the aw variable name
        String speedCalc = "Weapon aw = this.activeSlot == 1 ? this.weapon1 : this.weapon2;\n" +
            "            double wWeight = (aw != null) ? aw.weight : 1.0;\n" +
            "            double currSpeed = this.speed * wWeight;\n" +
            "            if (ZombieGame.this.isAiming) currSpeed *= 0.5;";
        String newSpeedCalc = "Weapon wActive = this.activeSlot == 1 ? this.weapon1 : this.weapon2;\n" +
            "            double wWeight = (wActive != null) ? wActive.weight : 1.0;\n" +
            "            double currSpeed = this.speed * wWeight;\n" +
            "            if (ZombieGame.this.isAiming) currSpeed *= 0.5;";
        code = code.replace(speedCalc, newSpeedCalc);

        Files.write(f.toPath(), code.getBytes());
    }
}
