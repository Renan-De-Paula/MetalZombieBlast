import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.Random;

public class AssetGenerator {
    public static void main(String[] args) {
        File dir = new File("img");
        if (!dir.exists()) dir.mkdirs();

        try {
            // 1. bg_grass.png
            BufferedImage grass = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2g = grass.createGraphics();
            g2g.setColor(new Color(34, 139, 34));
            g2g.fillRect(0, 0, 64, 64);
            Random r = new Random(42);
            for (int i = 0; i < 40; i++) {
                g2g.setColor(new Color(0, 100 + r.nextInt(50), 0));
                int x = r.nextInt(64);
                int y = r.nextInt(64);
                g2g.fillRect(x, y, 2, 4 + r.nextInt(4));
            }
            g2g.dispose();
            ImageIO.write(grass, "png", new File("img/bg_grass.png"));

            // 2. floor_wood.png
            BufferedImage wood = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2w = wood.createGraphics();
            g2w.setColor(new Color(139, 69, 19));
            g2w.fillRect(0, 0, 64, 64);
            g2w.setColor(new Color(101, 67, 33));
            for (int i = 0; i < 64; i += 16) {
                g2w.drawLine(i, 0, i, 64);
                if (r.nextBoolean()) g2w.drawLine(i, r.nextInt(64), i+16, r.nextInt(64));
            }
            g2w.dispose();
            ImageIO.write(wood, "png", new File("img/floor_wood.png"));

            // 3. wall.png
            BufferedImage wall = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2wall = wall.createGraphics();
            g2wall.setColor(new Color(105, 50, 20));
            g2wall.fillRect(0, 0, 64, 64);
            g2wall.setColor(Color.BLACK);
            g2wall.drawRect(0, 0, 63, 63);
            g2wall.dispose();
            ImageIO.write(wall, "png", new File("img/wall.png"));

            // 4. zombie_basic.png
            BufferedImage zBasic = createZombie(30, new Color(0, 100, 0), new Color(0, 50, 0));
            ImageIO.write(zBasic, "png", new File("img/zombie_basic.png"));

            // 5. zombie_runner.png
            BufferedImage zRunner = createZombie(25, new Color(200, 50, 50), new Color(100, 0, 0));
            ImageIO.write(zRunner, "png", new File("img/zombie_runner.png"));

            // 6. zombie_brute.png
            BufferedImage zBrute = createZombie(45, new Color(100, 100, 100), new Color(50, 50, 50));
            ImageIO.write(zBrute, "png", new File("img/zombie_brute.png"));

            // 7. item_exp.png
            BufferedImage exp = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Graphics2D ge = exp.createGraphics();
            ge.setColor(Color.CYAN);
            ge.fillPolygon(new int[]{8, 14, 8, 2}, new int[]{2, 8, 14, 8}, 4);
            ge.setColor(Color.WHITE);
            ge.fillPolygon(new int[]{8, 11, 8, 5}, new int[]{4, 8, 12, 8}, 4);
            ge.dispose();
            ImageIO.write(exp, "png", new File("img/item_exp.png"));

            // 8. item_coin.png
            BufferedImage coin = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gc = coin.createGraphics();
            gc.setColor(Color.YELLOW);
            gc.fillOval(2, 2, 12, 12);
            gc.setColor(new Color(200, 150, 0));
            gc.drawOval(2, 2, 12, 12);
            gc.setColor(Color.ORANGE);
            gc.drawOval(5, 5, 6, 6);
            gc.dispose();
            ImageIO.write(coin, "png", new File("img/item_coin.png"));

            // 9. item_material.png
            BufferedImage mat = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gm = mat.createGraphics();
            gm.setColor(new Color(150, 100, 50));
            gm.fillRect(2, 6, 12, 4);
            gm.setColor(Color.BLACK);
            gm.drawRect(2, 6, 12, 4);
            gm.fillRect(3, 7, 1, 1);
            gm.fillRect(12, 7, 1, 1);
            gm.dispose();
            ImageIO.write(mat, "png", new File("img/item_material.png"));
            
            System.out.println("Imagens geradas com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage createZombie(int size, Color skin, Color outline) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        
        g.setColor(skin);
        g.fillRoundRect(size/2, 2, size/2, size/4, 4, 4);
        g.fillRoundRect(size/2, size - size/4 - 2, size/2, size/4, 4, 4);
        g.setColor(outline);
        g.drawRoundRect(size/2, 2, size/2, size/4, 4, 4);
        g.drawRoundRect(size/2, size - size/4 - 2, size/2, size/4, 4, 4);
        
        g.setColor(skin);
        g.fillOval(2, 2, size-4, size-4);
        g.setColor(outline);
        g.drawOval(2, 2, size-4, size-4);
        
        g.dispose();
        return img;
    }
}
