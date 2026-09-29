import java.io.File;
import java.nio.file.Files;

public class RestoreWorkbench {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));

        String workbenchCode = "    class Workbench {\n" +
            "        double x, y;\n" +
            "        Workbench(double x, double y) { this.x = x; this.y = y; }\n" +
            "        void draw(java.awt.Graphics2D g) {\n" +
            "            if (imgWorkbench != null) g.drawImage(imgWorkbench, (int)x, (int)y, 60, 60, null);\n" +
            "            else { g.setColor(java.awt.Color.GRAY); g.fillRect((int)x, (int)y, 60, 60); }\n" +
            "        }\n" +
            "    }\n";
            
        code = code.replace("    class SolidWall", workbenchCode + "    class SolidWall");
        Files.write(f.toPath(), code.getBytes());
    }
}
