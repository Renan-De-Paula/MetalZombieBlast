import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class OpenWorldRefactor {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        // 1. Scale Grass 5x
        String grassLogic = "n3 = (int)this.camX - (int)this.camX % 64 - 64;\n" +
            "            n = n2 = (int)this.camY - (int)this.camY % 64 - 64;\n" +
            "            while ((double)n < this.camY + 600.0 + 64.0) {\n" +
            "                int n4 = n3;\n" +
            "                while ((double)n4 < this.camX + 800.0 + 64.0) {\n" +
            "                    graphics2D.drawImage((Image)imgBgGrass, n4, n, null);\n" +
            "                    n4 += 64;\n" +
            "                }\n" +
            "                n += 64;\n" +
            "            }";
        String newGrassLogic = "n3 = (int)this.camX - (int)this.camX % 320 - 320;\n" +
            "            n = n2 = (int)this.camY - (int)this.camY % 320 - 320;\n" +
            "            while ((double)n < this.camY + 600.0 + 320.0) {\n" +
            "                int n4 = n3;\n" +
            "                while ((double)n4 < this.camX + 800.0 + 320.0) {\n" +
            "                    graphics2D.drawImage(imgBgGrass, n4, n, 320, 320, null);\n" +
            "                    n4 += 320;\n" +
            "                }\n" +
            "                n += 320;\n" +
            "            }";
        code = code.replace(grassLogic, newGrassLogic);
        
        // 2. Spawn Enemies relative to Camera
        String spawnEnemy = "private void spawnEnemy() {\n" +
            "        double d;\n" +
            "        double d2;\n" +
            "        int n = this.random.nextInt(4);\n" +
            "        if (n == 0) {\n" +
            "            d2 = this.random.nextInt(800);\n" +
            "            d = -50.0;\n" +
            "        } else if (n == 1) {\n" +
            "            d2 = this.random.nextInt(800);\n" +
            "            d = 650.0;\n" +
            "        } else if (n == 2) {\n" +
            "            d2 = -50.0;\n" +
            "            d = this.random.nextInt(600);\n" +
            "        } else {\n" +
            "            d2 = 850.0;\n" +
            "            d = this.random.nextInt(600);\n" +
            "        }";
        String newSpawnEnemy = "private void spawnEnemy() {\n" +
            "        double d;\n" +
            "        double d2;\n" +
            "        int n = this.random.nextInt(4);\n" +
            "        if (n == 0) {\n" +
            "            d2 = this.camX + this.random.nextInt(800);\n" +
            "            d = this.camY - 100.0;\n" +
            "        } else if (n == 1) {\n" +
            "            d2 = this.camX + this.random.nextInt(800);\n" +
            "            d = this.camY + 700.0;\n" +
            "        } else if (n == 2) {\n" +
            "            d2 = this.camX - 100.0;\n" +
            "            d = this.camY + this.random.nextInt(600);\n" +
            "        } else {\n" +
            "            d2 = this.camX + 900.0;\n" +
            "            d = this.camY + this.random.nextInt(600);\n" +
            "        }";
        code = code.replace(spawnEnemy, newSpawnEnemy);
        
        // 3. Generate huge forest randomly
        String resourceGen = "this.resources.add(new ResourceNode(100, 100, 0, 0));\n" +
            "        this.resources.add(new ResourceNode(700, 200, 0, 1));\n" +
            "        this.resources.add(new ResourceNode(200, 500, 1, 0));\n" +
            "        this.resources.add(new ResourceNode(600, 500, 1, 1));\n" +
            "        this.resources.add(new ResourceNode(400, -100, 0, 2));";
        String newResourceGen = resourceGen + "\n" +
            "        // Open World Generation\n" +
            "        java.util.Random rnd = new java.util.Random();\n" +
            "        for (int i=0; i<400; i++) {\n" +
            "            int rx = -5000 + rnd.nextInt(10000);\n" +
            "            int ry = -5000 + rnd.nextInt(10000);\n" +
            "            // Avoid spawning inside the house (150 to 650 X, 150 to 450 Y)\n" +
            "            if (rx > 0 && rx < 800 && ry > 0 && ry < 600) continue;\n" +
            "            int type = rnd.nextInt(2); // 0=Tree, 1=Rock\n" +
            "            int model = rnd.nextInt(3);\n" +
            "            this.resources.add(new ResourceNode(rx, ry, type, model));\n" +
            "        }";
        code = code.replace(resourceGen, newResourceGen);

        Files.write(f.toPath(), code.getBytes());
    }
}
