package br.com.ultimoabrigo.entities;

import br.com.ultimoabrigo.assets.ProceduralSprites;
import br.com.ultimoabrigo.assets.SoundSystem;
import br.com.ultimoabrigo.core.GameConfig;
import br.com.ultimoabrigo.world.Camera;
import br.com.ultimoabrigo.world.Obstacle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

/**
 * MrXBoss - Chefe da Fase 1 (Floresta do Cedro e Estrada).
 * Homem colossal de sobretudo longo e chapéu, implacável.
 * Ataques:
 * 1. Soco Pesado Telegrafado (curto alcance, dano 2 corações).
 * 2. Investida em Linha Reta (atravessa a arena; bate na parede se o jogador pular por cima).
 * 3. Arremesso de Destroços (bloco de motor em arco parabólico).
 * Mecânica: Ao atingir 50% de HP, rasga o sobretudo, entra em fúria (Fase 2) e fica +50% mais veloz.
 */
public class MrXBoss extends Boss {

    public enum BossState {
        WALKING,
        TELEGRAPH_PUNCH,
        PUNCHING,
        TELEGRAPH_CHARGE,
        CHARGING,
        STUNNED,
        THROW_DEBRIS
    }

    private BossState state = BossState.WALKING;
    private int stateTimer = 0;
    private boolean trenchcoatTorn = false;

    // Hitbox de ataque do soco
    private final br.com.ultimoabrigo.util.AABB punchHitbox;

    public MrXBoss(float startX, float startY) {
        super(startX, startY, 60, 102, "MR. X", "O INCANSÁVEL DO VALE DO CEDRO", 65);
        this.punchHitbox = new br.com.ultimoabrigo.util.AABB(0, 0, 58, 65);
    }

    @Override
    public void updateBossAI(
            Player player,
            List<Projectile> bossProjectiles,
            List<Particle> particles,
            Camera camera,
            List<Obstacle> obstacles,
            List<Zombie> minions
    ) {
        if (defeated) {
            processDefeatExplosions(particles, camera);
            return;
        }

        // Transição para a Fase 2 (rasga o sobretudo aos 50% de vida)
        if (hp <= maxHp / 2 && !trenchcoatTorn) {
            trenchcoatTorn = true;
            currentPhase = 2;
            SoundSystem.playBossAlarm();
            camera.shake(10.0f, 35);
            // Estilhaços de pano e sangue
            for (int i = 0; i < 20; i++) {
                particles.add(new Particle(x + width / 2, y + height / 2,
                        (float) (Math.random() * 8.0 - 4.0), (float) (-Math.random() * 5.0 - 1.0),
                        Particle.ParticleType.SPARK, new Color(40, 40, 45), 40, 6));
            }
        }

        float distToPlayer = player.getX() - x;
        float absDist = Math.abs(distToPlayer);
        facing = (distToPlayer > 0) ? 1 : -1;

        float moveSpeed = (currentPhase == 2) ? 2.5f : 1.6f;

        switch (state) {
            case WALKING:
                vx = facing * moveSpeed;

                // Se colado no jogador, inicia o soco pesado telegrafado
                if (absDist < 60) {
                    state = BossState.TELEGRAPH_PUNCH;
                    stateTimer = (currentPhase == 2) ? 16 : 24; // 0.4s telegrafando
                    vx = 0;
                }
                // Se a média distância (180px a 360px), chance de disparar investida ou arremessar destroço
                else if (absDist > 160 && animTick % 90 == 0) {
                    if (Math.random() < 0.55) {
                        state = BossState.TELEGRAPH_CHARGE;
                        stateTimer = 22;
                        vx = 0;
                        camera.shake(4.0f, 15);
                    } else {
                        state = BossState.THROW_DEBRIS;
                        stateTimer = 30;
                        vx = 0;
                    }
                }
                break;

            case TELEGRAPH_PUNCH:
                vx = 0;
                stateTimer--;
                if (stateTimer <= 0) {
                    state = BossState.PUNCHING;
                    stateTimer = 18;
                    SoundSystem.playWrenchHit();
                    camera.shake(6.0f, 12);

                    // Atualiza hitbox do soco
                    float hx = (facing == 1) ? x + width : x - 58;
                    punchHitbox.set(hx, y + 20, 58, 65);

                    if (punchHitbox.intersects(player.getBounds())) {
                        player.takeDamage(2); // Dano pesado
                    }
                }
                break;

            case PUNCHING:
                vx = 0;
                stateTimer--;
                if (stateTimer <= 0) {
                    state = BossState.WALKING;
                }
                break;

            case TELEGRAPH_CHARGE:
                vx = 0;
                stateTimer--;
                if (stateTimer <= 0) {
                    state = BossState.CHARGING;
                    stateTimer = (currentPhase == 2) ? 75 : 60;
                    SoundSystem.playExplosion();
                }
                break;

            case CHARGING:
                vx = facing * ((currentPhase == 2) ? 8.5f : 7.2f);
                camera.shake(4.0f, 6);

                // Dano por atropelamento se atingir o jogador
                if (bounds.intersects(player.getBounds())) {
                    player.takeDamage(1);
                }

                // Dano em obstáculos no caminho
                for (Obstacle obs : obstacles) {
                    if (obs.isActive() && bounds.intersects(obs.getBounds())) {
                        obs.takeDamage(10, particles, minions, null, player, camera);
                    }
                }

                stateTimer--;
                // Se bater no limite da arena (31040 ou 32000), fica atordoado
                if (x <= 31050 || x + width >= GameConfig.STAGE_WIDTH_PIXELS - 10 || stateTimer <= 0) {
                    state = BossState.STUNNED;
                    stateTimer = 50; // Atordoado por quase 1 segundo
                    vx = 0;
                    camera.shake(8.0f, 20);
                    SoundSystem.playExplosion();
                }
                break;

            case STUNNED:
                vx = 0;
                stateTimer--;
                if (stateTimer <= 0) {
                    state = BossState.WALKING;
                }
                break;

            case THROW_DEBRIS:
                vx = 0;
                stateTimer--;
                if (stateTimer == 15) {
                    // Arremessa bloco de destroços pesado
                    float spawnX = (facing == 1) ? x + width + 5 : x - 25;
                    float spawnY = y + 10;
                    float dvx = facing * 7.5f;
                    float dvy = -7.0f;
                    bossProjectiles.add(new Projectile(spawnX, spawnY, dvx, dvy, Projectile.ProjectileType.BOSS_ROCKET, 2, false, 90));
                    SoundSystem.playWrenchHit();
                    camera.shake(5.0f, 12);
                }
                if (stateTimer <= 0) {
                    state = BossState.WALKING;
                }
                break;
        }
    }

    @Override
    public void render(Graphics2D g, float cameraX, float cameraY) {
        int drawX = (int) (x - cameraX);
        int drawY = (int) (y - cameraY);

        if (drawX < -150 || drawX > GameConfig.LOGICAL_WIDTH + 150) return;

        ProceduralSprites.renderMrX(
                g,
                drawX, drawY,
                facing,
                animTick,
                state,
                trenchcoatTorn,
                hurt,
                defeated,
                defeatTimer
        );

        // Barra de vida imponente no topo
        renderBossHealthBar(g);
    }

    public BossState getState() { return state; }
    public boolean isTrenchcoatTorn() { return trenchcoatTorn; }
}
