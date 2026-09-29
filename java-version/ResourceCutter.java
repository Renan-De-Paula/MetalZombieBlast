import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ResourceCutter {
    public static void main(String[] args) throws Exception {
        File outDir = new File("img");

        // 1. Cut Trees
        File inTrees = new File("../src/img/arvores_3_modelos_topdown_animacao_vento.png");
        if (inTrees.exists()) {
            BufferedImage src = ImageIO.read(inTrees);
            int cols = 4, rows = 3;
            int wP = src.getWidth() / cols;
            int hP = src.getHeight() / rows;
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    BufferedImage sub = src.getSubimage(c * wP, r * hP, wP, hP);
                    ImageIO.write(sub, "png", new File(outDir, "tree_model" + r + "_f" + c + ".png"));
                }
            }
            System.out.println("Sliced trees");
        }

        // 2. Cut Rocks (Simple)
        File inRocks = new File("../src/img/pedras_3_modelos_topdown.png");
        if (inRocks.exists()) {
            BufferedImage src = ImageIO.read(inRocks);
            int cols = 3, rows = 1;
            int wP = src.getWidth() / cols;
            int hP = src.getHeight() / rows;
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    BufferedImage sub = src.getSubimage(c * wP, r * hP, wP, hP);
                    ImageIO.write(sub, "png", new File(outDir, "rock_model" + c + ".png"));
                }
            }
            System.out.println("Sliced rocks");
        }
    }
}
