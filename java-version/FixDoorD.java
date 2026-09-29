import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class FixDoorD {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        code = code.replace("for (Door d : doors)", "for (Door doorItem : doors)");
        code = code.replace("d.getBounds()", "doorItem.getBounds()");
        Files.write(f.toPath(), code.getBytes());
    }
}
