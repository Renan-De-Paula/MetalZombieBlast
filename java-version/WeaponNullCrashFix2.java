import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class WeaponNullCrashFix2 {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        String badR = "if (keyEvent.getKeyCode() == 82 && weapon != null && weapon.ammoType >= 0) {";
        String newR = "Weapon wAct = this.player.activeSlot == 1 ? this.player.weapon1 : this.player.weapon2;\n" +
            "                if (keyEvent.getKeyCode() == 82 && wAct != null && wAct.ammoType >= 0) {";
        code = code.replace(badR, newR);

        Files.write(f.toPath(), code.getBytes());
    }
}
