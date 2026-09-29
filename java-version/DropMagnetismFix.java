import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class DropMagnetismFix {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        String dropMagnet = "double d2 = Math.atan2(this.player.y - iteratorD.y, this.player.x - iteratorD.x);\n" +
            "            iteratorD.x += Math.cos(d2) * 3500.0 * 0.016;\n" +
            "            iteratorD.y += Math.sin(d2) * 3500.0 * 0.016;";
        String newDropMagnet = "if (d < 150.0) {\n" +
            "                double d2 = Math.atan2(this.player.y - iteratorD.y, this.player.x - iteratorD.x);\n" +
            "                iteratorD.x += Math.cos(d2) * 500.0 * 0.016;\n" +
            "                iteratorD.y += Math.sin(d2) * 500.0 * 0.016;\n" +
            "            }";
        code = code.replace(dropMagnet, newDropMagnet);
        
        Files.write(f.toPath(), code.getBytes());
    }
}
