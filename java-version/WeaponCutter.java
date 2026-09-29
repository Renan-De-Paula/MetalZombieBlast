import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class WeaponCutter {
    public static void main(String[] args) throws Exception {
        String[] weapons = {"revolver", "glock", "ak47", "shotgun", "minigun", "rpg", "lancachamas", "lancagranadas", "smg", "miniuzi"};
        File outDir = new File("img");
        if (!outDir.exists()) outDir.mkdir();

        for (String w : weapons) {
            File in = new File("../src/img/personagem_" + w + "_6_movimentos.png");
            if (!in.exists()) {
                System.out.println("Missing " + in.getName());
                continue;
            }
            BufferedImage src = ImageIO.read(in);
            int cols = 3, rows = 2;
            int wP = src.getWidth() / cols;
            int hP = src.getHeight() / rows;
            
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    int frame = r * cols + c;
                    BufferedImage sub = src.getSubimage(c * wP, r * hP, wP, hP);
                    ImageIO.write(sub, "png", new File(outDir, "weapon_" + w + "_f" + frame + ".png"));
                }
            }
            System.out.println("Sliced " + w);
        }
    }
}
