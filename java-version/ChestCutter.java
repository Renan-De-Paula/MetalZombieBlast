import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ChestCutter {
    public static void main(String[] args) throws Exception {
        File outDir = new File("img");
        File inChest = new File("../src/img/bau_madeira_3_modelos_3_estados_topdown.png");
        if (inChest.exists()) {
            BufferedImage src = ImageIO.read(inChest);
            int cols = 3, rows = 3;
            int wP = src.getWidth() / cols;
            int hP = src.getHeight() / rows;
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    BufferedImage sub = src.getSubimage(c * wP, r * hP, wP, hP);
                    ImageIO.write(sub, "png", new File(outDir, "chest_m" + r + "_f" + c + ".png"));
                }
            }
            System.out.println("Sliced chests");
        }
    }
}
