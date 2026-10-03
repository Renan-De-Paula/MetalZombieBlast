import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class MakeGrenade {
    public static void main(String[] args) throws Exception {
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        
        // Transparent background
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, 32, 32);
        
        // Draw grenade body (dark green oval with pixels)
        g.setColor(new Color(34, 50, 34));
        g.fillOval(8, 12, 16, 18);
        g.setColor(new Color(50, 80, 50));
        g.fillOval(10, 14, 12, 14);
        g.setColor(new Color(70, 110, 70));
        g.fillOval(14, 16, 6, 8);
        
        // Draw grid lines
        g.setColor(new Color(25, 40, 25));
        g.drawLine(10, 20, 22, 20);
        g.drawLine(10, 24, 22, 24);
        g.drawLine(14, 14, 14, 28);
        g.drawLine(18, 14, 18, 28);

        // Draw top pin and ring
        g.setColor(new Color(100, 100, 100));
        g.fillRect(12, 8, 8, 4); // neck
        g.setColor(new Color(150, 150, 150));
        g.fillRect(14, 6, 4, 2); // pin base
        
        // Draw ring
        g.setColor(new Color(180, 180, 180));
        g.drawOval(16, 2, 8, 8);
        
        // Draw safety lever
        g.setColor(new Color(60, 60, 60));
        g.fillRect(8, 6, 4, 12);

        g.dispose();
        ImageIO.write(img, "png", new File("src/main/resources/assets/items/grenade.png"));
        System.out.println("Created grenade.png");
    }
}