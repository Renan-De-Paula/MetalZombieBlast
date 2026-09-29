import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class LootChestRefactor {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        // 1. Image loading
        String staticVars = "public static java.awt.image.BufferedImage[][] imgTrees";
        String newStaticVars = "public static java.awt.image.BufferedImage[][] imgChests = new java.awt.image.BufferedImage[3][3];\n    " + staticVars;
        code = code.replace(staticVars, newStaticVars);
        
        String imageLoading = "if (treeF.exists()) imgTrees[i][j] = ImageIO.read(treeF);\n                }";
        String newImageLoading = imageLoading + "\n" +
            "                for (int j=0; j<3; j++) {\n" +
            "                    File chestF = new File(\"img/chest_m\" + i + \"_f\" + j + \".png\");\n" +
            "                    if (chestF.exists()) imgChests[i][j] = ImageIO.read(chestF);\n" +
            "                }";
        code = code.replace(imageLoading, newImageLoading);
        
        // 2. Additional Inventory Variables & Drop Pickup
        String vars = "public int wood = 10, iron = 0, lead = 0, nails = 10, screws = 0, fuelCans = 0;";
        String newVars = vars + "\n    public int food = 0, medkit = 0, crystals = 0;";
        code = code.replace(vars, newVars);
        
        // Backpack capacity
        String capacity = "int currentCapacity = this.wood + this.nails + this.iron + this.screws + this.lead;";
        String newCapacity = "int currentCapacity = this.wood + this.nails + this.iron + this.screws + this.lead + this.food + this.medkit + this.crystals + this.fuelCans;";
        code = code.replace(capacity, newCapacity);
        
        // Pickup logic
        String oldPickup = "else if (iteratorD.type == 7) this.lead += iteratorD.value;";
        String newPickup = oldPickup + "\n" +
            "                    else if (iteratorD.type == 14) this.food += iteratorD.value;\n" +
            "                    else if (iteratorD.type == 15) this.medkit += iteratorD.value;\n" +
            "                    else if (iteratorD.type == 16) this.crystals += iteratorD.value;\n" +
            "                    else if (iteratorD.type == 17) this.fuelCans += iteratorD.value;";
        code = code.replace(oldPickup, newPickup);
        
        String dropColor = "} else if (iteratorD.type >= 8 && iteratorD.type <= 13) {";
        String newDropColor = "} else if (this.type >= 14 && this.type <= 17) {\n" +
            "                graphics2D.setColor(new java.awt.Color(0, 255, 255)); // Cyan for rare items\n" +
            "                graphics2D.fillRect((int)this.x - 4, (int)this.y - 4, 8, 8);\n" +
            "            } else if (this.type >= 8 && this.type <= 13) {";
        code = code.replace(dropColor, newDropColor);
        
        // 3. LootChest Class
        String chestClass = "    class LootChest {\n" +
            "        int x, y, model, state = 0;\n" +
            "        long openTime = 0;\n" +
            "        LootChest(int x, int y, int m) { this.x = x; this.y = y; this.model = m; }\n" +
            "        void draw(java.awt.Graphics2D g) {\n" +
            "            if (imgChests[model][state] != null) {\n" +
            "                g.drawImage(imgChests[model][state], x, y, 60, 60, null);\n" +
            "            }\n" +
            "        }\n" +
            "        void update() {\n" +
            "            if (state == 1 && System.currentTimeMillis() - openTime > 200) {\n" +
            "                state = 2; // Fully open\n" +
            "                // Drop items!\n" +
            "                if (model == 0) {\n" +
            "                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 3, 5)); // Wood\n" +
            "                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 4, 5)); // Nails\n" +
            "                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 14, 2)); // Food\n" +
            "                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 8, 20)); // Ammo T1\n" +
            "                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 9, 30)); // Ammo T2\n" +
            "                } else if (model == 1) {\n" +
            "                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 5, 5)); // Iron\n" +
            "                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 6, 5)); // Screws\n" +
            "                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 15, 1)); // Medkit\n" +
            "                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 10, 40)); // Ammo T3\n" +
            "                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 11, 10)); // Ammo T4\n" +
            "                } else if (model == 2) {\n" +
            "                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 7, 5)); // Lead\n" +
            "                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 16, 2)); // Crystals\n" +
            "                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 17, 1)); // Fuel\n" +
            "                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 12, 5)); // Ammo T5\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    }\n";
        code = code.replace("    class Enemy {", chestClass + "    class Enemy {");
        
        // 4. Add Chests list
        code = code.replace("private ArrayList<ResourceNode> resources;", "private ArrayList<ResourceNode> resources;\n    private java.util.ArrayList<LootChest> chests;");
        code = code.replace("this.resources = new ArrayList();", "this.resources = new ArrayList();\n        this.chests = new java.util.ArrayList();");
        
        // 5. Generate Chests
        String genRes = "for (int i=0; i<5; i++) {";
        String newGenRes = "this.chests.add(new LootChest(300, 100, 0));\n        this.chests.add(new LootChest(-100, 300, 1));\n        this.chests.add(new LootChest(600, 400, 2));\n        " + genRes;
        code = code.replace(genRes, newGenRes);
        
        // 6. Draw Chests (drawn before player)
        String drawRes = "for (ResourceNode r : this.resources) if (r.type == 1) r.draw(graphics2D);";
        String newDrawRes = drawRes + "\n        for (LootChest c : this.chests) c.draw(graphics2D);";
        code = code.replace(drawRes, newDrawRes);
        
        // 7. Update Chests
        String updateRes = "for (ResourceNode r : this.resources) r.update();";
        String newUpdateRes = updateRes + "\n        for (LootChest c : this.chests) c.update();";
        code = code.replace(updateRes, newUpdateRes);
        
        // 8. Open Chest interaction 'E' (key code 69)
        String eKey = "ZombieGame.this.chestScrews += ZombieGame.this.screws; ZombieGame.this.screws = 0;\n" +
            "                }\n" +
            "            }";
        String newEKey = eKey + "\n" +
            "            for (LootChest c : ZombieGame.this.chests) {\n" +
            "                if (c.state == 0 && Math.hypot((c.x+30) - ZombieGame.this.player.x, (c.y+30) - ZombieGame.this.player.y) < 80) {\n" +
            "                    c.state = 1;\n" +
            "                    c.openTime = System.currentTimeMillis();\n" +
            "                }\n" +
            "            }";
        code = code.replace(eKey, newEKey);

        Files.write(f.toPath(), code.getBytes());
    }
}
