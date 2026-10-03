import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class GenerateAssets {
    public static void main(String[] args) throws Exception {
        File dir = new File("src/main/resources/assets/items");
        dir.mkdirs();

        // 1. MEDKIT
        BufferedImage medkit = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = medkit.createGraphics();
        g.setColor(new Color(200, 30, 30));
        g.fillRect(4, 8, 24, 18);
        g.setColor(new Color(150, 20, 20));
        g.drawRect(4, 8, 24, 18);
        g.setColor(Color.WHITE);
        g.fillRect(12, 11, 8, 12);
        g.fillRect(10, 15, 12, 4);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(12, 5, 8, 3); // handle
        g.setColor(new Color(200, 30, 30));
        g.fillRect(14, 6, 4, 2);
        g.dispose();
        ImageIO.write(medkit, "png", new File(dir, "medkit.png"));

        // 2. AMMO
        BufferedImage ammo = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        g = ammo.createGraphics();
        g.setColor(new Color(60, 100, 60)); // dark green
        g.fillRect(4, 12, 24, 16);
        g.setColor(new Color(40, 70, 40));
        g.drawRect(4, 12, 24, 16);
        // Bullets on top
        g.setColor(new Color(210, 180, 50)); // gold
        for(int i=0; i<3; i++) {
            int bx = 8 + i*7;
            g.fillRect(bx, 6, 4, 6);
            g.fillPolygon(new int[]{bx, bx+2, bx+4}, new int[]{6, 2, 6}, 3);
            g.setColor(new Color(150, 120, 30));
            g.drawRect(bx, 6, 4, 6);
            g.setColor(new Color(210, 180, 50));
        }
        g.setColor(new Color(30, 30, 30));
        g.fillRect(12, 16, 8, 8); // latch
        g.dispose();
        ImageIO.write(ammo, "png", new File(dir, "ammo.png"));

        // 3. MINE
        BufferedImage mine = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        g = mine.createGraphics();
        g.setColor(new Color(30, 50, 30));
        g.fillOval(4, 16, 24, 12);
        g.setColor(new Color(40, 70, 40));
        g.fillOval(6, 14, 20, 10);
        g.setColor(new Color(200, 30, 30));
        g.fillOval(12, 12, 8, 6); // red button
        g.setColor(new Color(250, 100, 100));
        g.fillOval(14, 13, 4, 3); // highlight
        g.dispose();
        ImageIO.write(mine, "png", new File(dir, "mine.png"));

        // 4. SCRAP
        BufferedImage scrap = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        g = scrap.createGraphics();
        g.setColor(new Color(150, 160, 170));
        g.fillOval(6, 6, 20, 20); // gear base
        g.setColor(new Color(100, 110, 120));
        g.drawOval(6, 6, 20, 20);
        // gear teeth
        for(int i=0; i<8; i++) {
            double angle = i * Math.PI / 4;
            int tx = 16 + (int)(Math.cos(angle) * 12);
            int ty = 16 + (int)(Math.sin(angle) * 12);
            g.fillRect(tx-2, ty-2, 4, 4);
        }
        // hole
        g.setComposite(java.awt.AlphaComposite.Clear);
        g.fillOval(12, 12, 8, 8);
        g.setComposite(java.awt.AlphaComposite.SrcOver);
        g.setColor(new Color(100, 110, 120));
        g.drawOval(12, 12, 8, 8);
        g.dispose();
        ImageIO.write(scrap, "png", new File(dir, "scrap.png"));

        System.out.println("Assets generated successfully!");
    }
}