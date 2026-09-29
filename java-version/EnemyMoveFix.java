import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class EnemyMoveFix {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        code = code.replace("for (Door d : ZombieGame.this.doors)", "for (Door dr : ZombieGame.this.doors)");
        code = code.replace("if (d.getBounds()", "if (dr.getBounds()");

        Files.write(f.toPath(), code.getBytes());
    }
}
