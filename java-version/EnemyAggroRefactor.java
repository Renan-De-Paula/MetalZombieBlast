import java.io.File;
import java.nio.file.Files;

public class EnemyAggroRefactor {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));

        // Add lastPlayerShootTime to ZombieGame
        code = code.replace("boolean isMousePressed = false;", "boolean isMousePressed = false;\n    public static long lastPlayerShootTime = 0;");
        
        // Update it when shooting
        code = code.replace("player.lastShotTime = System.currentTimeMillis();", "player.lastShotTime = System.currentTimeMillis();\n        lastPlayerShootTime = System.currentTimeMillis();");
        
        // Refactor Enemy update method
        String oldUpdateStart = "            double targetX = 0, targetY = 0;\n" +
            "            boolean insideHouse = x > 150 && x < 650 && y > 150 && y < 450;\n" +
            "            if (!insideHouse && !type.equals(\"runner\")) {\n" +
            "                Window best = null; double bestDist = 999999;\n" +
            "                for (Window w : windows) {\n" +
            "                    double dist = Math.hypot(w.centerX - x, w.centerY - y);\n" +
            "                    if (dist < bestDist) { bestDist = dist; best = w; }\n" +
            "                }\n" +
            "                if (best != null) { targetX = best.centerX; targetY = best.centerY; }\n" +
            "                if (Math.hypot(player.x - x, player.y - y) < bestDist && x > 150 && x < 650 && y > 150 && y < 450) {\n" +
            "                    targetX = player.x; targetY = player.y;\n" +
            "                }\n" +
            "            } else {\n" +
            "                targetX = player.x; targetY = player.y;\n" +
            "            }";
            
        String newUpdateStart = "            double targetX = 0, targetY = 0;\n" +
            "            boolean insideHouse = x > 150 && x < 650 && y > 150 && y < 450;\n" +
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
            "            }";
            
        code = code.replace(oldUpdateStart, newUpdateStart);

        Files.write(f.toPath(), code.getBytes());
    }
}
