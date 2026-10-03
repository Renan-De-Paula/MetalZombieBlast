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
 * PhaseClearState - Resumo de conclusão de fase com pontuação e estatísticas.
 */
public class PhaseClearState implements GameStateHandler {

    private final StateManager stateManager;
    private int currentStage = 1;
    private int zombiesKilled = 0;
    private int score = 0;
    private int animTick = 0;

    public PhaseClearState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    public void setPhaseStats(int stage, int zombiesKilled, int score) {
        this.currentStage = stage;
        this.zombiesKilled = zombiesKilled;
        this.score = score;
    }

    @Override
    public void enter() {
        animTick = 0;
    }

    @Override
    public void update(InputHandler input) {
        animTick++;
        if (input.isConfirmJustPressed()) {
            if (currentStage >= 3) {
                stateManager.setState(GameState.ENDING);
            } else {
                currentStage++;
                PlayingState playing = (PlayingState) stateManager.getStateHandler(GameState.PLAYING);
                if (playing != null) {
                    br.com.ultimoabrigo.entities.Player p = playing.getPlayer();
                    if (p != null) {
                        p.setCheckpoint(120, GameConfig.GROUND_Y - GameConfig.PLAYER_HEIGHT);
                    }
                    playing.initStage(currentStage, true);
                }
                stateManager.setState(GameState.PLAYING);
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(new Color(10, 20, 15, 230));
        g.fillRect(0, 0, GameConfig.LOGICAL_WIDTH, GameConfig.LOGICAL_HEIGHT);

        int centerY = 160;
        g.setFont(new Font("Impact", Font.BOLD, 54));
        String title = "FASE " + currentStage + " CONCLUÍDA!";
        int titleW = g.getFontMetrics().stringWidth(title);
        g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
        g.drawString(title, (GameConfig.LOGICAL_WIDTH - titleW) / 2, centerY);

        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.setColor(Color.WHITE);
        g.drawString("Distância percorrida: 1000 m / 1000 m (100%)", 280, centerY + 70);
        g.drawString("Inimigos eliminados: " + zombiesKilled, 280, centerY + 110);
        g.drawString("Pontuação total: " + score + " PTS", 280, centerY + 150);

        g.setFont(GameConfig.FONT_UI);
        g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
        String nextMsg = currentStage >= 3 ? "Pressione [ENTER / J] para ver o DESFECHO FINAL" : "Pressione [ENTER / J] para avançar à Fase " + (currentStage + 1);
        g.drawString(nextMsg, (GameConfig.LOGICAL_WIDTH - g.getFontMetrics().stringWidth(nextMsg)) / 2, 450);
    }

    @Override
    public void exit() {
    }
}
