import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class HealthFix {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        code = code.replace("aoeE.life -=", "aoeE.health -=");
        code = code.replace("aoeE.life <=", "aoeE.health <=");
        code = code.replace("objE.life -=", "objE.health -=");
        code = code.replace("objE.life <=", "objE.health <=");

        Files.write(f.toPath(), code.getBytes());
    }
}
