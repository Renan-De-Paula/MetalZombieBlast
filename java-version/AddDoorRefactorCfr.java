import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class AddDoorRefactorCfr {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        // 1. Add image array for doors
        if (!code.contains("imgDoorT")) {
            code = code.replace("public static BufferedImage[] imgWindowT;", "public static BufferedImage[] imgWindowT;\n    public static BufferedImage[] imgDoorT;");
            code = code.replace("imgWindowT = new BufferedImage[6];", "imgWindowT = new BufferedImage[6];\n        imgDoorT = new BufferedImage[6];");
            
            // 2. Load images for doors
            String windowLoad = "ZombieGame.imgWindowT[(n - 1) * 2 + 1] = ImageIO.read(new File(\"img/window_t\" + n + \"_v.png\"));";
            String doorLoad = "ZombieGame.imgWindowT[(n - 1) * 2 + 1] = ImageIO.read(new File(\"img/window_t\" + n + \"_v.png\"));\n" +
                "                        ZombieGame.imgDoorT[(n - 1) * 2] = ImageIO.read(new File(\"img/door_t\" + n + \"_h.png\"));\n" +
                "                        ZombieGame.imgDoorT[(n - 1) * 2 + 1] = ImageIO.read(new File(\"img/door_t\" + n + \"_v.png\"));";
            code = code.replace(windowLoad, doorLoad);
            
            // 3. Create Door class
            String doorClass = "    class Door {\n" +
                "        int x, y, width, height;\n" +
                "        boolean isHorizontal, isOpen = false;\n" +
                "        Door(int x, int y, int w, int h, boolean horiz) {\n" +
                "            this.x=x; this.y=y; width=w; height=h; isHorizontal=horiz;\n" +
                "        }\n" +
                "        java.awt.Rectangle getBounds() { return isOpen ? new java.awt.Rectangle(0,0,0,0) : new java.awt.Rectangle(x, y, width, height); }\n" +
                "        void draw(java.awt.Graphics2D g) {\n" +
                "            if (isOpen) return;\n" +
                "            java.awt.image.BufferedImage img = isHorizontal ? imgDoorT[0] : imgDoorT[1];\n" +
                "            if (img != null) g.drawImage(img, x, y, width, height, null);\n" +
                "            else { g.setColor(new java.awt.Color(139, 69, 19)); g.fillRect(x, y, width, height); }\n" +
                "        }\n" +
                "        void toggle() { isOpen = !isOpen; }\n" +
                "    }\n";
                
            code = code.replace("class Core {", doorClass + "    class Core {");
            
            // 4. Add doors list
            code = code.replace("ArrayList<Window> windows = new ArrayList();", "ArrayList<Window> windows = new ArrayList();\n    ArrayList<Door> doors = new ArrayList();");
            
            // 5. Add a door to the house! Let's replace the top window with a door.
            code = code.replace("this.windows.add(new Window(350, 150, 100, 20, true));", "this.doors.add(new Door(350, 150, 100, 20, true));");
            
            // 6. Draw doors
            code = code.replace("for (Window window : this.windows) {\n            window.draw(graphics2D);\n        }", 
                "for (Window window : this.windows) {\n            window.draw(graphics2D);\n        }\n        for (Door d : this.doors) d.draw(graphics2D);");
            
            // 7. Player collision with doors
            String colLoop = "for (Window window : arrayList2) {\n" +
                "            if (window.health <= 0 || !window.getBounds().intersects(rectangle)) continue;\n" +
                "            bl = false;\n" +
                "        }";
            String colNew = "for (Window window : arrayList2) {\n" +
                "            if (window.health <= 0 || !window.getBounds().intersects(rectangle)) continue;\n" +
                "            bl = false;\n" +
                "        }\n        for (Door d : this.doors) if (d.getBounds().intersects(rectangle)) bl = false;";
            code = code.replace(colLoop, colNew);
            
            String colLoopY = "for (Window window : arrayList2) {\n" +
                "            if (window.health <= 0 || !window.getBounds().intersects(rectangle2)) continue;\n" +
                "            bl2 = false;\n" +
                "        }";
            String colNewY = "for (Window window : arrayList2) {\n" +
                "            if (window.health <= 0 || !window.getBounds().intersects(rectangle2)) continue;\n" +
                "            bl2 = false;\n" +
                "        }\n        for (Door d : this.doors) if (d.getBounds().intersects(rectangle2)) bl2 = false;";
            code = code.replace(colLoopY, colNewY);
            
            // 8. Door interaction! If player presses 'E', toggle nearby doors.
            String keyOld = "if (blArray[82]) {\n" +
                "            this.weapon1.ammo = this.weapon1.maxAmmo;\n" +
                "            this.weapon2.ammo = this.weapon2.maxAmmo;\n" +
                "        }";
            String keyNew = "if (blArray[82]) {\n" +
                "            this.weapon1.ammo = this.weapon1.maxAmmo;\n" +
                "            this.weapon2.ammo = this.weapon2.maxAmmo;\n" +
                "        }\n" +
                "        if (blArray[69]) {\n" +
                "            blArray[69] = false;\n" +
                "            for (Door d : this.doors) {\n" +
                "                if (Math.hypot((d.x+d.width/2.0) - this.x, (d.y+d.height/2.0) - this.y) < 80) d.toggle();\n" +
                "            }\n" +
                "        }";
            code = code.replace(keyOld, keyNew);
            
            Files.write(f.toPath(), code.getBytes());
        }
    }
}
