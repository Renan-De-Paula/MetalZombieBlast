package br.com.ultimoabrigo.entities;

import br.com.ultimoabrigo.assets.SoundSystem;
import br.com.ultimoabrigo.core.GameConfig;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

/**
 * Projectile - Representa tiros de armas de fogo, balotes de escopeta,
 * partículas de chamas, granadas e foguetes.
 */
public class Projectile extends Entity {

    public enum ProjectileType {
        PISTOL_BULLET,
        SHOTGUN_PELLET,
        RIFLE_BULLET,
        FLAME,
        GRENADE,
        BOSS_ROCKET
    }

    private final ProjectileType type;
    private final int damage;
    private final boolean fromPlayer;
    private int lifetime;
    private int bounceCount = 0;
    private boolean exploded = false;

    public Projectile(float x, float y, float vx, float vy, ProjectileType type, int damage, boolean fromPlayer, int lifetime) {
        super(x, y, getInitialWidth(type), getInitialHeight(type));
        this.vx = vx;
        this.vy = vy;
        this.type = type;
        this.damage = damage;
        this.fromPlayer = fromPlayer;
        this.lifetime = lifetime;
    }

    private static int getInitialWidth(ProjectileType type) {
        switch (type) {
            case GRENADE: return 12;
            case FLAME: return 14;
            case BOSS_ROCKET: return 20;
            default: return 8;
        }
    }

    private static int getInitialHeight(ProjectileType type) {
        switch (type) {
            case GRENADE: return 12;
            case FLAME: return 14;
            case BOSS_ROCKET: return 10;
            default: return 4;
        }
    }

    @Override
    public void update() {
        x += vx;
        y += vy;
        lifetime--;

        // Física da Granada
        if (type == ProjectileType.GRENADE) {
            vy += 0.42f; // Gravidade
            if (y + height >= GameConfig.GROUND_Y) {
                y = GameConfig.GROUND_Y - height;
                vy = -vy * 0.45f;
                vx *= 0.7f;
                bounceCount++;
                if (bounceCount >= 3 || Math.abs(vy) < 0.5f) {
                    explode();
                }
            }
        } else if (type == ProjectileType.FLAME) {
            vx *= 0.94f;
            vy += (float) (Math.sin(x * 0.05) * 0.2f);
            width = Math.min(32, width + 1);
            height = Math.min(32, height + 1);
        }

        updateBounds();

        if (lifetime <= 0 && !exploded) {
            if (type == ProjectileType.GRENADE) {
                explode();
            } else {
                active = false;
            }
        }
    }

    public void explode() {
        if (exploded) return;
        exploded = true;
        active = false;
        SoundSystem.playExplosion();
    }

    public void spawnExplosionParticles(List<Particle> particles) {
        if (!exploded) return;
        // Gera explosão volumosa e fumaça
        for (int i = 0; i < 28; i++) {
            float ang = (float) (Math.random() * Math.PI * 2);
            float spd = (float) (Math.random() * 6.0 + 1.0);
            float pvx = (float) (Math.cos(ang) * spd);
            float pvy = (float) (Math.sin(ang) * spd - 2.0);
            Color c = (i % 2 == 0) ? GameConfig.COLOR_METAL_SLUG_YELLOW : GameConfig.COLOR_DANGER_RED;
            particles.add(new Particle(x, y, pvx, pvy, Particle.ParticleType.EXPLOSION, c, 35, 12));
        }
        for (int i = 0; i < 15; i++) {
            float pvx = (float) (Math.random() * 3.0 - 1.5);
            float pvy = (float) (-Math.random() * 3.0 - 1.0);
            particles.add(new Particle(x, y, pvx, pvy, Particle.ParticleType.SMOKE, new Color(70, 70, 70), 50, 16));
        }
    }

    @Override
    public void render(Graphics2D g, float cameraX, float cameraY) {
        int drawX = (int) (x - cameraX);
        int drawY = (int) (y - cameraY);

        switch (type) {
            case PISTOL_BULLET:
                g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
                g.fillRect(drawX, drawY, width, height);
                g.setColor(Color.WHITE);
                g.fillRect(drawX + (vx < 0 ? 0 : width - 3), drawY, 3, height);
                break;

            case SHOTGUN_PELLET:
                g.setColor(new Color(255, 140, 30));
                g.fillOval(drawX, drawY, width, height);
                break;

            case RIFLE_BULLET:
                g.setColor(new Color(255, 230, 100));
                g.fillRect(drawX, drawY, width + 4, height);
                break;

            case FLAME:
                Color flameColor = lifetime % 2 == 0 ? new Color(255, 120, 20, 180) : new Color(255, 200, 30, 200);
                g.setColor(flameColor);
                g.fillOval(drawX, drawY, width, height);
                break;

            case GRENADE:
                // Granada verde oliva / cinza escuro
                g.setColor(new Color(60, 75, 45));
                g.fillOval(drawX, drawY, width, height);
                g.setColor(Color.DARK_GRAY);
                g.fillRect(drawX + width / 2 - 2, drawY - 3, 4, 3);
                break;

            case BOSS_ROCKET:
                g.setColor(new Color(190, 40, 40));
                g.fillRect(drawX, drawY, width, height);
                g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
                g.fillRect(drawX + (vx < 0 ? 0 : width - 4), drawY, 4, height);
                break;
        }
    }

    public ProjectileType getType() { return type; }
    public int getDamage() { return damage; }
    public boolean isFromPlayer() { return fromPlayer; }
    public boolean isExploded() { return exploded; }
}
