import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class MenuRefactor {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        // 1. Add gameState
        String vars = "public boolean isMapOpen = false;";
        String newVars = "public int gameState = 0;\n    " + vars;
        code = code.replace(vars, newVars);
        
        // 2. actionPerformed
        String action = "public void actionPerformed(ActionEvent actionEvent) {\n        if (!this.isPaused) {\n            this.update();\n        }";
        String newAction = "public void actionPerformed(ActionEvent actionEvent) {\n        if (this.gameState == 1 && !this.isPaused) {\n            this.update();\n        }";
        code = code.replace(action, newAction);
        
        // 3. mousePressed
        String mouseP = "public void mousePressed(MouseEvent mouseEvent) {\n        if (mouseEvent.getButton() == 1) {";
        String newMouseP = "public void mousePressed(MouseEvent mouseEvent) {\n" +
            "        int mx = mouseEvent.getX();\n" +
            "        int my = mouseEvent.getY();\n" +
            "        if (this.gameState == 0) {\n" +
            "            if (mx >= 300 && mx <= 500 && my >= 250 && my <= 300) {\n" +
            "                this.gameState = 1;\n" +
            "            } else if (mx >= 300 && mx <= 500 && my >= 320 && my <= 370) {\n" +
            "                this.gameState = 2;\n" +
            "            } else if (mx >= 300 && mx <= 500 && my >= 390 && my <= 440) {\n" +
            "                this.gameState = 3;\n" +
            "            }\n" +
            "            return;\n" +
            "        }\n" +
            "        if (this.gameState == 2 || this.gameState == 3) {\n" +
            "            if (mx >= 300 && mx <= 500 && my >= 500 && my <= 550) {\n" +
            "                this.gameState = 0;\n" +
            "            }\n" +
            "            return;\n" +
            "        }\n" +
            "        if (mouseEvent.getButton() == 1) {";
        code = code.replace(mouseP, newMouseP);
        
        // 4. paintComponent
        String paintC = "public void paintComponent(Graphics graphics) {\n        int n;\n        int n2;\n        int n3;\n        super.paintComponent(graphics);\n        Graphics2D graphics2D = (Graphics2D)graphics;\n        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);";
        String newPaintC = paintC + "\n" +
            "        if (this.gameState == 0) { this.drawMainMenu(graphics2D); return; }\n" +
            "        if (this.gameState == 2) { this.drawSettingsMenu(graphics2D); return; }\n" +
            "        if (this.gameState == 3) { this.drawCreditsMenu(graphics2D); return; }";
        code = code.replace(paintC, newPaintC);
        
        // 5. Inject draw methods
        String methods = "    private void drawButton(java.awt.Graphics2D g, String text, int x, int y, int w, int h) {\n" +
            "        g.setColor(new java.awt.Color(50, 50, 50));\n" +
            "        g.fillRect(x, y, w, h);\n" +
            "        g.setColor(java.awt.Color.WHITE);\n" +
            "        g.drawRect(x, y, w, h);\n" +
            "        g.setFont(new java.awt.Font(\"Arial\", 1, 20));\n" +
            "        java.awt.FontMetrics fm = g.getFontMetrics();\n" +
            "        int tw = fm.stringWidth(text);\n" +
            "        g.drawString(text, x + (w - tw)/2, y + (h + fm.getAscent())/2 - 3);\n" +
            "    }\n" +
            "\n" +
            "    private void drawMainMenu(java.awt.Graphics2D g) {\n" +
            "        g.setColor(new java.awt.Color(20, 20, 20));\n" +
            "        g.fillRect(0, 0, 800, 600);\n" +
            "        g.setColor(java.awt.Color.RED);\n" +
            "        g.setFont(new java.awt.Font(\"Arial\", 1, 60));\n" +
            "        g.drawString(\"ZOMBIE SURVIVAL\", 120, 150);\n" +
            "        drawButton(g, \"JOGAR\", 300, 250, 200, 50);\n" +
            "        drawButton(g, \"CONFIGURAÇÕES\", 300, 320, 200, 50);\n" +
            "        drawButton(g, \"CRÉDITOS\", 300, 390, 200, 50);\n" +
            "    }\n" +
            "\n" +
            "    private void drawSettingsMenu(java.awt.Graphics2D g) {\n" +
            "        g.setColor(new java.awt.Color(20, 20, 20));\n" +
            "        g.fillRect(0, 0, 800, 600);\n" +
            "        g.setColor(java.awt.Color.WHITE);\n" +
            "        g.setFont(new java.awt.Font(\"Arial\", 1, 40));\n" +
            "        g.drawString(\"CONFIGURAÇÕES\", 220, 150);\n" +
            "        g.setFont(new java.awt.Font(\"Arial\", 0, 20));\n" +
            "        g.drawString(\"Música: Ligado\", 320, 250);\n" +
            "        g.drawString(\"Efeitos: Ligado\", 320, 300);\n" +
            "        g.drawString(\"Dificuldade: Normal\", 320, 350);\n" +
            "        drawButton(g, \"VOLTAR\", 300, 500, 200, 50);\n" +
            "    }\n" +
            "\n" +
            "    private void drawCreditsMenu(java.awt.Graphics2D g) {\n" +
            "        g.setColor(new java.awt.Color(20, 20, 20));\n" +
            "        g.fillRect(0, 0, 800, 600);\n" +
            "        g.setColor(java.awt.Color.WHITE);\n" +
            "        g.setFont(new java.awt.Font(\"Arial\", 1, 40));\n" +
            "        g.drawString(\"CRÉDITOS\", 290, 150);\n" +
            "        g.setFont(new java.awt.Font(\"Arial\", 0, 20));\n" +
            "        g.drawString(\"Desenvolvido com Antigravit\", 260, 250);\n" +
            "        g.drawString(\"Design: Você!\", 320, 300);\n" +
            "        drawButton(g, \"VOLTAR\", 300, 500, 200, 50);\n" +
            "    }\n";
        String insertHook = "private void drawInventory(java.awt.Graphics2D g) {";
        code = code.replace(insertHook, methods + "\n    " + insertHook);

        Files.write(f.toPath(), code.getBytes());
    }
}
