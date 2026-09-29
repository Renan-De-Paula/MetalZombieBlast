import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class FixBrokenClasses {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));

        String solidWallRegex = "class SolidWall \\{.*?\\}\\s*class Window";
        Pattern pWall = Pattern.compile(solidWallRegex, Pattern.DOTALL);
        
        String newSolidWall = "class SolidWall {\n" +
            "        int x, y, width, height;\n" +
            "        SolidWall(int x, int y, int w, int h) { this.x=x; this.y=y; width=w; height=h; }\n" +
            "        java.awt.Rectangle getBounds() { return new java.awt.Rectangle(x, y, width, height); }\n" +
            "        void draw(java.awt.Graphics2D g) {\n" +
            "            boolean horiz = width > height;\n" +
            "            java.awt.image.BufferedImage img = horiz ? imgWallT[4] : imgWallT[5];\n" +
            "            if (img != null) g.drawImage(img, x, y, width, height, null);\n" +
            "            else { g.setColor(java.awt.Color.DARK_GRAY); g.fillRect(x, y, width, height); }\n" +
            "        }\n" +
            "    }\n    class Window";
        
        Matcher mWall = pWall.matcher(code);
        if (mWall.find()) {
            code = mWall.replaceFirst(newSolidWall);
        }

        String windowRegex = "class Window \\{.*?(?=class Core \\{)";
        Pattern pWindow = Pattern.compile(windowRegex, Pattern.DOTALL);
        
        String newWindow = "class Window {\n" +
            "        int x, y, width, height;\n" +
            "        int health, maxHealth;\n" +
            "        double centerX, centerY;\n" +
            "        boolean isHorizontal;\n" +
            "\n" +
            "        Window(int x, int y, int w, int h, boolean horiz) {\n" +
            "            this.x=x; this.y=y; width=w; height=h; isHorizontal = horiz;\n" +
            "            centerX = x + w/2.0; centerY = y + h/2.0;\n" +
            "            maxHealth = 100; health = 100;\n" +
            "        }\n" +
            "        java.awt.Rectangle getBounds() { return new java.awt.Rectangle(x, y, width, height); }\n" +
            "        void draw(java.awt.Graphics2D g) {\n" +
            "            g.setColor(new java.awt.Color(0,0,0, 100)); g.fillRect(x, y, width, height);\n" +
            "            if (health > 0) {\n" +
            "                java.awt.image.BufferedImage img = isHorizontal ? imgWindowT[4] : imgWindowT[5]; // Tier 3 Armored window\n" +
            "                if (img != null) g.drawImage(img, x, y, width, height, null);\n" +
            "                else { g.setColor(new java.awt.Color(139, 69, 19)); g.fillRect(x, y, width, height); }\n" +
            "                g.setColor(java.awt.Color.GREEN);\n" +
            "                if (isHorizontal) {\n" +
            "                    int hpL = (int)(width * ((double)health / maxHealth));\n" +
            "                    g.fillRect(x + (width - hpL)/2, y + height/2 - 2, hpL, 4);\n" +
            "                } else {\n" +
            "                    int hpL = (int)(height * ((double)health / maxHealth));\n" +
            "                    g.fillRect(x + width/2 - 2, y + (height - hpL)/2, 4, hpL);\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    }\n    ";
            
        Matcher mWin = pWindow.matcher(code);
        if (mWin.find()) {
            code = mWin.replaceFirst(newWindow);
        }

        Files.write(f.toPath(), code.getBytes());
    }
}
