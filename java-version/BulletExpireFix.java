import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class BulletExpireFix {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        String badExpire = "if (!(object2.isExpired() || object2.x < -100.0 || object2.x > 900.0 || object2.y < -100.0) && !(object2.y > 700.0)) continue;\n" +
            "            iterator2.remove();";
        String newExpire = "if (!object2.isExpired()) continue;\n" +
            "            if (object2.weaponType != null && (object2.weaponType.equals(\"rpg\") || object2.weaponType.equals(\"lancagranadas\")) && !object2.isExploding) {\n" +
            "                object2.isExploding = true;\n" +
            "                object2.explodeTime = System.currentTimeMillis();\n" +
            "                for (Enemy aoeE : ZombieGame.this.enemies) {\n" +
            "                    if (!aoeE.dead && Math.hypot(aoeE.x - object2.x, aoeE.y - object2.y) < 150.0) {\n" +
            "                        aoeE.life -= object2.damage;\n" +
            "                        ZombieGame.this.damageTexts.add(new DamageText(ZombieGame.this, aoeE.x, aoeE.y, object2.damage));\n" +
            "                        if (aoeE.life <= 0) { aoeE.dead = true; aoeE.deathTime = System.currentTimeMillis(); }\n" +
            "                    }\n" +
            "                }\n" +
            "                continue;\n" +
            "            }\n" +
            "            iterator2.remove();";
        code = code.replace(badExpire, newExpire);

        Files.write(f.toPath(), code.getBytes());
    }
}
