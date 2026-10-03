package br.com.ultimoabrigo.entities;

import br.com.ultimoabrigo.assets.ProceduralSprites;
import br.com.ultimoabrigo.assets.SoundSystem;
import br.com.ultimoabrigo.core.GameConfig;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

/**
 * RunnerZombie - Zumbi Corredor mutante, extremamente rápido e agressivo (HP 2, 150 pontos).
 * Telegrafa um salto/investida com agachamento de 0,3 s e salta vorazmente contra Elias Rocha.
 */
public class RunnerZombie extends Zombie {

    private boolean isTelegraphing = false;
    private int telegraphTimer = 0;
    private boolean isLeaping = false;
    private int leapCooldown = 0;

    public RunnerZombie(float startX, float startY) {
        super(startX, startY, GameConfig.RUNNER_WIDTH, GameConfig.RUNNER_HEIGHT, ZombieType.RUNNER, GameConfig.RUNNER_HP, GameConfig.RUNNER_BASE_SPEED, GameConfig.RUNNER_SCORE);
    }

    @Override
    public void updateAI(Player player, List<Particle> particles) {
        if (dead) return;

        if (leapCooldown > 0) leapCooldown--;

        float distToPlayer = player.getX() - x;
        float absDist = Math.abs(distToPlayer);

        facing = (distToPlayer > 0) ? 1 : -1;

        // Se está telegrafando o salto (0.3s)
        if (isTelegraphing) {
            vx = 0;
            telegraphTimer--;
            if (telegraphTimer <= 0) {
                // Dispara o salto feroz
                isTelegraphing = false;
                isLeaping = true;
                vx = facing * 7.2f;
                vy = -5.5f;
                onGround = false;
                SoundSystem.playRunnerScreech();
            }
            return;
        }

        // Se está no meio do salto
        if (isLeaping) {
            // Se colidiu com Elias durante o salto
            if (bounds.intersects(player.getBounds()) && !player.isDead()) {
                player.takeDamage(1);
                isLeaping = false;
                leapCooldown = 90;
            }
            if (onGround) {
                isLeaping = false;
                leapCooldown = 80;
            }
            return;
        }

        // Se estiver a uma distância de emboscada (entre 120 e 240 px), telegrafa o salto
        if (absDist >= 110 && absDist <= 240 && leapCooldown <= 0 && onGround && !player.isDead()) {
            isTelegraphing = true;
            telegraphTimer = GameConfig.RUNNER_TELEGRAPH_FRAMES; // 0.3 segundos (18 frames)
            vx = 0;
            return;
        }

        // Ataque corpo a corpo próximo caso colado no jogador
        if (absDist < 35 && attackCooldown <= 0 && !player.isDead()) {
            attackCooldown = 40;
            player.takeDamage(1);
        }

        // Corrida agressiva em perseguição a Elias
        if (!hurt) {
            vx = facing * baseSpeed;
        }
    }

    @Override
    public void render(Graphics2D g, float cameraX, float cameraY) {
        int drawX = (int) (x - cameraX);
        int drawY = (int) (y - cameraY);

        if (drawX < -80 || drawX > GameConfig.LOGICAL_WIDTH + 80) return;

        ProceduralSprites.renderRunner(
                g,
                drawX, drawY,
                facing,
                animTick,
                isTelegraphing,
                isLeaping,
                hurt,
                dead,
                deathTimer,
                corpseDecayTimer
        );

        // Barra de vida se ferido
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

    public boolean isTelegraphing() { return isTelegraphing; }
    public boolean isLeaping() { return isLeaping; }
}
