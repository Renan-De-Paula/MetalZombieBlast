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
 * NemesisBoss - Chefe da Fase 3 (Bunker Militar).
 * Monstro mutante formidável.
 * Fase 1: Lança-foguetes e pulos altos.
 * Fase 2: Tentáculos chicoteadores.
 * Fase 3: Mutação Apex (Agitada, regeneração).
 */
public class NemesisBoss extends Boss {

    public enum BossState {
        IDLE,
        WALKING,
        JUMPING,
        SHOOTING_ROCKET,
        WHIPPING,
        ROARING,
        MUTATING,
        APEX_CHARGE
    }

    private BossState state = BossState.IDLE;
    private int stateTimer = 0;
    
    // Hitbox do tentáculo
    private final br.com.ultimoabrigo.util.AABB whipHitbox;

    public NemesisBoss(float startX, float startY) {
        super(startX, startY, 50, 95, "NEMESIS", "ARMA BIOLÓGICA SUPREMA", 120);
        this.whipHitbox = new br.com.ultimoabrigo.util.AABB(0, 0, 100, 30);
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

        // Transições de fase
        if (hp <= 80 && currentPhase == 1) {
            currentPhase = 2;
            state = BossState.ROARING;
            stateTimer = 45;
            vx = 0;
            SoundSystem.playBossAlarm();
            camera.shake(8.0f, 30);
        } else if (hp <= 40 && currentPhase == 2) {
            currentPhase = 3;
            state = BossState.MUTATING;
            stateTimer = 60;
            vx = 0;
            SoundSystem.playBossAlarm();
            camera.shake(12.0f, 40);
        }

        // Regeneração passiva na fase 3
        if (currentPhase == 3 && animTick % 60 == 0 && hp < 40) {
            hp += 1;
            particles.add(new Particle(x + width/2, y + height/2, 0, -2, Particle.ParticleType.SPARK, Color.GREEN, 20, 4));
        }

        float distToPlayer = player.getX() - x;
        float absDist = Math.abs(distToPlayer);
        facing = (distToPlayer > 0) ? 1 : -1;

        switch (state) {
            case IDLE:
            case WALKING:
                float moveSpeed = (currentPhase == 3) ? 3.5f : (currentPhase == 2) ? 2.5f : 1.5f;
                vx = facing * moveSpeed;
                
                if (currentPhase == 1) {
                    if (absDist > 200 && animTick % 100 == 0) {
                        state = BossState.SHOOTING_ROCKET;
                        stateTimer = 30;
                        vx = 0;
                    } else if (absDist < 150 && onGround && Math.random() < 0.05) {
                        state = BossState.JUMPING;
                        vy = -12.0f;
                        vx = facing * 4.0f;
                    }
                } else if (currentPhase == 2) {
                    if (absDist < 120) {
                        state = BossState.WHIPPING;
                        stateTimer = 20; // telegraph
                        vx = 0;
                    } else if (absDist > 150 && animTick % 80 == 0) {
                        state = BossState.JUMPING;
                        vy = -10.0f;
                        vx = facing * 5.0f;
                    }
                } else if (currentPhase == 3) {
                    if (absDist > 100 && animTick % 60 == 0) {
                        state = BossState.APEX_CHARGE;
                        stateTimer = 40;
                        vx = facing * 8.0f;
                    } else if (absDist <= 100) {
                        state = BossState.WHIPPING;
                        stateTimer = 15;
                        vx = 0;
                    }
                }
                break;
                
            case SHOOTING_ROCKET:
                vx = 0;
                stateTimer--;
                if (stateTimer == 10) {
                    float spawnX = (facing == 1) ? x + width : x - 20;
                    float spawnY = y + 20;
                    bossProjectiles.add(new Projectile(spawnX, spawnY, facing * 6.0f, 0, Projectile.ProjectileType.BOSS_ROCKET, 2, false, 120));
                    SoundSystem.playExplosion();
                    camera.shake(3.0f, 10);
                }
                if (stateTimer <= 0) state = BossState.WALKING;
                break;
                
            case WHIPPING:
                vx = 0;
                stateTimer--;
                if (stateTimer <= 0) {
                    // Causa dano
                    float hx = (facing == 1) ? x + width : x - 100;
                    whipHitbox.set(hx, y + 30, 100, 30);
                    if (whipHitbox.intersects(player.getBounds())) {
                        player.takeDamage(1);
                    }
                    SoundSystem.playWrenchHit();
                    state = BossState.WALKING;
                }
                break;

            case JUMPING:
                if (onGround && vy >= 0) {
                    state = BossState.WALKING;
                    camera.shake(5.0f, 15);
                    SoundSystem.playExplosion();
                }
                break;

            case APEX_CHARGE:
                stateTimer--;
                if (bounds.intersects(player.getBounds())) {
                    player.takeDamage(2);
                }
                if (stateTimer <= 0 || !onGround) {
                    state = BossState.WALKING;
                }
                break;

            case ROARING:
            case MUTATING:
                stateTimer--;
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

        ProceduralSprites.renderNemesis(
                g,
                drawX, drawY,
                facing,
                animTick,
                state.name(),
                currentPhase,
                hurt,
                defeated,
                defeatTimer
        );

        renderBossHealthBar(g);
    }
}
