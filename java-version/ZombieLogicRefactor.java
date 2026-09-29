import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class ZombieLogicRefactor {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));

        String find = "            if (en.health <= 0) {\n" +
            "                killCount++;\n" +
            "                generateDrops(en.x, en.y, en.expValue, en.type);\n" +
            "                eit.remove();\n" +
            "            }";
            
        String replace = "            if (en.health <= 0 && !en.dead) {\n" +
            "                killCount++;\n" +
            "                generateDrops(en.x, en.y, en.expValue, en.type);\n" +
            "                en.changeState(\"death\");\n" +
            "                en.dead = true;\n" +
            "            }\n" +
            "            if (en.dead && System.currentTimeMillis() - en.deathTime > 2000) {\n" +
            "                eit.remove();\n" +
            "            }";
            
        code = code.replace(find, replace);
        Files.write(f.toPath(), code.getBytes());
    }
}
