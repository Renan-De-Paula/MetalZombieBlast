package br.com.ultimoabrigo.entities;

import br.com.ultimoabrigo.assets.ProceduralSprites;
import br.com.ultimoabrigo.assets.SoundSystem;
import br.com.ultimoabrigo.core.GameConfig;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

/**
 * TankZombie - Zumbi Caminhante clÃ¡ssico infectado pelo vÃ­rus SDNA.
 * Lento e resistente (HP 3, 100 pontos). AvanÃ§a em hordas em direÃ§Ã£o a Elias Rocha,
 * atacando por proximidade ao alcanÃ§ar o herÃ³i.
 */
public class TankZombie extends Zombie {

    private boolean attacking = false;
    private int attackAnimTimer = 0;

    public TankZombie(float startX, float startY) {
        super(startX, startY, GameConfig.TANK_WIDTH, GameConfig.TANK_HEIGHT, ZombieType.TANK, GameConfig.TANK_HP, GameConfig.TANK_BASE_SPEED, GameConfig.TANK_SCORE);
    }

    @Override
    public void updateAI(Player player, List<Particle> particles) {
        if (dead) return;

        float distToPlayer = player.getX() - x;
        float absDist = Math.abs(distToPlayer);

        // Define a direÃ§Ã£o da perseguiÃ§Ã£o no eixo X
        facing = (distToPlayer > 0) ? 1 : -1;

        // Rosnado atmosfÃ©rico periÃ³dico
        if (animTick % 300 == 0 && Math.random() < 0.35 && absDist < 600) {
            SoundSystem.playZombieGroan();
        }

        // Ataque ao entrar no alcance corpo a corpo (~38 px)
        if (absDist < 38 && attackCooldown <= 0 && !player.isDead()) {
            attacking = true;
            attackAnimTimer = 20;
            attackCooldown = 50; // Intervalo entre ataques
            vx = 0;
            player.takeDamage(2); // Mais dano no impacto
        }

        if (attacking) {
            attackAnimTimer--;
            if (attackAnimTimer <= 0) {
                attacking = false;
            }
        } else if (!hurt) {
            // AvanÃ§a na direÃ§Ã£o do jogador
            vx = facing * baseSpeed;
        }
    }

    @Override
    public void render(Graphics2D g, float cameraX, float cameraY) {
        int drawX = (int) (x - cameraX);
        int drawY = (int) (y - cameraY);

        if (drawX < -80 || drawX > GameConfig.LOGICAL_WIDTH + 80) return;

        ProceduralSprites.renderTank(
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

        // Barra de vida simples acima do zumbi se tomou dano e ainda estÃ¡ vivo
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
