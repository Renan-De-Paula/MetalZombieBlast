import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class InventoryFix {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        code = code.replace("aW.maxRange", "aW.cooldown");

        Files.write(f.toPath(), code.getBytes());
    }
}
