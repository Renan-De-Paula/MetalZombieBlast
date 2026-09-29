import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class LoadAmmoSprites {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        String initFrame = "for (int i = 0; i < 4; ++i) {";
        String loadAmmo = "try {\n" +
            "            java.awt.image.BufferedImage sheet1 = javax.imageio.ImageIO.read(new File(\"src/img/municao_leve_3_imagens.png\"));\n" +
            "            if (sheet1 != null) { int cw = sheet1.getWidth()/3; int ch = sheet1.getHeight(); for(int i=0;i<3;i++) imgAmmoLight[i] = sheet1.getSubimage(i*cw,0,cw,ch); }\n" +
            "            java.awt.image.BufferedImage sheet2 = javax.imageio.ImageIO.read(new File(\"src/img/municao_media_3_imagens_corrigida.png\"));\n" +
            "            if (sheet2 != null) { int cw = sheet2.getWidth()/3; int ch = sheet2.getHeight(); for(int i=0;i<3;i++) imgAmmoMedium[i] = sheet2.getSubimage(i*cw,0,cw,ch); }\n" +
            "            java.awt.image.BufferedImage sheet3 = javax.imageio.ImageIO.read(new File(\"src/img/municao_shotgun_3_imagens_corrigida.png\"));\n" +
            "            if (sheet3 != null) { int cw = sheet3.getWidth()/3; int ch = sheet3.getHeight(); for(int i=0;i<3;i++) imgAmmoShotgun[i] = sheet3.getSubimage(i*cw,0,cw,ch); }\n" +
            "            java.awt.image.BufferedImage sheet4 = javax.imageio.ImageIO.read(new File(\"src/img/municao_rpg_4_imagens_corrigida.png\"));\n" +
            "            if (sheet4 != null) { int cw = sheet4.getWidth()/4; int ch = sheet4.getHeight(); for(int i=0;i<4;i++) imgAmmoRPG[i] = sheet4.getSubimage(i*cw,0,cw,ch); }\n" +
            "            java.awt.image.BufferedImage sheet5 = javax.imageio.ImageIO.read(new File(\"src/img/municao_lancagranadas_4_imagens_corrigida.png\"));\n" +
            "            if (sheet5 != null) { int cw = sheet5.getWidth()/4; int ch = sheet5.getHeight(); for(int i=0;i<4;i++) imgAmmoGrenade[i] = sheet5.getSubimage(i*cw,0,cw,ch); }\n" +
            "        } catch (Exception e) {}\n" +
            "        for (int i = 0; i < 4; ++i) {";
        code = code.replace(initFrame, loadAmmo);
        
        Files.write(f.toPath(), code.getBytes());
    }
}
