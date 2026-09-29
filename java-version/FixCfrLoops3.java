import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class FixCfrLoops3 {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));

        code = code.replace("object2 = iterator2.next();", "Bullet object2 = iterator2.next();");
        code = code.replace("window2 = object;", "window2 = objW;");

        code = code.replace("java.util.Iterator<DamageText> itT = this.damageTexts.iterator();", "");
        code = code.replace("while (itT.hasNext()) {", "java.util.Iterator<DamageText> itT = this.damageTexts.iterator();\n        while (itT.hasNext()) {");
        
        Files.write(f.toPath(), code.getBytes());
    }
}
