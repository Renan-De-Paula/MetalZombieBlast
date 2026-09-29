import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class RemoveInit {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        String r1 = "imgParedeTrue = ImageIO.read(getClass().getResourceAsStream(\"/img/parede_true.png\"));";
        String r2 = "imgJanelaTrue = ImageIO.read(getClass().getResourceAsStream(\"/img/janela_true.png\"));";
        
        code = code.replace(r1, "");
        code = code.replace(r2, "");
        
        Files.write(f.toPath(), code.getBytes());
    }
}
