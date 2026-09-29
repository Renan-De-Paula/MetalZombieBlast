import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class WeaponNullCrashFix {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        String attack = "if (this.isMousePressed && System.currentTimeMillis() - this.player.lastShotTime > weapon.cooldown) {";
        String newAttack = "if (weapon != null && this.isMousePressed && System.currentTimeMillis() - this.player.lastShotTime > weapon.cooldown) {";
        code = code.replace(attack, newAttack);
        
        String rKey = "if (keyEvent.getKeyCode() == 82) {";
        String newRKey = "if (keyEvent.getKeyCode() == 82 && weapon != null && weapon.ammoType >= 0) {\n" +
            "                    this.player.isReloading = true;\n" +
            "                    this.player.reloadStartTime = System.currentTimeMillis();\n" +
            "                }\n" +
            "                if (keyEvent.getKeyCode() == 82) {"; // Leave old R key for repairing windows
        code = code.replace(rKey, newRKey);

        Files.write(f.toPath(), code.getBytes());
    }
}
