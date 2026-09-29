import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class PlayerCollisionRefactor {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));

        // Change player.update call in update()
        code = code.replace("player.update(keys, solidWalls);", "player.update(keys, solidWalls, windows);");
        
        // Change player.update signature and body
        String oldUpdate = "        void update(boolean\\[\\] keys, ArrayList<SolidWall> walls) \\{\n" +
            "            double vx = 0, vy = 0;\n" +
            "            if \\(keys\\[KeyEvent.VK_LEFT\\] \\|\\| keys\\[KeyEvent.VK_A\\]\\) vx = -speed;\n" +
            "            if \\(keys\\[KeyEvent.VK_RIGHT\\] \\|\\| keys\\[KeyEvent.VK_D\\]\\) vx = speed;\n" +
            "            if \\(keys\\[KeyEvent.VK_UP\\] \\|\\| keys\\[KeyEvent.VK_W\\]\\) vy = -speed;\n" +
            "            if \\(keys\\[KeyEvent.VK_DOWN\\] \\|\\| keys\\[KeyEvent.VK_S\\]\\) vy = speed;\n" +
            "            if \\(vx \\!= 0 && vy \\!= 0\\) \\{ vx \\*= 0.7071; vy \\*= 0.7071; \\}\n" +
            "            \n" +
            "            double nextX = x \\+ vx \\* 0.016;\n" +
            "            if\\(nextX > 170 && nextX < 630\\) x = nextX;\n" +
            "            double nextY = y \\+ vy \\* 0.016;\n" +
            "            if\\(nextY > 170 && nextY < 430\\) y = nextY;\n" +
            "        \\}";
            
        String newUpdate = "        void update(boolean[] keys, java.util.ArrayList<SolidWall> walls, java.util.ArrayList<Window> windows) {\n" +
            "            double vx = 0, vy = 0;\n" +
            "            if (keys[java.awt.event.KeyEvent.VK_LEFT] || keys[java.awt.event.KeyEvent.VK_A]) vx = -speed;\n" +
            "            if (keys[java.awt.event.KeyEvent.VK_RIGHT] || keys[java.awt.event.KeyEvent.VK_D]) vx = speed;\n" +
            "            if (keys[java.awt.event.KeyEvent.VK_UP] || keys[java.awt.event.KeyEvent.VK_W]) vy = -speed;\n" +
            "            if (keys[java.awt.event.KeyEvent.VK_DOWN] || keys[java.awt.event.KeyEvent.VK_S]) vy = speed;\n" +
            "            if (vx != 0 && vy != 0) { vx *= 0.7071; vy *= 0.7071; }\n" +
            "            \n" +
            "            double nextX = x + vx * 0.016;\n" +
            "            java.awt.Rectangle boundsX = new java.awt.Rectangle((int)nextX - 15, (int)y - 15, 30, 30);\n" +
            "            boolean canMoveX = true;\n" +
            "            for (SolidWall w : walls) if (w.getBounds().intersects(boundsX)) canMoveX = false;\n" +
            "            for (Window w : windows) if (w.health > 0 && w.getBounds().intersects(boundsX)) canMoveX = false;\n" +
            "            if (canMoveX) x = nextX;\n" +
            "\n" +
            "            double nextY = y + vy * 0.016;\n" +
            "            java.awt.Rectangle boundsY = new java.awt.Rectangle((int)x - 15, (int)nextY - 15, 30, 30);\n" +
            "            boolean canMoveY = true;\n" +
            "            for (SolidWall w : walls) if (w.getBounds().intersects(boundsY)) canMoveY = false;\n" +
            "            for (Window w : windows) if (w.health > 0 && w.getBounds().intersects(boundsY)) canMoveY = false;\n" +
            "            if (canMoveY) y = nextY;\n" +
            "        }";
            
        code = code.replaceAll(oldUpdate, newUpdate);
        
        // Also old Update without escape
        String oldUpdate2 = "        void update(boolean[] keys, ArrayList<SolidWall> walls) {\n" +
            "            double vx = 0, vy = 0;\n" +
            "            if (keys[KeyEvent.VK_LEFT] || keys[KeyEvent.VK_A]) vx = -speed;\n" +
            "            if (keys[KeyEvent.VK_RIGHT] || keys[KeyEvent.VK_D]) vx = speed;\n" +
            "            if (keys[KeyEvent.VK_UP] || keys[KeyEvent.VK_W]) vy = -speed;\n" +
            "            if (keys[KeyEvent.VK_DOWN] || keys[KeyEvent.VK_S]) vy = speed;\n" +
            "            if (vx != 0 && vy != 0) { vx *= 0.7071; vy *= 0.7071; }\n" +
            "            \n" +
            "            double nextX = x + vx * 0.016;\n" +
            "            if(nextX > 170 && nextX < 630) x = nextX;\n" +
            "            double nextY = y + vy * 0.016;\n" +
            "            if(nextY > 170 && nextY < 430) y = nextY;\n" +
            "        }";
        code = code.replace(oldUpdate2, newUpdate);
        
        Files.write(f.toPath(), code.getBytes());
    }
}
