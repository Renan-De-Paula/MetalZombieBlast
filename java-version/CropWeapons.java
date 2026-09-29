import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;

public class CropWeapons {
    public static void main(String[] args) throws Exception {
        String[] weapons = {"revolver", "glock", "ak47", "shotgun", "minigun", "rpg", "lancachamas", "lancagranadas", "smg", "miniuzi", "martelo", "faca"};
        File outDir = new File("img");

        for (String w : weapons) {
            for (int frame = 0; frame < 6; frame++) {
                File f = new File(outDir, "weapon_" + w + "_f" + frame + ".png");
                if (f.exists()) {
                    BufferedImage src = ImageIO.read(f);
                    // Crop bottom 20 pixels to remove foot bleed
                    if (src.getHeight() > 20) {
                        BufferedImage sub = src.getSubimage(0, 0, src.getWidth(), src.getHeight() - 20);
                        ImageIO.write(sub, "png", f);
                    }
                }
            }
        }
    }
}
