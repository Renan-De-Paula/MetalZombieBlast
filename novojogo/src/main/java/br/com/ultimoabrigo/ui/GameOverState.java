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
 * GameOverState - Tela de Game Over com opção de reviver no último checkpoint.
 */
public class GameOverState implements GameStateHandler {

    private final StateManager stateManager;
    private final String[] options = {"TENTAR NOVAMENTE (CHECKPOINT)", "MENU PRINCIPAL"};
    private int selectedIndex = 0;
    private int animTick = 0;

    public GameOverState(StateManager stateManager) {
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

        if (input.isKeyJustPressed(java.awt.event.KeyEvent.VK_W) || input.isKeyJustPressed(java.awt.event.KeyEvent.VK_UP)) {
            selectedIndex = (selectedIndex - 1 + options.length) % options.length;
        }
        if (input.isKeyJustPressed(java.awt.event.KeyEvent.VK_S) || input.isKeyJustPressed(java.awt.event.KeyEvent.VK_DOWN)) {
            selectedIndex = (selectedIndex + 1) % options.length;
        }

        if (input.isConfirmJustPressed()) {
            if (selectedIndex == 0) {
                stateManager.setState(GameState.PLAYING);
            } else {
                stateManager.setState(GameState.MENU);
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(new Color(20, 5, 5, 230));
        g.fillRect(0, 0, GameConfig.LOGICAL_WIDTH, GameConfig.LOGICAL_HEIGHT);

        int centerY = 190;
        g.setFont(new Font("Impact", Font.BOLD, 58));
        String title = "FIM DE JOGO";
        int titleW = g.getFontMetrics().stringWidth(title);
        g.setColor(Color.BLACK);
        g.drawString(title, (GameConfig.LOGICAL_WIDTH - titleW) / 2 + 3, centerY + 3);
        g.setColor(GameConfig.COLOR_DANGER_RED);
        g.drawString(title, (GameConfig.LOGICAL_WIDTH - titleW) / 2, centerY);

        g.setFont(new Font("SansSerif", Font.ITALIC, 16));
        String sub = "\"Elias sucumbiu aos horrores do vale... Mas a esperança não pode morrer.\"";
        int subW = g.getFontMetrics().stringWidth(sub);
        g.setColor(new Color(200, 200, 200));
        g.drawString(sub, (GameConfig.LOGICAL_WIDTH - subW) / 2, centerY + 45);

        // Opções
        g.setFont(new Font("Arial", Font.BOLD, 20));
        int optStartY = centerY + 120;
        for (int i = 0; i < options.length; i++) {
            boolean selected = (i == selectedIndex);
            String opt = options[i];
            if (selected) {
                g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
                String line = "▶  " + opt + "  ◀";
                g.drawString(line, (GameConfig.LOGICAL_WIDTH - g.getFontMetrics().stringWidth(line)) / 2, optStartY + (i * 42));
            } else {
                g.setColor(new Color(170, 170, 170));
                g.drawString(opt, (GameConfig.LOGICAL_WIDTH - g.getFontMetrics().stringWidth(opt)) / 2, optStartY + (i * 42));
            }
        }
    }

    @Override
    public void exit() {
    }
}
