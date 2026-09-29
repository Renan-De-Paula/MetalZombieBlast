import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageInfo {
    public static void main(String[] args) {
        try {
            File[] files = {
                new File("img/zumbis_sprite_sheet_classes_movimentos_topdown.png"),
                new File("img/personagem_sprite_sheet_3_movimentos_por_arma.png")
            };
            for (File f : files) {
                if (f.exists()) {
                    BufferedImage img = ImageIO.read(f);
                    System.out.println(f.getName() + " -> " + img.getWidth() + "x" + img.getHeight());
                } else {
                    System.out.println("Nao encontrou " + f.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
