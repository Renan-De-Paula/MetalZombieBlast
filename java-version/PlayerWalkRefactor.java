import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class PlayerWalkRefactor {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        // 1. Add isMoving and wobbleTime to Player
        String playerVars = "class Player {\n" +
            "        double x;\n" +
            "        double y;";
        String newPlayerVars = "class Player {\n" +
            "        double x;\n" +
            "        double y;\n" +
            "        boolean isMoving = false;\n" +
            "        long wobbleTime = 0;";
        code = code.replace(playerVars, newPlayerVars);
        
        // 2. Update isMoving in update()
        String moveLogic = "if (d3 != 0.0 && d4 != 0.0) { d3 *= 0.7071; d4 *= 0.7071; }";
        String newMoveLogic = moveLogic + "\n" +
            "            this.isMoving = (d3 != 0.0 || d4 != 0.0);\n" +
            "            if (this.isMoving) this.wobbleTime += 16;\n" +
            "            else this.wobbleTime = 0;";
        code = code.replace(moveLogic, newMoveLogic);
        
        // 3. Draw wobble logic
        String drawAngle = "double d = Math.atan2(mouseY - this.y, mouseX - this.x);";
        String newDrawAngle = "double dx = mouseX - this.x;\n" +
            "                double dy = mouseY - this.y;\n" +
            "                double d = Math.atan2(dy, dx);\n" +
            "                if (this.isMoving && !ZombieGame.this.isAiming) {\n" +
            "                    d += Math.sin(this.wobbleTime * 0.015) * 0.2;\n" +
            "                }";
        code = code.replace(drawAngle, newDrawAngle);
        
        // 4. Bobbing scale
        String drawScale = "double d2 = Math.min(80.0 / (double)n, 80.0 / (double)n2);";
        String newDrawScale = "double d2 = Math.min(80.0 / (double)n, 80.0 / (double)n2);\n" +
            "                if (this.isMoving && !ZombieGame.this.isAiming) {\n" +
            "                    d2 *= 1.0 + Math.abs(Math.sin(this.wobbleTime * 0.015)) * 0.05;\n" +
            "                }";
        code = code.replace(drawScale, newDrawScale);

        Files.write(f.toPath(), code.getBytes());
    }
}
