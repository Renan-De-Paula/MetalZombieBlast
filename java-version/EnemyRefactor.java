import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class EnemyRefactor {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));

        String regex = "class Enemy \\{.*?(?=\\n\\s*class Bullet \\{)";
        Pattern p = Pattern.compile(regex, Pattern.DOTALL);
        Matcher m = p.matcher(code);

        String newEnemy = "class Enemy {\n" +
            "        double x, y, speed;\n" +
            "        int health, expValue, size;\n" +
            "        String type;\n" +
            "        Color color;\n" +
            "        long lastFireDamageTime = 0;\n" +
            "        double angle = 0;\n" +
            "        int frameCount = 0;\n" +
            "        long lastFrameChange = 0;\n" +
            "        String state = \"walk\";\n" +
            "        boolean dead = false;\n" +
            "        long stateStartTime = 0;\n" +
            "        long attackCooldown = 0;\n" +
            "        long deathTime = 0;\n" +
            "\n" +
            "        Enemy(double x, double y, String type, int wave) {\n" +
            "            this.x = x; this.y = y; this.type = type;\n" +
            "            int hpMult = 1 + (wave/3);\n" +
            "            if (type.equals(\"basic\")) { health = 10 * hpMult; speed = 50; expValue = 1; color = new java.awt.Color(0, 100, 0); size = 30; }\n" +
            "            else if (type.equals(\"runner\")) { health = 25 * hpMult; speed = 100; expValue = 2; color = new java.awt.Color(200, 50, 50); size = 25; }\n" +
            "            else if (type.equals(\"boss\")) { health = 100 * hpMult; speed = 35; expValue = 10; color = new java.awt.Color(100, 100, 100); size = 60; }\n" +
            "            else if (type.equals(\"superboss\")) { health = 500 * hpMult; speed = 25; expValue = 50; color = new java.awt.Color(138, 43, 226); size = 80; }\n" +
            "        }\n" +
            "\n" +
            "        void changeState(String newState) {\n" +
            "            if (this.state.equals(\"death\")) return;\n" +
            "            if (!this.state.equals(newState)) {\n" +
            "                this.state = newState;\n" +
            "                this.stateStartTime = System.currentTimeMillis();\n" +
            "                this.lastFrameChange = System.currentTimeMillis();\n" +
            "                if (newState.equals(\"idle\")) frameCount = 0;\n" +
            "                else if (newState.equals(\"walk\")) frameCount = 1;\n" +
            "                else if (newState.equals(\"attack\")) frameCount = 3;\n" +
            "                else if (newState.equals(\"death\")) { frameCount = 4; deathTime = System.currentTimeMillis(); }\n" +
            "            }\n" +
            "        }\n" +
            "\n" +
            "        void update() {\n" +
            "            if (health <= 0 && !dead) {\n" +
            "                changeState(\"death\");\n" +
            "                dead = true;\n" +
            "            }\n" +
            "            if (dead) { frameCount = 4; return; }\n" +
            "\n" +
            "            double targetX = 0, targetY = 0;\n" +
            "            boolean insideHouse = x > 150 && x < 650 && y > 150 && y < 450;\n" +
            "            if (!insideHouse && !type.equals(\"runner\")) {\n" +
            "                Window best = null; double bestDist = 999999;\n" +
            "                for (Window w : windows) {\n" +
            "                    double dist = Math.hypot(w.centerX - x, w.centerY - y);\n" +
            "                    if (dist < bestDist) { bestDist = dist; best = w; }\n" +
            "                }\n" +
            "                if(best != null) { targetX = best.centerX; targetY = best.centerY; }\n" +
            "                else { targetX = core.x; targetY = core.y; }\n" +
            "            } else {\n" +
            "                if (Math.hypot(player.x - x, player.y - y) < 150 || type.equals(\"runner\")) {\n" +
            "                    targetX = player.x; targetY = player.y;\n" +
            "                } else {\n" +
            "                    targetX = core.x; targetY = core.y;\n" +
            "                }\n" +
            "            }\n" +
            "\n" +
            "            angle = Math.atan2(targetY - y, targetX - x);\n" +
            "            double distToTarget = Math.hypot(targetX - x, targetY - y);\n" +
            "\n" +
            "            if (distToTarget < size/2 + 20 && System.currentTimeMillis() - attackCooldown > 1000) {\n" +
            "                changeState(\"attack\");\n" +
            "                attackCooldown = System.currentTimeMillis();\n" +
            "            }\n" +
            "\n" +
            "            if (state.equals(\"attack\")) {\n" +
            "                frameCount = 3;\n" +
            "                if (System.currentTimeMillis() - stateStartTime > 500) {\n" +
            "                    changeState(\"walk\");\n" +
            "                }\n" +
            "                return;\n" +
            "            }\n" +
            "\n" +
            "            double currentSpeed = speed;\n" +
            "            for (Window w : windows) {\n" +
            "                if (w.getBounds().contains(x, y)) {\n" +
            "                    if (w.health > 0) {\n" +
            "                        currentSpeed = 0;\n" +
            "                        w.health -= (type.equals(\"boss\") ? 3 : (type.equals(\"superboss\") ? 10 : 1));\n" +
            "                        changeState(\"attack\");\n" +
            "                    } else {\n" +
            "                        currentSpeed = speed * 0.3;\n" +
            "                    }\n" +
            "                    break;\n" +
            "                }\n" +
            "            }\n" +
            "            \n" +
            "            if (currentSpeed > 0 && !state.equals(\"attack\")) {\n" +
            "                x += Math.cos(angle) * currentSpeed * 0.016;\n" +
            "                y += Math.sin(angle) * currentSpeed * 0.016;\n" +
            "                changeState(\"walk\");\n" +
            "                if (System.currentTimeMillis() - lastFrameChange > 150) {\n" +
            "                    frameCount = (frameCount == 1) ? 2 : 1;\n" +
            "                    lastFrameChange = System.currentTimeMillis();\n" +
            "                }\n" +
            "            } else if (currentSpeed == 0 && !state.equals(\"attack\")) {\n" +
            "                changeState(\"idle\");\n" +
            "            }\n" +
            "        }\n" +
            "        \n" +
            "        java.awt.Rectangle getBounds() { return new java.awt.Rectangle((int)x - size/2, (int)y - size/2, size, size); }\n" +
            "        \n" +
            "        void draw(java.awt.Graphics2D g) {\n" +
            "            java.awt.image.BufferedImage currentImg = null;\n" +
            "            if (type.equals(\"basic\") && imgBasic[frameCount] != null) currentImg = imgBasic[frameCount];\n" +
            "            else if (type.equals(\"runner\") && imgRunner[frameCount] != null) currentImg = imgRunner[frameCount];\n" +
            "            else if (type.equals(\"boss\") && imgBoss[frameCount] != null) currentImg = imgBoss[frameCount];\n" +
            "            else if (type.equals(\"superboss\") && imgSuperboss[frameCount] != null) currentImg = imgSuperboss[frameCount];\n" +
            "            \n" +
            "            if (currentImg != null) {\n" +
            "                java.awt.geom.AffineTransform old = g.getTransform();\n" +
            "                g.translate(x, y);\n" +
            "                g.rotate(angle);\n" +
            "                int iw = currentImg.getWidth();\n" +
            "                int ih = currentImg.getHeight();\n" +
            "                double scale = Math.min((double)size * 1.5 / iw, (double)size * 1.5 / ih);\n" +
            "                int dw = (int)(iw * scale);\n" +
            "                int dh = (int)(ih * scale);\n" +
            "                g.drawImage(currentImg, -dw/2, -dh/2, dw, dh, null);\n" +
            "                g.setTransform(old);\n" +
            "            } else {\n" +
            "                g.setColor(color); g.fillRect((int)x - size/2, (int)y - size/2, size, size);\n" +
            "                g.setColor(java.awt.Color.BLACK); g.drawRect((int)x - size/2, (int)y - size/2, size, size);\n" +
            "            }\n" +
            "        }\n" +
            "    }\n";
            
        code = m.replaceFirst(newEnemy);
        Files.write(f.toPath(), code.getBytes());
    }
}
