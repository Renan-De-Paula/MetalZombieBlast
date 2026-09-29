import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class AddGlock {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        String dropStr = "this.drops.add(new Drop(this, 400.0, 350.0, this.comuns[1]));";
        String newDropStr = "this.drops.add(new Drop(this, 420.0, 370.0, this.comuns[0]));\n            " + dropStr;
        
        code = code.replace(dropStr, newDropStr);

        Files.write(f.toPath(), code.getBytes());
    }
}
