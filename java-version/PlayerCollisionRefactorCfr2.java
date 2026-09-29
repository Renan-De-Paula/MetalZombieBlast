import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class PlayerCollisionRefactorCfr2 {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));

        // Change player.update body
        String regex = "void update\\(boolean\\[\\] blArray, ArrayList<SolidWall> arrayList\\) \\{.*?if \\(\\(d = this\\.y \\+ d4 \\* 0\\.016\\) > 170\\.0 && d < 430\\.0\\) \\{\\s*this\\.y = d;\\s*\\}\\s*\\}";
        Pattern p = Pattern.compile(regex, Pattern.DOTALL);
        Matcher m = p.matcher(code);
        
        String newUpdate = "void update(boolean[] blArray, java.util.ArrayList<SolidWall> arrayList, java.util.ArrayList<Window> windows, java.util.ArrayList<Door> doors) {\n" +
            "            double d3 = 0.0;\n" +
            "            double d4 = 0.0;\n" +
            "            if (blArray[37] || blArray[65]) d3 = -this.speed;\n" +
            "            if (blArray[39] || blArray[68]) d3 = this.speed;\n" +
            "            if (blArray[38] || blArray[87]) d4 = -this.speed;\n" +
            "            if (blArray[40] || blArray[83]) d4 = this.speed;\n" +
            "            if (d3 != 0.0 && d4 != 0.0) { d3 *= 0.7071; d4 *= 0.7071; }\n" +
            "            double d2 = this.x + d3 * 0.016;\n" +
            "            java.awt.Rectangle boundsX = new java.awt.Rectangle((int)d2 - 15, (int)this.y - 15, 30, 30);\n" +
            "            boolean canMoveX = true;\n" +
            "            for (SolidWall w : arrayList) if (w.getBounds().intersects(boundsX)) canMoveX = false;\n" +
            "            for (Window w : windows) if (w.health > 0 && w.getBounds().intersects(boundsX)) canMoveX = false;\n" +
            "            for (Door d : doors) if (d.getBounds().intersects(boundsX)) canMoveX = false;\n" +
            "            if (canMoveX) this.x = d2;\n" +
            "            double d = this.y + d4 * 0.016;\n" +
            "            java.awt.Rectangle boundsY = new java.awt.Rectangle((int)this.x - 15, (int)d - 15, 30, 30);\n" +
            "            boolean canMoveY = true;\n" +
            "            for (SolidWall w : arrayList) if (w.getBounds().intersects(boundsY)) canMoveY = false;\n" +
            "            for (Window w : windows) if (w.health > 0 && w.getBounds().intersects(boundsY)) canMoveY = false;\n" +
            "            for (Door d : doors) if (d.getBounds().intersects(boundsY)) canMoveY = false;\n" +
            "            if (canMoveY) this.y = d;\n" +
            "        }";
            
        if (m.find()) {
            code = m.replaceFirst(newUpdate);
        }
        
        Files.write(f.toPath(), code.getBytes());
    }
}
