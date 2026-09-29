import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ImageCheck {
    public static void main(String[] args) throws Exception {
        File f = new File("E:/projetos/game2/src/img/municao_shotgun_3_imagens_corrigida.png");
        BufferedImage img = ImageIO.read(f);
        if (img == null) {
            System.out.println("null");
        } else {
            System.out.println(img.getWidth() + " x " + img.getHeight());
        }
    }
}
