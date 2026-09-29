import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class SpriteGenerator {
    public static void main(String[] args) {
        File dir = new File("img");
        if (!dir.exists()) dir.mkdirs();

        try {
            generateZombieFrames("zombie_basic", 30, new Color(34, 139, 34), new Color(0, 0, 150), 4);
            generateZombieFrames("zombie_runner", 25, new Color(200, 50, 50), new Color(50, 50, 50), 3);
            generateZombieFrames("zombie_boss", 45, new Color(100, 100, 100), new Color(20, 20, 20), 8);
            generateZombieFrames("zombie_superboss", 65, new Color(138, 43, 226), new Color(0, 0, 0), 12);
            
            System.out.println("Novos Sprites Gerados!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateZombieFrames(String prefix, int size, Color skin, Color clothes, int shoulderWidth) throws Exception {
        for (int frame = 0; frame < 3; frame++) {
            BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int center = size / 2;
            int headSize = size / 2 + 2;
            int armWidth = shoulderWidth;
            int armLength = size / 2;
            
            int rightArmY = size/2 - armWidth; 
            int leftArmY = size/2 + armWidth;
            
            int rightArmX = center; 
            int leftArmX = center; 
            
            if (frame == 1) { rightArmX += size/6; leftArmX -= size/6; } 
            else if (frame == 2) { leftArmX += size/6; rightArmX -= size/6; }

            // Braço Direito (Cima)
            g.setColor(skin);
            g.fillRoundRect(rightArmX, 2, armLength, armWidth, 2, 2);
            // Manga direita
            g.setColor(clothes);
            g.fillRoundRect(center - 2, 2, armLength/2, armWidth, 2, 2);
            
            // Braço Esquerdo (Baixo)
            g.setColor(skin);
            g.fillRoundRect(leftArmX, size - armWidth - 2, armLength, armWidth, 2, 2);
            // Manga esquerda
            g.setColor(clothes);
            g.fillRoundRect(center - 2, size - armWidth - 2, armLength/2, armWidth, 2, 2);
            
            // Corpo (Ombros)
            g.setColor(clothes);
            g.fillOval(center - size/4, 2, size/2, size - 4);
            
            // Cabeça
            g.setColor(skin);
            g.fillOval(center - headSize/2, center - headSize/2, headSize, headSize);
            
            // Sangue/Detalhes na cabeça
            g.setColor(new Color(150, 0, 0));
            g.fillOval(center, center - headSize/3, headSize/3, headSize/3);
            
            // Contornos
            g.setColor(new Color(0,0,0, 100));
            g.drawOval(center - headSize/2, center - headSize/2, headSize, headSize);
            
            g.dispose();
            ImageIO.write(img, "png", new File("img/" + prefix + "_" + frame + ".png"));
        }
    }
}
