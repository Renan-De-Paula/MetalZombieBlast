import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class AddDoorRefactor {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        // 1. Add image array for doors
        if(!code.contains("imgDoorT")) {
            code = code.replace("public static java.awt.image.BufferedImage[] imgWindowT = new java.awt.image.BufferedImage[6];", 
                "public static java.awt.image.BufferedImage[] imgWindowT = new java.awt.image.BufferedImage[6];\n    public static java.awt.image.BufferedImage[] imgDoorT = new java.awt.image.BufferedImage[6];");
                
            // 2. Load images for doors
            String doorLoad = "try { imgDoorT[(i-1)*2] = javax.imageio.ImageIO.read(new java.io.File(\"img/door_t\"+i+\"_h.png\")); } catch(Exception ex){}\n" +
                "                    try { imgDoorT[(i-1)*2+1] = javax.imageio.ImageIO.read(new java.io.File(\"img/door_t\"+i+\"_v.png\")); } catch(Exception ex){}\n" +
                "                }";
            code = code.replace("                }\n            }\n        } catch (Exception e)", doorLoad + "\n            }\n        } catch (Exception e)");
            
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
                "            java.awt.image.BufferedImage img = isHorizontal ? imgDoorT[0] : imgDoorT[1]; // Tier 1 Wood Door\n" +
                "            if (img != null) g.drawImage(img, x, y, width, height, null);\n" +
                "            else { g.setColor(new java.awt.Color(139, 69, 19)); g.fillRect(x, y, width, height); }\n" +
                "        }\n" +
                "        void toggle() { isOpen = !isOpen; }\n" +
                "    }\n";
                
            code = code.replace("class Core {", doorClass + "    class Core {");
            
            // 4. Add doors list
            code = code.replace("java.util.ArrayList<Window> windows = new java.util.ArrayList<>();", "java.util.ArrayList<Window> windows = new java.util.ArrayList<>();\n    java.util.ArrayList<Door> doors = new java.util.ArrayList<>();");
            // OR if it wasn't replaced yet:
            code = code.replace("ArrayList<Window> windows = new ArrayList<>();", "ArrayList<Window> windows = new ArrayList<>();\n    ArrayList<Door> doors = new ArrayList<>();");
            
            // 5. Add a door to the house! Let's replace the top window with a door.
            String initMap = "windows.add(new Window(350, 150, 100, 20, true));";
            String newInitMap = "doors.add(new Door(350, 150, 100, 20, true));";
            code = code.replace(initMap, newInitMap);
            
            // 6. Draw doors
            code = code.replace("for (Window w : windows) w.draw(g2d);", "for (Window w : windows) w.draw(g2d);\n        for (Door d : doors) d.draw(g2d);");
            
            // 7. Player collision with doors
            String oldColX = "for (Window w : windows) if (w.health > 0 && w.getBounds().intersects(boundsX)) canMoveX = false;";
            String newColX = "for (Window w : windows) if (w.health > 0 && w.getBounds().intersects(boundsX)) canMoveX = false;\n            for (Door d : doors) if (d.getBounds().intersects(boundsX)) canMoveX = false;";
            code = code.replace(oldColX, newColX);
            
            String oldColY = "for (Window w : windows) if (w.health > 0 && w.getBounds().intersects(boundsY)) canMoveY = false;";
            String newColY = "for (Window w : windows) if (w.health > 0 && w.getBounds().intersects(boundsY)) canMoveY = false;\n            for (Door d : doors) if (d.getBounds().intersects(boundsY)) canMoveY = false;";
            code = code.replace(oldColY, newColY);
            
            // 8. Door interaction! If player presses 'E', toggle nearby doors.
            String keyOld = "if (keys[KeyEvent.VK_R]) { player.weapon1.ammo = player.weapon1.maxAmmo; player.weapon2.ammo = player.weapon2.maxAmmo; }";
            String keyNew = "if (keys[KeyEvent.VK_R]) { player.weapon1.ammo = player.weapon1.maxAmmo; player.weapon2.ammo = player.weapon2.maxAmmo; }\n" +
                "            if (keys[KeyEvent.VK_E]) {\n" +
                "                keys[KeyEvent.VK_E] = false; // debounce\n" +
                "                for (Door d : doors) {\n" +
                "                    if (Math.hypot((d.x+d.width/2.0) - player.x, (d.y+d.height/2.0) - player.y) < 80) d.toggle();\n" +
                "                }\n" +
                "            }";
            code = code.replace(keyOld, keyNew);
            // OR if full package name
            String keyOld2 = "if (keys[java.awt.event.KeyEvent.VK_R]) { player.weapon1.ammo = player.weapon1.maxAmmo; player.weapon2.ammo = player.weapon2.maxAmmo; }";
            String keyNew2 = "if (keys[java.awt.event.KeyEvent.VK_R]) { player.weapon1.ammo = player.weapon1.maxAmmo; player.weapon2.ammo = player.weapon2.maxAmmo; }\n" +
                "            if (keys[java.awt.event.KeyEvent.VK_E]) {\n" +
                "                keys[java.awt.event.KeyEvent.VK_E] = false; // debounce\n" +
                "                for (Door d : doors) {\n" +
                "                    if (Math.hypot((d.x+d.width/2.0) - player.x, (d.y+d.height/2.0) - player.y) < 80) d.toggle();\n" +
                "                }\n" +
                "            }";
            code = code.replace(keyOld2, keyNew2);
            
            Files.write(f.toPath(), code.getBytes());
        }
    }
}
