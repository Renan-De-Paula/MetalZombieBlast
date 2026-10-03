package br.com.ultimoabrigo.ui;

import br.com.ultimoabrigo.core.GameConfig;
import br.com.ultimoabrigo.core.GameState;
import br.com.ultimoabrigo.core.GameStateHandler;
import br.com.ultimoabrigo.core.InputHandler;
import br.com.ultimoabrigo.core.StateManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * PauseState - Overlay de pausa que preserva o quadro atual do jogo por baixo.
 */
public class PauseState implements GameStateHandler {

    private final StateManager stateManager;
    private final String[] options = {"CONTINUAR", "REINICIAR DO CHECKPOINT", "MENU PRINCIPAL"};
    private int selectedIndex = 0;
    private int animTick = 0;

    public PauseState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void enter() {
        selectedIndex = 0;
        animTick = 0;
    }

    @Override
    public void update(InputHandler input) {
        animTick++;

        if (input.isPauseJustPressed()) {
            stateManager.setState(GameState.PLAYING);
            return;
        }

        if (input.isKeyJustPressed(java.awt.event.KeyEvent.VK_W) || input.isKeyJustPressed(java.awt.event.KeyEvent.VK_UP)) {
            selectedIndex--;
            if (selectedIndex < 0) selectedIndex = options.length - 1;
        }
        if (input.isKeyJustPressed(java.awt.event.KeyEvent.VK_S) || input.isKeyJustPressed(java.awt.event.KeyEvent.VK_DOWN)) {
            selectedIndex++;
            if (selectedIndex >= options.length) selectedIndex = 0;
        }

        if (input.isConfirmJustPressed()) {
            switch (selectedIndex) {
                case 0: // Continuar
                    stateManager.setState(GameState.PLAYING);
                    break;
                case 1: // Reiniciar Checkpoint
                    stateManager.setState(GameState.PLAYING);
                    break;
                case 2: // Menu Principal
                    stateManager.setState(GameState.MENU);
                    break;
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        // Escurece o jogo ao fundo
        g.setColor(new Color(0, 0, 0, 185));
        g.fillRect(0, 0, GameConfig.LOGICAL_WIDTH, GameConfig.LOGICAL_HEIGHT);

        // Caixa de diálogo
        int boxW = 420;
        int boxH = 260;
        int boxX = (GameConfig.LOGICAL_WIDTH - boxW) / 2;
        int boxY = (GameConfig.LOGICAL_HEIGHT - boxH) / 2;

        g.setColor(new Color(25, 25, 35, 240));
        g.fillRoundRect(boxX, boxY, boxW, boxH, 12, 12);
        g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
        g.drawRoundRect(boxX, boxY, boxW, boxH, 12, 12);

        // Título de pausa
        g.setFont(new Font("Impact", Font.BOLD, 36));
        g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
        String title = "JOGO PAUSADO";
        int titleW = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (GameConfig.LOGICAL_WIDTH - titleW) / 2, boxY + 55);

        // Opções
        g.setFont(new Font("Arial", Font.BOLD, 18));
        int startY = boxY + 115;
        for (int i = 0; i < options.length; i++) {
            boolean selected = (i == selectedIndex);
            String opt = options[i];
            if (selected) {
                g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
                String line = "▶  " + opt + "  ◀";
                g.drawString(line, (GameConfig.LOGICAL_WIDTH - g.getFontMetrics().stringWidth(line)) / 2, startY + (i * 38));
            } else {
                g.setColor(new Color(180, 180, 180));
                g.drawString(opt, (GameConfig.LOGICAL_WIDTH - g.getFontMetrics().stringWidth(opt)) / 2, startY + (i * 38));
            }
        }
    }

    @Override
    public void exit() {
    }
}
