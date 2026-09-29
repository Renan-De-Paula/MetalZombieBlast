import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class MouseRefactor {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));

        // Add MouseListener to implements
        code = code.replace("implements ActionListener, KeyListener, MouseMotionListener {", "implements ActionListener, KeyListener, MouseMotionListener, java.awt.event.MouseListener {");
        
        // Add addMouseListener(this)
        code = code.replace("addMouseMotionListener(this);", "addMouseMotionListener(this);\n        addMouseListener(this);");
        
        // Add isMousePressed variable
        code = code.replace("boolean[] keys = new boolean[256];", "boolean[] keys = new boolean[256];\n    boolean isMousePressed = false;");
        
        // Update the shooting logic
        String shootOld = "if (System.currentTimeMillis() - player.lastShotTime > activeWeapon.cooldown) {\n" +
            "            mouseAttack(activeWeapon);\n" +
            "        }";
        String shootNew = "if (isMousePressed && System.currentTimeMillis() - player.lastShotTime > activeWeapon.cooldown) {\n" +
            "            mouseAttack(activeWeapon);\n" +
            "        }";
        code = code.replace(shootOld, shootNew);
        
        // Add MouseListener methods at the end of the file before the last brace
        String mouseMethods = "\n    public void mouseClicked(java.awt.event.MouseEvent e) {}\n" +
            "    public void mousePressed(java.awt.event.MouseEvent e) { if(e.getButton() == java.awt.event.MouseEvent.BUTTON1) isMousePressed = true; }\n" +
            "    public void mouseReleased(java.awt.event.MouseEvent e) { if(e.getButton() == java.awt.event.MouseEvent.BUTTON1) isMousePressed = false; }\n" +
            "    public void mouseEntered(java.awt.event.MouseEvent e) {}\n" +
            "    public void mouseExited(java.awt.event.MouseEvent e) {}\n" +
            "    public static void main";
            
        code = code.replace("    public static void main", mouseMethods);

        Files.write(f.toPath(), code.getBytes());
    }
}
