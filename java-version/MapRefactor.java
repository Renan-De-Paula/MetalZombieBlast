import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class MapRefactor {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        // 1. Add fields
        String fields = "private ArrayList<DamageText> damageTexts;";
        String newFields = fields + "\n    public boolean[][] exploredMap = new boolean[100][100];\n    public boolean isMapOpen = false;";
        code = code.replace(fields, newFields);
        
        // 2. Add Key Bindings
        String mKey = "if (keyEvent.getKeyCode() == 27) {";
        String newMKey = "if (keyEvent.getKeyCode() == 77) { this.isMapOpen = !this.isMapOpen; }\n        if (keyEvent.getKeyCode() == 27) {";
        code = code.replace(mKey, newMKey);
        
        // 3. Update Map logic in Player.update()
        String updateVars = "double d3 = 0.0;\n            double d4 = 0.0;";
        String newUpdateVars = updateVars + "\n" +
            "            int pCellX = (int) Math.floor((this.x + 10000) / 200.0);\n" +
            "            int pCellY = (int) Math.floor((this.y + 10000) / 200.0);\n" +
            "            for (int cx = pCellX - 4; cx <= pCellX + 4; cx++) {\n" +
            "                for (int cy = pCellY - 4; cy <= pCellY + 4; cy++) {\n" +
            "                    if (cx >= 0 && cx < 100 && cy >= 0 && cy < 100) {\n" +
            "                        ZombieGame.this.exploredMap[cx][cy] = true;\n" +
            "                    }\n" +
            "                }\n" +
            "            }";
        code = code.replace(updateVars, newUpdateVars);
        
        // 4. Add drawMap function
        String drawMapFunc = "private void drawMap(java.awt.Graphics2D g, int sx, int sy, int size, double scale, boolean isCircular) {\n" +
            "        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();\n" +
            "        if (isCircular) {\n" +
            "            g2.setClip(new java.awt.geom.Ellipse2D.Float(sx, sy, size, size));\n" +
            "        } else {\n" +
            "            g2.setClip(sx, sy, size, size);\n" +
            "        }\n" +
            "        g2.setColor(new java.awt.Color(20, 20, 20));\n" +
            "        g2.fillRect(sx, sy, size, size);\n" +
            "        g2.translate(sx + size / 2, sy + size / 2);\n" +
            "        g2.setColor(new java.awt.Color(34, 139, 34));\n" +
            "        for (int cx = 0; cx < 100; cx++) {\n" +
            "            for (int cy = 0; cy < 100; cy++) {\n" +
            "                if (this.exploredMap[cx][cy]) {\n" +
            "                    int worldX = cx * 200 - 10000;\n" +
            "                    int worldY = cy * 200 - 10000;\n" +
            "                    int drawX = (int) ((worldX - this.player.x) * scale);\n" +
            "                    int drawY = (int) ((worldY - this.player.y) * scale);\n" +
            "                    int drawW = (int) (200 * scale) + 1;\n" +
            "                    g2.fillRect(drawX, drawY, drawW, drawW);\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "        int hx = (int) ((400 - this.player.x) * scale);\n" +
            "        int hy = (int) ((300 - this.player.y) * scale);\n" +
            "        g2.setColor(java.awt.Color.BLUE);\n" +
            "        g2.fillRect(hx - 5, hy - 5, 10, 10);\n" +
            "        for (ResourceNode r : this.resources) {\n" +
            "            int cx = (int) Math.floor((r.x + 10000) / 200.0);\n" +
            "            int cy = (int) Math.floor((r.y + 10000) / 200.0);\n" +
            "            if (cx >= 0 && cx < 100 && cy >= 0 && cy < 100 && this.exploredMap[cx][cy]) {\n" +
            "                int rx = (int) ((r.x - this.player.x) * scale);\n" +
            "                int ry = (int) ((r.y - this.player.y) * scale);\n" +
            "                g2.setColor(r.type == 0 ? new java.awt.Color(0, 100, 0) : java.awt.Color.GRAY);\n" +
            "                g2.fillOval(rx - 2, ry - 2, 4, 4);\n" +
            "            }\n" +
            "        }\n" +
            "        for (LootChest c : this.chests) {\n" +
            "            if (c.state != 2) {\n" +
            "                int cx = (int) Math.floor((c.x + 10000) / 200.0);\n" +
            "                int cy = (int) Math.floor((c.y + 10000) / 200.0);\n" +
            "                if (cx >= 0 && cx < 100 && cy >= 0 && cy < 100 && this.exploredMap[cx][cy]) {\n" +
            "                    int rx = (int) ((c.x - this.player.x) * scale);\n" +
            "                    int ry = (int) ((c.y - this.player.y) * scale);\n" +
            "                    g2.setColor(java.awt.Color.YELLOW);\n" +
            "                    g2.fillRect(rx - 3, ry - 3, 6, 6);\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "        g2.setColor(java.awt.Color.RED);\n" +
            "        g2.fillOval(-3, -3, 6, 6);\n" +
            "        g2.dispose();\n" +
            "        if (isCircular) {\n" +
            "            g.setColor(new java.awt.Color(101, 67, 33));\n" +
            "            g.setStroke(new java.awt.BasicStroke(6));\n" +
            "            g.drawOval(sx, sy, size, size);\n" +
            "            g.setColor(java.awt.Color.WHITE);\n" +
            "            g.setFont(new java.awt.Font(\"Arial\", 1, 14));\n" +
            "            g.drawString(\"N\", sx + size / 2 - 5, sy + 15);\n" +
            "            g.drawString(\"S\", sx + size / 2 - 5, sy + size - 5);\n" +
            "            g.drawString(\"W\", sx + 5, sy + size / 2 + 5);\n" +
            "            g.drawString(\"E\", sx + size - 15, sy + size / 2 + 5);\n" +
            "            double distHome = Math.hypot(400 - this.player.x, 300 - this.player.y);\n" +
            "            if (distHome * scale > size / 2) {\n" +
            "                double angle = Math.atan2(300 - this.player.y, 400 - this.player.x);\n" +
            "                int indX = sx + size / 2 + (int) (Math.cos(angle) * (size / 2 - 10));\n" +
            "                int indY = sy + size / 2 + (int) (Math.sin(angle) * (size / 2 - 10));\n" +
            "                g.setColor(java.awt.Color.CYAN);\n" +
            "                g.fillOval(indX - 4, indY - 4, 8, 8);\n" +
            "                g.setFont(new java.awt.Font(\"Arial\", 0, 9));\n" +
            "                g.drawString(\"BASE\", indX - 12, indY + 12);\n" +
            "            }\n" +
            "            g.setColor(java.awt.Color.WHITE);\n" +
            "            g.setFont(new java.awt.Font(\"Arial\", 0, 12));\n" +
            "            g.drawString(\"X: \" + (int) this.player.x + \" Y: \" + (int) this.player.y, sx + size / 2 - 35, sy + size + 20);\n" +
            "        } else {\n" +
            "            g.setColor(java.awt.Color.WHITE);\n" +
            "            g.setStroke(new java.awt.BasicStroke(4));\n" +
            "            g.drawRect(sx, sy, size, size);\n" +
            "            g.setFont(new java.awt.Font(\"Arial\", 1, 18));\n" +
            "            g.drawString(\"MAPA MUNDI (M para fechar)\", sx + 10, sy - 10);\n" +
            "        }\n" +
            "    }\n";
        String drawHook = "    private void drawUI(Graphics2D graphics2D) {";
        code = code.replace(drawHook, drawMapFunc + "\n" + drawHook);
        
        // 5. Inject draw calls
        String paintEnd = "if (this.isRewardMenu) {";
        String newPaintEnd = "if (this.isMapOpen) {\n" +
            "                this.drawMap(graphics2D, 150, 50, 500, 0.03, false);\n" +
            "            } else {\n" +
            "                this.drawMap(graphics2D, 620, 20, 160, 0.1, true);\n" +
            "            }\n            " + paintEnd;
        code = code.replace(paintEnd, newPaintEnd);
        
        Files.write(f.toPath(), code.getBytes());
    }
}
