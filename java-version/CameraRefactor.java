import java.io.File;
import java.nio.file.Files;

public class CameraRefactor {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));

        // 1. Add camera fields
        code = code.replace("boolean isMousePressed = false;", "boolean isMousePressed = false;\n    double camX = 0, camY = 0;");

        // 2. Map size changes (5x)
        code = code.replace("player.x = Math.max(10, Math.min(WIDTH - 10, player.x));", "// player bounds removed for open world");
        code = code.replace("player.y = Math.max(10, Math.min(HEIGHT - 10, player.y));", "// player bounds removed");

        // 3. Mouse Attack Angle
        String oldAttack = "double angle = Math.atan2(mouseY - player.y, mouseX - player.x);";
        String newAttack = "double angle = Math.atan2((mouseY + camY) - player.y, (mouseX + camX) - player.x);";
        code = code.replace(oldAttack, newAttack);

        // 4. Enemy Spawning - Spawn way outside the camera
        String oldSpawn = "if (Math.random() < 0.5) {\n" +
            "                x = Math.random() < 0.5 ? -50 : WIDTH + 50;\n" +
            "                y = Math.random() * HEIGHT;\n" +
            "            } else {\n" +
            "                x = Math.random() * WIDTH;\n" +
            "                y = Math.random() < 0.5 ? -50 : HEIGHT + 50;\n" +
            "            }";
        String newSpawn = "if (Math.random() < 0.5) {\n" +
            "                x = camX + (Math.random() < 0.5 ? -200 : WIDTH + 200);\n" +
            "                y = camY + Math.random() * HEIGHT;\n" +
            "            } else {\n" +
            "                x = camX + Math.random() * WIDTH;\n" +
            "                y = camY + (Math.random() < 0.5 ? -200 : HEIGHT + 200);\n" +
            "            }";
        code = code.replace(oldSpawn, newSpawn);

        // 5. PaintComponent Camera Setup & Grass Tiling
        String paintOld = "        Graphics2D g2d = (Graphics2D) g;\n" +
            "        g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);\n" +
            "\n" +
            "        if (imgBgGrass != null) {\n" +
            "            for (int y = 0; y < HEIGHT; y += 64) {\n" +
            "                for (int x = 0; x < WIDTH; x += 64) {\n" +
            "                    g2d.drawImage(imgBgGrass, x, y, null);\n" +
            "                }\n" +
            "            }\n" +
            "        } else {\n" +
            "            g2d.setColor(new java.awt.Color(34, 139, 34));\n" +
            "            g2d.fillRect(0, 0, WIDTH, HEIGHT);\n" +
            "        }";
            
        String paintNew = "        Graphics2D g2d = (Graphics2D) g;\n" +
            "        g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);\n" +
            "        camX = player.x - WIDTH / 2.0;\n" +
            "        camY = player.y - HEIGHT / 2.0;\n" +
            "        g2d.translate(-camX, -camY);\n" +
            "\n" +
            "        if (imgBgGrass != null) {\n" +
            "            int startX = (int)camX - ((int)camX % 64) - 64;\n" +
            "            int startY = (int)camY - ((int)camY % 64) - 64;\n" +
            "            for (int y = startY; y < camY + HEIGHT + 64; y += 64) {\n" +
            "                for (int x = startX; x < camX + WIDTH + 64; x += 64) {\n" +
            "                    g2d.drawImage(imgBgGrass, x, y, null);\n" +
            "                }\n" +
            "            }\n" +
            "        } else {\n" +
            "            g2d.setColor(new java.awt.Color(34, 139, 34));\n" +
            "            g2d.fillRect((int)camX, (int)camY, WIDTH, HEIGHT);\n" +
            "        }";
        // Try replacing with Color/RenderingHints since they might be imported or not.
        code = code.replace(paintOld, paintNew);
        
        String paintOld2 = "        Graphics2D g2d = (Graphics2D) g;\n" +
            "        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);\n" +
            "\n" +
            "        if (imgBgGrass != null) {\n" +
            "            for (int y = 0; y < HEIGHT; y += 64) {\n" +
            "                for (int x = 0; x < WIDTH; x += 64) {\n" +
            "                    g2d.drawImage(imgBgGrass, x, y, null);\n" +
            "                }\n" +
            "            }\n" +
            "        } else {\n" +
            "            g2d.setColor(new Color(34, 139, 34));\n" +
            "            g2d.fillRect(0, 0, WIDTH, HEIGHT);\n" +
            "        }";
        String paintNew2 = "        Graphics2D g2d = (Graphics2D) g;\n" +
            "        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);\n" +
            "        camX = player.x - WIDTH / 2.0;\n" +
            "        camY = player.y - HEIGHT / 2.0;\n" +
            "        g2d.translate(-camX, -camY);\n" +
            "\n" +
            "        if (imgBgGrass != null) {\n" +
            "            int startX = (int)camX - ((int)camX % 64) - 64;\n" +
            "            int startY = (int)camY - ((int)camY % 64) - 64;\n" +
            "            for (int y = startY; y < camY + HEIGHT + 64; y += 64) {\n" +
            "                for (int x = startX; x < camX + WIDTH + 64; x += 64) {\n" +
            "                    g2d.drawImage(imgBgGrass, x, y, null);\n" +
            "                }\n" +
            "            }\n" +
            "        } else {\n" +
            "            g2d.setColor(new Color(34, 139, 34));\n" +
            "            g2d.fillRect((int)camX, (int)camY, WIDTH, HEIGHT);\n" +
            "        }";
        code = code.replace(paintOld2, paintNew2);

        // 6. UI untranslate
        String uiOld = "drawCrosshair(g2d);\n        drawUI(g2d);";
        String uiNew = "g2d.translate(camX, camY);\n        drawCrosshair(g2d);\n        drawUI(g2d);";
        code = code.replace(uiOld, uiNew);

        Files.write(f.toPath(), code.getBytes());
    }
}
