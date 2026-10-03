package br.com.ultimoabrigo.entities;

import br.com.ultimoabrigo.assets.ProceduralSprites;
import br.com.ultimoabrigo.assets.SoundSystem;
import br.com.ultimoabrigo.core.GameConfig;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

/**
 * WalkerZombie - Zumbi Caminhante clássico infectado pelo vírus SDNA.
 * Lento e resistente (HP 3, 100 pontos). Avança em hordas em direção a Elias Rocha,
 * atacando por proximidade ao alcançar o herói.
 */
public class WalkerZombie extends Zombie {

    private boolean attacking = false;
    private int attackAnimTimer = 0;

    public WalkerZombie(float startX, float startY) {
        super(startX, startY, GameConfig.WALKER_WIDTH, GameConfig.WALKER_HEIGHT, ZombieType.WALKER, GameConfig.WALKER_HP, GameConfig.WALKER_BASE_SPEED, GameConfig.WALKER_SCORE);
    }

    @Override
    public void updateAI(Player player, List<Particle> particles) {
        if (dead) return;

        float distToPlayer = player.getX() - x;
        float absDist = Math.abs(distToPlayer);

        // Define a direção da perseguição no eixo X
        facing = (distToPlayer > 0) ? 1 : -1;

        // Rosnado atmosférico periódico
        if (animTick % 300 == 0 && Math.random() < 0.35 && absDist < 600) {
            SoundSystem.playZombieGroan();
        }

        // Ataque ao entrar no alcance corpo a corpo (~38 px)
        if (absDist < 38 && attackCooldown <= 0 && !player.isDead()) {
            attacking = true;
            attackAnimTimer = 20;
            attackCooldown = 50; // Intervalo entre ataques
            vx = 0;
            player.takeDamage(1);
        }

        if (attacking) {
            attackAnimTimer--;
            if (attackAnimTimer <= 0) {
                attacking = false;
            }
        } else if (!hurt) {
            // Avança na direção do jogador
            vx = facing * baseSpeed;
        }
    }

    @Override
    public void render(Graphics2D g, float cameraX, float cameraY) {
        int drawX = (int) (x - cameraX);
        int drawY = (int) (y - cameraY);

        if (drawX < -80 || drawX > GameConfig.LOGICAL_WIDTH + 80) return;

        ProceduralSprites.renderWalker(
                g,
                drawX, drawY,
                facing,
                animTick,
                attacking,
                hurt,
                dead,
                deathTimer,
                corpseDecayTimer
        );

        // Barra de vida simples acima do zumbi se tomou dano e ainda está vivo
        if (!dead && hp < maxHp) {
            int barW = width;
            int barH = 4;
            int barY = drawY - 8;
            g.setColor(Color.BLACK);
            g.fillRect(drawX, barY, barW, barH);
            g.setColor(GameConfig.COLOR_DANGER_RED);
            g.fillRect(drawX, barY, (int) (barW * ((float) hp / maxHp)), barH);
            g.setColor(Color.WHITE);
            g.drawRect(drawX, barY, barW, barH);
        }
    }
}
