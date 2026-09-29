import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class FixCfrLoops {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));

        // Enemies
        code = code.replace("object2 = this.enemies.iterator();", "java.util.Iterator<Enemy> itE = this.enemies.iterator();");
        code = code.replace("while (object2.hasNext()) {", "while (itE.hasNext()) {");
        code = code.replace("object = (Enemy)object2.next();", "Enemy object = itE.next();");
        code = code.replace("object2.remove();", "itE.remove();");

        // Drops
        code = code.replace("object = this.drops.iterator();", "java.util.Iterator<Drop> itD = this.drops.iterator();");
        code = code.replace("while (object.hasNext()) {", "while (itD.hasNext()) {");
        code = code.replace("iterator = (Drop)object.next();", "Drop iteratorD = itD.next();");
        code = code.replace("object.remove();", "itD.remove();");
        code = code.replace("((Drop)((Object)iterator))", "iteratorD");

        // DamageTexts
        code = code.replace("iterator = this.damageTexts.iterator();", "java.util.Iterator<DamageText> itT = this.damageTexts.iterator();");
        code = code.replace("while (iterator.hasNext()) {", "while (itT.hasNext()) {");
        code = code.replace("DamageText damageText = (DamageText)iterator.next();", "DamageText damageText = itT.next();");
        code = code.replace("iterator.remove();", "itT.remove();");
        
        // Let's also fix bullets while we are here if they were broken
        // "Iterator iterator2 = this.bullets.iterator();"
        // "while (iterator2.hasNext()) {"
        // "Object object2 = iterator2.next();"
        code = code.replace("Iterator iterator2 = this.bullets.iterator();", "java.util.Iterator<Bullet> iterator2 = this.bullets.iterator();");
        code = code.replace("Object object2 = iterator2.next();", "Bullet object2 = iterator2.next();");
        code = code.replace("((Bullet)object2)", "object2");
        code = code.replace("Iterator<Bullet> iterator = null;", "");
        code = code.replace("Object object = null;", "");
        code = code.replace("Object object2 = null;", "");

        Files.write(f.toPath(), code.getBytes());
    }
}
