package br.com.ultimoabrigo.entities;

import br.com.ultimoabrigo.core.GameConfig;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Particle - Efeitos visuais efêmeros: faíscas de disparo, cápsulas de bala ejetadas,
 * respingos de sangue escuro, chamas, explosões e fumaça.
 */
public class Particle {

    public enum ParticleType {
        SPARK, CASING, BLOOD, FLAME, SMOKE, EXPLOSION
    }

    private float x;
    private float y;
    private float vx;
    private float vy;
    private final ParticleType type;
    private final Color color;
    private final int maxLife;
    private int life;
    private float size;
    private boolean active = true;

    public Particle(float x, float y, float vx, float vy, ParticleType type, Color color, int maxLife, float size) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.type = type;
        this.color = color;
        this.maxLife = maxLife;
        this.life = maxLife;
        this.size = size;
    }

    public void update() {
        x += vx;
        y += vy;
        life--;

        // Física específica por tipo
        if (type == ParticleType.CASING || type == ParticleType.BLOOD) {
            vy += 0.35f; // Gravidade
            if (y >= GameConfig.GROUND_Y) {
                y = GameConfig.GROUND_Y;
                vy = -vy * 0.3f;
                vx *= 0.6f;
            }
        } else if (type == ParticleType.SMOKE) {
            vy -= 0.1f; // Fumaça sobe
            size += 0.2f; // Expande
            vx *= 0.95f;
        } else if (type == ParticleType.EXPLOSION) {
            size += 0.6f;
        }

        if (life <= 0) {
            active = false;
        }
    }

    public void render(Graphics2D g, float cameraX, float cameraY) {
        int drawX = (int) (x - cameraX);
        int drawY = (int) (y - cameraY);

        float alpha = Math.max(0f, Math.min(1f, (float) life / maxLife));
        int alphaInt = (int) (alpha * 255);

        Color drawColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alphaInt);
        g.setColor(drawColor);

        int s = (int) Math.max(1, size);
        if (type == ParticleType.CASING) {
            g.fillRect(drawX, drawY, 3, 2);
        } else if (type == ParticleType.EXPLOSION || type == ParticleType.SMOKE || type == ParticleType.FLAME) {
            g.fillOval(drawX - s / 2, drawY - s / 2, s, s);
        } else {
            g.fillRect(drawX, drawY, s, s);
        }
    }

    public boolean isActive() {
        return active;
    }
}
