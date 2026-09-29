import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class HammerCutter {
    public static void main(String[] args) throws Exception {
        File outDir = new File("img");
        File in = new File("../src/img/personagem_martelo_4_movimentos_realista.png");
        if (in.exists()) {
            BufferedImage src = ImageIO.read(in);
            // Assuming 4 frames in 1 row? Or 2x2? "4 movimentos realista"
            // The user didn't specify grid. If width > height it's probably 4x1. 
            // Let's assume 4x1 or 2x2. Actually usually they are horizontal.
            // Wait, we can just load the original image directly for simplicity if it fails, but slicing is safer.
            // Let's slice it 4x1.
            int cols = src.getWidth() / src.getHeight() >= 3 ? 4 : 2;
            int rows = cols == 4 ? 1 : 2;
            int wP = src.getWidth() / cols;
            int hP = src.getHeight() / rows;
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    int frame = r * cols + c;
                    BufferedImage sub = src.getSubimage(c * wP, r * hP, wP, hP);
                    ImageIO.write(sub, "png", new File(outDir, "weapon_martelo_f" + frame + ".png"));
                }
            }
        }
    }
}
