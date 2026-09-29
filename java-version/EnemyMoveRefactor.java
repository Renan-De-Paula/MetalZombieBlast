import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class EnemyMoveRefactor {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        String oldLogic = "if (d4 > 0.0 && !this.state.equals(\"attack\")) {\n" +
            "                this.x += Math.cos(this.angle) * d4 * 0.016;\n" +
            "                this.y += Math.sin(this.angle) * d4 * 0.016;\n" +
            "                this.changeState(\"walk\");\n" +
            "                if (System.currentTimeMillis() - this.lastFrameChange > 150L) {\n" +
            "                    this.frameCount = this.frameCount == 1 ? 2 : 1;\n" +
            "                    this.lastFrameChange = System.currentTimeMillis();\n" +
            "                }\n" +
            "            } else if (d4 == 0.0 && !this.state.equals(\"attack\")) {\n" +
            "                this.changeState(\"idle\");\n" +
            "            }";

        String newLogic = "if (d4 > 0.0 && !this.state.equals(\"attack\")) {\n" +
            "                double vx = Math.cos(this.angle) * d4 * 0.016;\n" +
            "                double vy = Math.sin(this.angle) * d4 * 0.016;\n" +
            "                \n" +
            "                java.awt.Rectangle boundsX = new java.awt.Rectangle((int)(this.x + vx) - this.size/2, (int)this.y - this.size/2, this.size, this.size);\n" +
            "                boolean canX = true;\n" +
            "                for (SolidWall w : arrayList2) if (w.getBounds().intersects(boundsX)) canX = false;\n" +
            "                for (Door d : ZombieGame.this.doors) if (d.getBounds().intersects(boundsX)) {\n" +
            "                    canX = false;\n" +
            "                    this.changeState(\"attack\");\n" +
            "                }\n" +
            "                if (canX) this.x += vx;\n" +
            "\n" +
            "                java.awt.Rectangle boundsY = new java.awt.Rectangle((int)this.x - this.size/2, (int)(this.y + vy) - this.size/2, this.size, this.size);\n" +
            "                boolean canY = true;\n" +
            "                for (SolidWall w : arrayList2) if (w.getBounds().intersects(boundsY)) canY = false;\n" +
            "                for (Door d : ZombieGame.this.doors) if (d.getBounds().intersects(boundsY)) {\n" +
            "                    canY = false;\n" +
            "                    this.changeState(\"attack\");\n" +
            "                }\n" +
            "                if (canY) this.y += vy;\n" +
            "\n" +
            "                if (canX || canY) {\n" +
            "                    this.changeState(\"walk\");\n" +
            "                    if (System.currentTimeMillis() - this.lastFrameChange > 150L) {\n" +
            "                        this.frameCount = this.frameCount == 1 ? 2 : 1;\n" +
            "                        this.lastFrameChange = System.currentTimeMillis();\n" +
            "                    }\n" +
            "                }\n" +
            "            } else if (d4 == 0.0 && !this.state.equals(\"attack\")) {\n" +
            "                this.changeState(\"idle\");\n" +
            "            }";

        code = code.replace(oldLogic, newLogic);

        Files.write(f.toPath(), code.getBytes());
    }
}
