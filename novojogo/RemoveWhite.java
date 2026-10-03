import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class RemoveWhite {
    public static void main(String[] args) throws Exception {
        File dir = new File("src/main/resources/assets/items");
        for (File f : dir.listFiles()) {
            if (f.getName().endsWith(".png")) {
                BufferedImage img = ImageIO.read(f);
                BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
                for (int y = 0; y < img.getHeight(); y++) {
                    for (int x = 0; x < img.getWidth(); x++) {
                        int rgb = img.getRGB(x, y);
                        Color c = new Color(rgb, true);
                        if (c.getRed() > 240 && c.getGreen() > 240 && c.getBlue() > 240) {
                            out.setRGB(x, y, 0x00FFFFFF);
                        } else {
                            out.setRGB(x, y, rgb);
                        }
                    }
                }
                ImageIO.write(out, "png", f);
                System.out.println("Processed " + f.getName());
            }
        }
    }
}