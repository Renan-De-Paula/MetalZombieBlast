import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class BulletCollisionFix {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        String badCode = "iterator = this.bullets.iterator();\n" +
            "            java.util.Iterator<DamageText> itT = this.damageTexts.iterator();\n" +
            "        while (itT.hasNext()) {\n" +
            "                Bullet bullet = iterator.next();\n" +
            "                if (!objE.getBounds().intersects(bullet.getBounds())) continue;\n" +
            "                if (bullet.type.equals(\"fire\")) {\n" +
            "                    if (System.currentTimeMillis() - objE.lastFireDamageTime <= 300L) continue;\n" +
            "                    objE.health -= bullet.damage;\n" +
            "                    this.damageTexts.add(new DamageText(this, objE.x, objE.y, bullet.damage));\n" +
            "                    objE.lastFireDamageTime = System.currentTimeMillis();\n" +
            "                    continue;\n" +
            "                }\n" +
            "                objE.health -= bullet.damage;\n" +
            "                this.damageTexts.add(new DamageText(this, objE.x, objE.y, bullet.damage));\n" +
            "                itT.remove();\n" +
            "                break;\n" +
            "            }";
            
        String goodCode = "java.util.Iterator<Bullet> itB = this.bullets.iterator();\n" +
            "            while (itB.hasNext()) {\n" +
            "                Bullet bullet = itB.next();\n" +
            "                if (!objE.getBounds().intersects(bullet.getBounds())) continue;\n" +
            "                if (bullet.type.equals(\"fire\")) {\n" +
            "                    if (System.currentTimeMillis() - objE.lastFireDamageTime <= 300L) continue;\n" +
            "                    objE.health -= bullet.damage;\n" +
            "                    this.damageTexts.add(new DamageText(this, objE.x, objE.y, bullet.damage));\n" +
            "                    objE.lastFireDamageTime = System.currentTimeMillis();\n" +
            "                    continue;\n" +
            "                }\n" +
            "                objE.health -= bullet.damage;\n" +
            "                this.damageTexts.add(new DamageText(this, objE.x, objE.y, bullet.damage));\n" +
            "                itB.remove();\n" +
            "                break;\n" +
            "            }";
            
        code = code.replace(badCode, goodCode);

        Files.write(f.toPath(), code.getBytes());
    }
}
