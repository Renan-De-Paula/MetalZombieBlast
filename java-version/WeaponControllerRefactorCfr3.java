import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class WeaponControllerRefactorCfr3 {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        code = code.replace("private static BufferedImage imgPlayerIdle;", "public static java.util.Map<String, java.awt.image.BufferedImage[]> weaponFrames = new java.util.HashMap();\n    public static java.awt.image.BufferedImage imgPlayerIdle;");
        code = code.replace("ZombieGame.weaponFrames.put(w, frames);", "weaponFrames.put(w, frames);");
        
        Files.write(f.toPath(), code.getBytes());
    }
}
