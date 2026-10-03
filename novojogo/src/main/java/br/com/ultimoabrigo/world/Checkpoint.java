package br.com.ultimoabrigo.world;

import br.com.ultimoabrigo.assets.SoundSystem;
import br.com.ultimoabrigo.core.GameConfig;
import br.com.ultimoabrigo.entities.Entity;
import br.com.ultimoabrigo.entities.Player;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * Checkpoint - Ponto de salvamento estratégico posicionado a cada 250 metros
 * (250m = 8.000px, 500m = 16.000px, 750m = 24.000px).
 */
public class Checkpoint extends Entity {

    private final int meterDistance;
    private boolean activated = false;
    private int animTick = 0;

    public Checkpoint(int meterDistance) {
        super(meterDistance * GameConfig.PIXELS_PER_METER, GameConfig.GROUND_Y - 95, 48, 95);
        this.meterDistance = meterDistance;
    }

    @Override
    public void update() {
        animTick++;
    }

    public boolean checkPlayerActivation(Player player) {
        if (!activated && bounds.intersects(player.getBounds())) {
            activated = true;
            // Salva coordenadas de respawn no jogador
            player.setCheckpoint(x + 10, GameConfig.GROUND_Y - GameConfig.PLAYER_HEIGHT);
            SoundSystem.playPickup();
            return true;
        }
        return false;
    }

    @Override
    public void render(Graphics2D g, float cameraX, float cameraY) {
        int drawX = (int) (x - cameraX);
        int drawY = (int) (y - cameraY);

        if (drawX < -100 || drawX > GameConfig.LOGICAL_WIDTH + 100) {
            return;
        }

        // Poste metálico do transmissor militar
        g.setColor(new Color(50, 55, 60));
        g.fillRect(drawX + 20, drawY + 20, 8, height - 20);

        // Caixa da bateria e rádio de emergência na base
        g.setColor(new Color(65, 75, 55));
        g.fillRect(drawX + 8, drawY + height - 35, 32, 35);
        g.setColor(Color.BLACK);
        g.drawRect(drawX + 8, drawY + height - 35, 32, 35);

        // Placa indicativa com a quilometragem
        g.setColor(new Color(30, 30, 35));
        g.fillRect(drawX - 10, drawY + 8, 68, 22);
        g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
        g.drawRect(drawX - 10, drawY + 8, 68, 22);

        g.setFont(new Font("Monospaced", Font.BOLD, 11));
        g.setColor(Color.WHITE);
        g.drawString(meterDistance + " METROS", drawX - 6, drawY + 23);

        // Luz sinalizadora no topo (Vermelha piscando se inativo, Verde brilhante quando ativado)
        boolean blink = (animTick / 12) % 2 == 0;
        int lightY = drawY - 4;
        if (activated) {
            g.setColor(new Color(40, 255, 40));
            g.fillOval(drawX + 19, lightY, 10, 10);
            g.setColor(new Color(150, 255, 150, 140));
            g.fillOval(drawX + 16, lightY - 3, 16, 16);
        } else {
            g.setColor(blink ? GameConfig.COLOR_DANGER_RED : new Color(120, 20, 20));
            g.fillOval(drawX + 19, lightY, 10, 10);
        }
    }

    public int getMeterDistance() { return meterDistance; }
    public boolean isActivated() { return activated; }
}
