import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class ResourceNodeRefactor {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        // 1. Add Arrays for loaded images
        String staticVars = "public static java.util.Map<String, java.awt.image.BufferedImage[]> weaponFrames";
        String newStaticVars = "public static java.awt.image.BufferedImage[][] imgTrees = new java.awt.image.BufferedImage[3][4];\n" +
            "    public static java.awt.image.BufferedImage[] imgRocks = new java.awt.image.BufferedImage[3];\n    " + staticVars;
        code = code.replace(staticVars, newStaticVars);
        
        String imageLoading = "imgPlayerShoot = ImageIO.read(new File(\"img/player_atirando_arma_levantada.png\"));";
        String newImageLoading = imageLoading + "\n" +
            "            for (int i=0; i<3; i++) {\n" +
            "                File rockF = new File(\"img/rock_model\" + i + \".png\");\n" +
            "                if (rockF.exists()) imgRocks[i] = ImageIO.read(rockF);\n" +
            "                for (int j=0; j<4; j++) {\n" +
            "                    File treeF = new File(\"img/tree_model\" + i + \"_f\" + j + \".png\");\n" +
            "                    if (treeF.exists()) imgTrees[i][j] = ImageIO.read(treeF);\n" +
            "                }\n" +
            "            }";
        code = code.replace(imageLoading, newImageLoading);
        
        // 2. ResourceNode Class
        String resourceClass = "    class ResourceNode {\n" +
            "        int x, y, type, model, health = 5;\n" + // type 0=tree, 1=rock
            "        long lastWindFrame = 0;\n" +
            "        int windFrame = 0;\n" +
            "        ResourceNode(int x, int y, int t, int m) { this.x = x; this.y = y; this.type = t; this.model = m; }\n" +
            "        java.awt.Rectangle getBounds() {\n" +
            "            if (type == 0) return new java.awt.Rectangle(x + 20, y + 80, 40, 40); // Tree trunk\n" +
            "            else return new java.awt.Rectangle(x, y, 60, 60); // Rock\n" +
            "        }\n" +
            "        void update() {\n" +
            "            if (type == 0 && System.currentTimeMillis() - lastWindFrame > 250) {\n" +
            "                windFrame = (windFrame + 1) % 4;\n" +
            "                lastWindFrame = System.currentTimeMillis();\n" +
            "            }\n" +
            "        }\n" +
            "        void draw(java.awt.Graphics2D g) {\n" +
            "            if (type == 0) {\n" +
            "                if (imgTrees[model][windFrame] != null) {\n" +
            "                    g.drawImage(imgTrees[model][windFrame], x - 40, y - 40, 160, 160, null);\n" +
            "                }\n" +
            "            } else {\n" +
            "                if (imgRocks[model] != null) {\n" +
            "                    g.drawImage(imgRocks[model], x, y, 60, 60, null);\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    }\n";
        code = code.replace("    class Enemy {", resourceClass + "    class Enemy {");
        
        // 3. Add to ZombieGame list
        code = code.replace("private ArrayList<Door> doors;", "private ArrayList<Door> doors;\n    private ArrayList<ResourceNode> resources;");
        code = code.replace("this.doors = new ArrayList();", "this.doors = new ArrayList();\n        this.resources = new ArrayList();");
        
        // 4. Generate some resources
        String genRes = "this.windows.add(new Window(450, 150, 100, 20, true));";
        String newGenRes = genRes + "\n" +
            "        for (int i=0; i<5; i++) {\n" +
            "            this.resources.add(new ResourceNode(-200 + this.random.nextInt(400), -200 + this.random.nextInt(1000), 0, this.random.nextInt(3)));\n" +
            "            this.resources.add(new ResourceNode(600 + this.random.nextInt(400), -200 + this.random.nextInt(1000), 0, this.random.nextInt(3)));\n" +
            "            this.resources.add(new ResourceNode(this.random.nextInt(800), -300 + this.random.nextInt(200), 1, this.random.nextInt(3)));\n" +
            "        }";
        code = code.replace(genRes, newGenRes);
        
        // 5. Update and Draw resources (and sorting layer logic!)
        // To implement sorting layer easily without full depth-sorting, draw resources AFTER house, but BEFORE player for trees where player.y > tree.y.
        // Actually, for simplicity, just draw all rocks, then player, then trees whose trunk > player.y.
        // Wait, standard 2D top-down: just draw everything ordered by Y coordinate.
        // Let's just draw them normally for now.
        code = code.replace("this.workbench.draw(graphics2D);", "this.workbench.draw(graphics2D);\n        for (ResourceNode r : this.resources) if (r.type == 1) r.draw(graphics2D);");
        
        // Trees drawn around player
        code = code.replace("this.player.draw(graphics2D);", "for (ResourceNode r : this.resources) if (r.type == 0 && r.y + 100 < this.player.y) r.draw(graphics2D);\n        this.player.draw(graphics2D);\n        for (ResourceNode r : this.resources) if (r.type == 0 && r.y + 100 >= this.player.y) r.draw(graphics2D);");
        
        // Update resources
        code = code.replace("this.player.update(this.keys, this.solidWalls, this.windows, this.doors);", "this.player.update(this.keys, this.solidWalls, this.windows, this.doors);\n        for (ResourceNode r : this.resources) r.update();");
        
        // 6. Player Collision with resources
        String colY = "for (Door d : doors) if (d.getBounds().intersects(boundsY)) canMoveY = false;";
        String colYnew = colY + "\n            for (ResourceNode r : ZombieGame.this.resources) if (r.getBounds().intersects(boundsY)) canMoveY = false;";
        code = code.replace(colY, colYnew);
        
        String colX = "for (Door d : doors) if (d.getBounds().intersects(boundsX)) canMoveX = false;";
        String colXnew = colX + "\n            for (ResourceNode r : ZombieGame.this.resources) if (r.getBounds().intersects(boundsX)) canMoveX = false;";
        code = code.replace(colX, colXnew);
        
        // 7. Hitting resources with Martelo
        String hitDoor = "for (Door d : this.doors) {\n" +
            "                    if (d.getBounds().intersects(hitBox)) {\n" +
            "                        if (this.iron >= 1 && this.screws >= 1) { this.iron--; this.screws--; }\n" +
            "                    }\n" +
            "                }";
        String newHitDoor = hitDoor + "\n" +
            "                java.util.Iterator<ResourceNode> itR = this.resources.iterator();\n" +
            "                while (itR.hasNext()) {\n" +
            "                    ResourceNode r = itR.next();\n" +
            "                    if (r.getBounds().intersects(hitBox)) {\n" +
            "                        r.health--;\n" +
            "                        this.damageTexts.add(new DamageText(r.x + 30, r.y + 30, 1));\n" +
            "                        if (r.health <= 0) {\n" +
            "                            itR.remove();\n" +
            "                            for (int i=0; i<3; i++) {\n" +
            "                                if (r.type == 0) this.drops.add(new Drop(this, r.x+30 + this.random.nextInt(30), r.y+80 + this.random.nextInt(30), 3, 1)); // Wood\n" +
            "                                else this.drops.add(new Drop(this, r.x+30 + this.random.nextInt(30), r.y+30 + this.random.nextInt(30), 5, 1)); // Iron/Stone\n" +
            "                            }\n" +
            "                        }\n" +
            "                    }\n" +
            "                }";
        code = code.replace(hitDoor, newHitDoor);
        
        Files.write(f.toPath(), code.getBytes());
    }
}
