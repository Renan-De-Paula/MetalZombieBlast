import java.io.File;
import java.nio.file.Files;

public class MapGraphicsRefactor {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));

        code = code.replace("private static BufferedImage imgParedeTrue;", "public static java.awt.image.BufferedImage[] imgWallT = new java.awt.image.BufferedImage[6];");
        code = code.replace("private static BufferedImage imgJanelaTrue;", "public static java.awt.image.BufferedImage[] imgWindowT = new java.awt.image.BufferedImage[6];");

        String loadReplace = "for (int i=1; i<=3; i++) {\n" +
            "                    try { imgWallT[(i-1)*2] = javax.imageio.ImageIO.read(new java.io.File(\"img/wall_t\"+i+\"_h.png\")); } catch(Exception ex){}\n" +
            "                    try { imgWallT[(i-1)*2+1] = javax.imageio.ImageIO.read(new java.io.File(\"img/wall_t\"+i+\"_v.png\")); } catch(Exception ex){}\n" +
            "                    try { imgWindowT[(i-1)*2] = javax.imageio.ImageIO.read(new java.io.File(\"img/window_t\"+i+\"_h.png\")); } catch(Exception ex){}\n" +
            "                    try { imgWindowT[(i-1)*2+1] = javax.imageio.ImageIO.read(new java.io.File(\"img/window_t\"+i+\"_v.png\")); } catch(Exception ex){}\n" +
            "                }";
        code = code.replace("imgParedeTrue = ImageIO.read(new java.io.File(\"img/parede_true.png\"));", loadReplace);
        code = code.replace("imgJanelaTrue = ImageIO.read(new java.io.File(\"img/janela_true.png\"));", "");
        
        String wallDraw = "        void draw(java.awt.Graphics2D g) {\n" +
            "            boolean horiz = width > height;\n" +
            "            java.awt.image.BufferedImage img = horiz ? imgWallT[4] : imgWallT[5]; // Tier 3 Concrete\n" +
            "            if (img != null) g.drawImage(img, x, y, width, height, null);\n" +
            "            else { g.setColor(java.awt.Color.DARK_GRAY); g.fillRect(x, y, width, height); }\n" +
            "        }";
        code = code.replaceAll("(?s)void draw\\(java\\.awt\\.Graphics2D g\\) \\{.*?if \\(imgParedeTrue \\!= null.*?\\}", wallDraw);
        code = code.replaceAll("(?s)void draw\\(Graphics2D g\\) \\{.*?if \\(imgParedeTrue \\!= null.*?\\}", wallDraw);

        String windowDraw = "        void draw(java.awt.Graphics2D g) {\n" +
            "            g.setColor(new java.awt.Color(0,0,0, 100)); g.fillRect(x, y, width, height);\n" +
            "            if (health > 0) {\n" +
            "                java.awt.image.BufferedImage img = isHorizontal ? imgWindowT[0] : imgWindowT[1]; // Tier 1 Wood\n" +
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
            "        }";
        code = code.replaceAll("(?s)void draw\\(java\\.awt\\.Graphics2D g\\) \\{.*?g\\.setColor\\(new java\\.awt\\.Color\\(0,0,0, 100\\)\\);.*?if \\(imgJanelaTrue \\!= null.*?\\}", windowDraw);
        code = code.replaceAll("(?s)void draw\\(Graphics2D g\\) \\{.*?g\\.setColor\\(new Color\\(0,0,0, 100\\)\\);.*?if \\(imgJanelaTrue \\!= null.*?\\}", windowDraw);

        Files.write(f.toPath(), code.getBytes());
    }
}
