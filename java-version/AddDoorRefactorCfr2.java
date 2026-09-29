import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class AddDoorRefactorCfr2 {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        code = code.replace("private ArrayList<Window> windows;", "private ArrayList<Window> windows;\n    private ArrayList<Door> doors;");
        code = code.replace("this.windows = new ArrayList();", "this.windows = new ArrayList();\n        this.doors = new ArrayList();");
            
        Files.write(f.toPath(), code.getBytes());
    }
}
