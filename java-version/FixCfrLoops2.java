import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class FixCfrLoops2 {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));

        code = code.replace("Object object;", "");
        code = code.replace("Object object2;", "");
        code = code.replace("Object object2 =", "Bullet object2 =");
        code = code.replace("Enemy object =", "Enemy objE =");
        code = code.replace("((Enemy)object)", "objE");
        
        // Window object
        code = code.replace("Window object : arrayList", "Window objW : arrayList");
        code = code.replace("object.centerX", "objW.centerX");
        code = code.replace("object.centerY", "objW.centerY");

        // "while (itT.hasNext())" was not working? Let's check bullet loops again
        // Actually itT was damageTexts!
        // The error said `itT` is not defined. Wait, maybe the replace for damage texts failed?
        code = code.replace("iterator = this.damageTexts.iterator();", "java.util.Iterator<DamageText> itT = this.damageTexts.iterator();");
        code = code.replace("while (iterator.hasNext()) {", "while (itT.hasNext()) {");
        code = code.replace("iterator.remove();", "itT.remove();");

        Files.write(f.toPath(), code.getBytes());
    }
}
