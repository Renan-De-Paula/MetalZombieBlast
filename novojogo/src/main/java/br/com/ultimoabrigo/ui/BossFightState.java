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
 * BossFightState - Estado para arena fechada de combate contra chefes.
 */
public class BossFightState implements GameStateHandler {

    private final StateManager stateManager;
    private int currentBossIndex = 1; // 1: Mr. X, 2: Tyrant, 3: Nemesis
    private int animTick = 0;
    private boolean dangerBannerActive = true;
    private int dangerTimer = 180; // 3 segundos de alerta

    public BossFightState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    public void setBoss(int bossIndex) {
        this.currentBossIndex = bossIndex;
    }

    @Override
    public void enter() {
        animTick = 0;
        dangerBannerActive = true;
        dangerTimer = 180;
    }

    @Override
    public void update(InputHandler input) {
        animTick++;
        if (dangerTimer > 0) {
            dangerTimer--;
            if (dangerTimer == 0) {
                dangerBannerActive = false;
            }
        }

        if (input.isPauseJustPressed()) {
            stateManager.setState(GameState.PAUSED);
        }
    }

    @Override
    public void render(Graphics2D g) {
        // Fundo de arena de combate
        g.setColor(new Color(25, 15, 20));
        g.fillRect(0, 0, GameConfig.LOGICAL_WIDTH, GameConfig.LOGICAL_HEIGHT);

        // Alerta dramático de PERIGO estilo arcade
        if (dangerBannerActive) {
            boolean blink = (animTick / 10) % 2 == 0;
            if (blink) {
                g.setColor(new Color(200, 20, 20, 190));
                g.fillRect(0, 200, GameConfig.LOGICAL_WIDTH, 90);
                g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
                g.setFont(new Font("Impact", Font.BOLD, 48));
                String warning = "⚠  PERIGO! ENCONTRO COM CHEFE  ⚠";
                int w = g.getFontMetrics().stringWidth(warning);
                g.drawString(warning, (GameConfig.LOGICAL_WIDTH - w) / 2, 265);
            }
        }

        // Barra de vida do Boss no topo
        renderBossHealthBar(g);
    }

    private void renderBossHealthBar(Graphics2D g) {
        String bossName;
        switch (currentBossIndex) {
            case 2: bossName = "TYRANT — MUTANTE DA CLÍNICA RURAL"; break;
            case 3: bossName = "NEMESIS — SUPEREXPERIMENTO MILITAR"; break;
            default: bossName = "MR. X — O INCANSÁVEL DO VALE DO CEDRO"; break;
        }

        int barW = 600;
        int barH = 20;
        int barX = (GameConfig.LOGICAL_WIDTH - barW) / 2;
        int barY = 35;

        g.setColor(Color.BLACK);
        g.fillRect(barX - 2, barY - 2, barW + 4, barH + 4);

        g.setColor(GameConfig.COLOR_DANGER_RED);
        g.fillRect(barX, barY, barW, barH);

        g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
        g.drawRect(barX - 2, barY - 2, barW + 4, barH + 4);

        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.setColor(Color.WHITE);
        g.drawString(bossName, barX, barY - 8);
    }

    @Override
    public void exit() {
    }
}
