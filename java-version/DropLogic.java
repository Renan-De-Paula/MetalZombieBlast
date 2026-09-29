import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class DropLogic {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        // 1. Enemy drop generation
        // Currently: this.generateDrops(objE.x, objE.y, objE.expValue, objE.type);
        // We will expand generateDrops to drop random materials!
        String oldDrops = "private void generateDrops(double d, double d2, int n, String string) {\n" +
            "        this.drops.add(new Drop(this, d, d2, 0, n));\n" +
            "        if (this.random.nextInt(100) < 30) {\n" +
            "            this.drops.add(new Drop(this, d + (double)(this.random.nextInt(20) - 10), d2 + (double)(this.random.nextInt(20) - 10), 1, 10));\n" +
            "        }\n" +
            "        if (this.random.nextInt(100) < 15) {\n" +
            "            this.drops.add(new Drop(this, d + (double)(this.random.nextInt(20) - 10), d2 + (double)(this.random.nextInt(20) - 10), 2, 1));\n" +
            "        }\n" +
            "    }";
        String newDrops = "private void generateDrops(double d, double d2, int n, String string) {\n" +
            "        this.drops.add(new Drop(this, d, d2, 0, n)); // EXP\n" +
            "        if (this.random.nextInt(100) < 30) this.drops.add(new Drop(this, d + this.random.nextInt(20)-10, d2 + this.random.nextInt(20)-10, 1, 10)); // Coins\n" +
            "        \n" +
            "        // 30% chance for crafting materials\n" +
            "        if (this.random.nextInt(100) < 30) {\n" +
            "            int mat = this.random.nextInt(5); // 0=Wood, 1=Nails, 2=Iron, 3=Screws, 4=Lead\n" +
            "            this.drops.add(new Drop(this, d + this.random.nextInt(20)-10, d2 + this.random.nextInt(20)-10, 3 + mat, 1));\n" +
            "        }\n" +
            "        // 20% chance for ammo\n" +
            "        if (this.random.nextInt(100) < 20) {\n" +
            "            int ammoT = this.random.nextInt(6); // 0..5 Ammo types\n" +
            "            this.drops.add(new Drop(this, d + this.random.nextInt(20)-10, d2 + this.random.nextInt(20)-10, 8 + ammoT, 10));\n" +
            "        }\n" +
            "    }";
        code = code.replace(oldDrops, newDrops);
        
        // 2. Player pickup logic
        // Current:
        // if (iteratorD.type == 0) { this.gainExp(iteratorD.value); }
        // else if (iteratorD.type == 1) { this.coins += iteratorD.value; }
        // else if (iteratorD.type == 2) { this.materials += iteratorD.value; }
        // itD.remove();
        String oldPickup = "if (iteratorD.type == 0) {\n" +
            "                this.gainExp(iteratorD.value);\n" +
            "            } else if (iteratorD.type == 1) {\n" +
            "                this.coins += iteratorD.value;\n" +
            "            } else if (iteratorD.type == 2) {\n" +
            "                this.materials += iteratorD.value;\n" +
            "            }\n" +
            "            itD.remove();";
        String newPickup = "if (iteratorD.type == 0) this.gainExp(iteratorD.value);\n" +
            "            else if (iteratorD.type == 1) this.coins += iteratorD.value;\n" +
            "            else if (iteratorD.type == 2) this.materials += iteratorD.value;\n" +
            "            else if (iteratorD.type == 3) this.wood += iteratorD.value;\n" +
            "            else if (iteratorD.type == 4) this.nails += iteratorD.value;\n" +
            "            else if (iteratorD.type == 5) this.iron += iteratorD.value;\n" +
            "            else if (iteratorD.type == 6) this.screws += iteratorD.value;\n" +
            "            else if (iteratorD.type == 7) this.lead += iteratorD.value;\n" +
            "            else if (iteratorD.type >= 8 && iteratorD.type <= 13) {\n" +
            "                this.globalAmmo[iteratorD.type - 8] += iteratorD.value;\n" +
            "            }\n" +
            "            itD.remove();";
        code = code.replace(oldPickup, newPickup);
        
        // 3. Drop draw logic
        String dropDraw = "} else if (this.type == 2) {\n" +
            "                graphics2D.setColor(Color.LIGHT_GRAY);\n" +
            "                graphics2D.fillRect((int)this.x - 4, (int)this.y - 4, 8, 8);\n" +
            "                graphics2D.setColor(Color.BLACK);\n" +
            "                graphics2D.drawRect((int)this.x - 4, (int)this.y - 4, 8, 8);\n" +
            "            }\n" +
            "        }";
        String newDropDraw = "} else if (this.type == 2) {\n" +
            "                graphics2D.setColor(Color.LIGHT_GRAY);\n" +
            "                graphics2D.fillRect((int)this.x - 4, (int)this.y - 4, 8, 8);\n" +
            "            } else if (this.type >= 3 && this.type <= 7) {\n" +
            "                graphics2D.setColor(new java.awt.Color(139, 69, 19)); // Brown for crafting\n" +
            "                graphics2D.fillRect((int)this.x - 4, (int)this.y - 4, 8, 8);\n" +
            "            } else if (this.type >= 8 && this.type <= 13) {\n" +
            "                graphics2D.setColor(new java.awt.Color(255, 0, 0)); // Red for Ammo\n" +
            "                graphics2D.fillRect((int)this.x - 4, (int)this.y - 4, 8, 4);\n" +
            "            }\n" +
            "        }";
        code = code.replace(dropDraw, newDropDraw);
        
        Files.write(f.toPath(), code.getBytes());
    }
}
