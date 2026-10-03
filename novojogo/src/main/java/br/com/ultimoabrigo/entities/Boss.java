package br.com.ultimoabrigo.entities;

import br.com.ultimoabrigo.assets.SoundSystem;
import br.com.ultimoabrigo.core.GameConfig;
import br.com.ultimoabrigo.world.Camera;
import br.com.ultimoabrigo.world.Obstacle;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;

/**
 * Boss - Classe base para os 3 grandes chefes de fim de fase:
 * Mr. X (Fase 1), Tyrant (Fase 2) e Nemesis (Fase 3).
 * Gerencia vida estendida, barra de vida no topo, múltiplas fases de combate,
 * recuo, flashes de dano e sequência de morte apocalíptica com tela tremendo.
 */
public abstract class Boss extends Entity {

    protected final String bossName;
    protected final String bossSubtitle;
    protected int hp;
    protected final int maxHp;
    protected int currentPhase = 1;

    protected boolean hurt = false;
    protected int hurtTimer = 0;
    protected boolean defeated = false;
    protected int defeatTimer = 0;
    protected final int MAX_DEFEAT_FRAMES = 180; // 3 segundos de explosões e tela tremendo

    protected int animTick = 0;
    protected boolean onGround = true;

    public Boss(float startX, float startY, int width, int height, String bossName, String bossSubtitle, int maxHp) {
        super(startX, startY, width, height);
        this.bossName = bossName;
        this.bossSubtitle = bossSubtitle;
        this.hp = maxHp;
        this.maxHp = maxHp;
    }

    @Override
    public void update() {
        animTick++;

        if (hurtTimer > 0) {
            hurtTimer--;
            if (hurtTimer == 0) {
                hurt = false;
            }
        }

        // Sequência dramática de derrota
        if (defeated) {
            defeatTimer++;
            vx = 0;
            return;
        }

        // Gravidade
        if (!onGround) {
            vy += GameConfig.GRAVITY;
            if (vy > GameConfig.MAX_FALL_SPEED) {
                vy = GameConfig.MAX_FALL_SPEED;
            }
        }

        x += vx;
        y += vy;

        float floorY = GameConfig.GROUND_Y - height;
        if (y >= floorY) {
            y = floorY;
            vy = 0;
            onGround = true;
        } else {
            onGround = false;
        }

        updateBounds();
    }

    public abstract void updateBossAI(
            Player player,
            List<Projectile> bossProjectiles,
            List<Particle> particles,
            Camera camera,
            List<Obstacle> obstacles,
            List<Zombie> minions
    );

    public void takeDamage(int dmg, List<Particle> particles, Camera camera, Player player) {
        if (defeated) return;

        hp -= dmg;
        hurt = true;
        hurtTimer = 8;
        SoundSystem.playZombieHit();

        // Respingos de sangue e impacto
        for (int i = 0; i < 4; i++) {
            particles.add(new Particle(x + width / 2, y + height / 2,
                    (float) (Math.random() * 6.0 - 3.0), (float) (-Math.random() * 4.0 - 1.0),
                    Particle.ParticleType.BLOOD, GameConfig.COLOR_BLOOD_DARK, 30, 5));
        }

        if (hp <= 0) {
            hp = 0;
            triggerDefeat(camera, player);
        }
    }

    protected void triggerDefeat(Camera camera, Player player) {
        defeated = true;
        defeatTimer = 0;
        SoundSystem.playExplosion();
        camera.shake(12.0f, 60);
        player.addScore(5000);
    }

    public void processDefeatExplosions(List<Particle> particles, Camera camera) {
        if (!defeated) return;

        // Gera explosões sucessivas sobre o corpo do boss
        if (defeatTimer % 10 == 0 && defeatTimer < MAX_DEFEAT_FRAMES - 20) {
            float expX = x + (float) (Math.random() * width);
            float expY = y + (float) (Math.random() * height);
            SoundSystem.playExplosion();
            camera.shake(8.0f, 15);

            for (int i = 0; i < 20; i++) {
                float ang = (float) (Math.random() * Math.PI * 2);
                float spd = (float) (Math.random() * 5.0 + 2.0);
                Color c = (i % 2 == 0) ? GameConfig.COLOR_METAL_SLUG_YELLOW : GameConfig.COLOR_DANGER_RED;
                particles.add(new Particle(expX, expY, (float) Math.cos(ang) * spd, (float) (Math.sin(ang) * spd - 1.5), Particle.ParticleType.EXPLOSION, c, 35, 12));
            }
        }
    }

    public void renderBossHealthBar(Graphics2D g) {
        int barW = 680;
        int barH = 18;
        int barX = (GameConfig.LOGICAL_WIDTH - barW) / 2;
        int barY = 32;

        // Fundo preto e borda metálica
        g.setColor(new Color(15, 15, 20, 230));
        g.fillRect(barX - 4, barY - 20, barW + 8, barH + 26);
        g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
        g.drawRect(barX - 4, barY - 20, barW + 8, barH + 26);

        // Nome do chefe e fase
        g.setFont(new Font("Impact", Font.PLAIN, 15));
        g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
        g.drawString(bossName.toUpperCase(), barX, barY - 5);

        g.setFont(new Font("Arial", Font.BOLD, 11));
        g.setColor(new Color(220, 220, 220));
        int subW = g.getFontMetrics().stringWidth(bossSubtitle);
        g.drawString(bossSubtitle, barX + barW - subW, barY - 5);

        // Barra de preenchimento de vida
        g.setColor(new Color(40, 15, 15));
        g.fillRect(barX, barY, barW, barH);

        float pct = Math.max(0f, (float) hp / maxHp);
        Color barColor = (pct > 0.5f) ? GameConfig.COLOR_DANGER_RED : new Color(255, 140, 20); // Amarelo/Laranja em fase 2
        g.setColor(barColor);
        g.fillRect(barX, barY, (int) (barW * pct), barH);

        // Brilho superior da barra
        g.setColor(new Color(255, 255, 255, 90));
        g.fillRect(barX, barY, (int) (barW * pct), barH / 2);

        // Borda dourada
        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barW, barH);
    }

    public boolean isDefeated() { return defeated; }
    public boolean isDefeatSequenceComplete() { return defeated && defeatTimer >= MAX_DEFEAT_FRAMES; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getCurrentPhase() { return currentPhase; }
    public String getBossName() { return bossName; }
}
