import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class DamageFix {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        String badDrop = "new Weapon(this, \"Faca de Combate\", \"Especial\", 1, 300L, 1, 0.0, java.awt.Color.LIGHT_GRAY, 100.0)";
        String goodDrop = "new Weapon(this, \"Faca de Combate\", \"Especial\", 15, 300L, 1, 0.0, java.awt.Color.LIGHT_GRAY, 250.0)";
        
        code = code.replace(badDrop, goodDrop);

        Files.write(f.toPath(), code.getBytes());
    }
}
