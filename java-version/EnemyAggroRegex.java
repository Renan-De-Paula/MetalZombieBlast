import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class EnemyAggroRegex {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));

        // Add lastPlayerShootTime to ZombieGame
        if (!code.contains("lastPlayerShootTime = 0")) {
            code = code.replace("boolean isMousePressed = false;", "boolean isMousePressed = false;\n    public static long lastPlayerShootTime = 0;");
            code = code.replace("player.lastShotTime = System.currentTimeMillis();", "player.lastShotTime = System.currentTimeMillis();\n        lastPlayerShootTime = System.currentTimeMillis();");
        }
        
        // Match from 'boolean insideHouse' up to 'angle = Math.atan2'
        String regex = "(boolean insideHouse = x > 150.*?)(?=\\s*angle = Math\\.atan2)";
        Pattern p = Pattern.compile(regex, Pattern.DOTALL);
        
        String replacement = "boolean insideHouse = x > 150 && x < 650 && y > 150 && y < 450;\n" +
            "            double playerDist = Math.hypot(player.x - x, player.y - y);\n" +
            "            boolean playerOutside = (player.x <= 150 || player.x >= 650 || player.y <= 150 || player.y >= 450);\n" +
            "            boolean heardGunshot = (System.currentTimeMillis() - lastPlayerShootTime < 2000) && playerDist < 1200;\n" +
            "            boolean aggroOnPlayer = type.equals(\"runner\") || (playerDist < 400 && playerOutside) || heardGunshot;\n" +
            "\n" +
            "            if (aggroOnPlayer || insideHouse) {\n" +
            "                targetX = player.x; targetY = player.y;\n" +
            "            } else {\n" +
            "                Window best = null; double bestDist = 999999;\n" +
            "                for (Window w : windows) {\n" +
            "                    double dist = Math.hypot(w.centerX - x, w.centerY - y);\n" +
            "                    if (dist < bestDist) { bestDist = dist; best = w; }\n" +
            "                }\n" +
            "                if (best != null) { targetX = best.centerX; targetY = best.centerY; }\n" +
            "                else { targetX = houseCore.x; targetY = houseCore.y; }\n" +
            "            }";
            
        Matcher m = p.matcher(code);
        if (m.find()) {
            code = m.replaceFirst(replacement);
        }

        Files.write(f.toPath(), code.getBytes());
    }
}
