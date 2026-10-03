package br.com.ultimoabrigo.entities;

import br.com.ultimoabrigo.assets.ProceduralSprites;
import br.com.ultimoabrigo.assets.SoundSystem;
import br.com.ultimoabrigo.core.GameConfig;
import br.com.ultimoabrigo.util.AABB;
import br.com.ultimoabrigo.world.Camera;
import br.com.ultimoabrigo.world.Obstacle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

/**
 * TyrantBoss - Chefe da Fase 2 (Cidade Velha, Delegacia e Clínica Rural).
 * Mutante gigante com garra colossal, pele pálida cicatrizada e coração exposto.
 * Ataques:
 * 1. Golpe com garra em arco (alcance médio, dano 2 corações).
 * 2. Salto com impacto no chão (gera onda de choque sísmica no solo que exige pulo).
 * 3. Investida furiosa em linha reta (ao errar e colidir com a parede, fica atordoado
 *    e expõe seu coração vulnerável a dano triplo crítico).
 * 4. Invocação de zumbis na arena quando em vida baixa.
 */
public class TyrantBoss extends Boss {

    public enum TyrantState {
        PROWLING,
        TELEGRAPH_CLAW,
        SWEEPING_CLAW,
        JUMPING_SLAM,
        AIRBORNE,
        SLAM_LANDING,
        TELEGRAPH_CHARGE,
        CHARGING,
        STUNNED_WEAK_POINT,
        ROARING_SUMMON
    }

    private TyrantState state = TyrantState.PROWLING;
    private int stateTimer = 0;
    private boolean heartExposed = false;
    private boolean summonedHorde = false;

    // Hitbox da garra
    private final AABB clawHitbox;
    // Ponto fraco do coração exposto no peito
    private final AABB heartWeakSpot;
    // Onda de choque rasteira do salto
    private boolean shockwaveActive = false;
    private float shockwaveX = 0;
    private float shockwaveSpeed = 0;

    public TyrantBoss(float startX, float startY) {
        super(startX, startY, 68, 108, "TYRANT", "MUTANTE DA CLÍNICA RURAL", 85);
        this.clawHitbox = new AABB(0, 0, 75, 70);
        this.heartWeakSpot = new AABB(0, 0, 24, 24);
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

        // Atualiza onda de choque se ativa
        if (shockwaveActive) {
            shockwaveX += shockwaveSpeed;
            // Se colidir com Elias no solo
            if (Math.abs(player.getX() - shockwaveX) < 25 && player.isOnGround() && !player.isCrouched()) {
                player.takeDamage(1);
            }
            // Partículas da onda de poeira
            particles.add(new Particle(shockwaveX, GameConfig.GROUND_Y - 8,
                    (float) (Math.random() * 2.0 - 1.0), -2.5f,
                    Particle.ParticleType.SPARK, new Color(180, 160, 140), 16, 5));

            if (shockwaveX < 31040 || shockwaveX > GameConfig.STAGE_WIDTH_PIXELS) {
                shockwaveActive = false;
            }
        }

        // Invocação de zumbis em vida baixa (HP <= 30)
        if (hp <= 30 && !summonedHorde) {
            summonedHorde = true;
            state = TyrantState.ROARING_SUMMON;
            stateTimer = 45;
            vx = 0;
            SoundSystem.playRunnerScreech();
            camera.shake(8.0f, 30);
            // Invoca zumbis na arena
            minions.add(new RunnerZombie(x - 180, GameConfig.GROUND_Y - GameConfig.RUNNER_HEIGHT));
            minions.add(new WalkerZombie(x + width + 140, GameConfig.GROUND_Y - GameConfig.WALKER_HEIGHT));
            return;
        }

        float distToPlayer = player.getX() - x;
        float absDist = Math.abs(distToPlayer);
        if (state != TyrantState.CHARGING && state != TyrantState.STUNNED_WEAK_POINT) {
            facing = (distToPlayer > 0) ? 1 : -1;
        }

        switch (state) {
            case PROWLING:
                heartExposed = false;
                vx = facing * 2.2f;

                // Golpe de Garra se estiver a curta distância
                if (absDist < 75) {
                    state = TyrantState.TELEGRAPH_CLAW;
                    stateTimer = 20; // 0.33s telegrafando
                    vx = 0;
                }
                // Salto com impacto no chão se a média/longa distância
                else if (absDist > 180 && absDist < 380 && animTick % 90 == 0) {
                    if (Math.random() < 0.50) {
                        state = TyrantState.JUMPING_SLAM;
                        stateTimer = 16;
                        vx = 0;
                    } else {
                        state = TyrantState.TELEGRAPH_CHARGE;
                        stateTimer = 25;
                        vx = 0;
                    }
                }
                break;

            case TELEGRAPH_CLAW:
                vx = 0;
                stateTimer--;
                if (stateTimer <= 0) {
                    state = TyrantState.SWEEPING_CLAW;
                    stateTimer = 18;
                    SoundSystem.playWrenchHit();
                    camera.shake(5.0f, 10);

                    float hx = (facing == 1) ? x + width : x - 75;
                    clawHitbox.set(hx, y + 25, 75, 70);
                    if (clawHitbox.intersects(player.getBounds())) {
                        player.takeDamage(2);
                    }
                }
                break;

            case SWEEPING_CLAW:
                vx = 0;
                stateTimer--;
                if (stateTimer <= 0) {
                    state = TyrantState.PROWLING;
                }
                break;

            case JUMPING_SLAM:
                vx = 0;
                stateTimer--;
                if (stateTimer <= 0) {
                    state = TyrantState.AIRBORNE;
                    vy = -12.5f;
                    vx = facing * 5.0f;
                    onGround = false;
                    SoundSystem.playJump();
                }
                break;

            case AIRBORNE:
                // Quando aterrissa com força
                if (onGround) {
                    state = TyrantState.SLAM_LANDING;
                    stateTimer = 28;
                    vx = 0;
                    SoundSystem.playExplosion();
                    camera.shake(11.0f, 30);

                    // Dispara onda de choque pelo chão em ambas as direções
                    shockwaveActive = true;
                    shockwaveX = x + width / 2;
                    shockwaveSpeed = facing * 7.5f;

                    // Dano direto no impacto se Elias estiver embaixo
                    if (bounds.intersects(player.getBounds())) {
                        player.takeDamage(2);
                    }
                }
                break;

            case SLAM_LANDING:
                vx = 0;
                stateTimer--;
                if (stateTimer <= 0) {
                    state = TyrantState.PROWLING;
                }
                break;

            case TELEGRAPH_CHARGE:
                vx = 0;
                stateTimer--;
                if (stateTimer <= 0) {
                    state = TyrantState.CHARGING;
                    stateTimer = 70;
                    SoundSystem.playExplosion();
                }
                break;

            case CHARGING:
                vx = facing * 9.2f;
                camera.shake(5.0f, 6);

                if (bounds.intersects(player.getBounds())) {
                    player.takeDamage(2);
                }

                // Dano em obstáculos no caminho
                for (Obstacle obs : obstacles) {
                    if (obs.isActive() && bounds.intersects(obs.getBounds())) {
                        obs.takeDamage(12, particles, minions, null, player, camera);
                    }
                }

                stateTimer--;
                // Ao colidir violentamente com a parede da arena
                if (x <= 31050 || x + width >= GameConfig.STAGE_WIDTH_PIXELS - 12 || stateTimer <= 0) {
                    state = TyrantState.STUNNED_WEAK_POINT;
                    stateTimer = 108; // 1.8 segundos atordoado com ponto fraco exposto!
                    heartExposed = true;
                    vx = 0;
                    camera.shake(12.0f, 35);
                    SoundSystem.playExplosion();
                }
                break;

            case STUNNED_WEAK_POINT:
                vx = 0;
                heartExposed = true;
                // Atualiza área do coração exposto no peito
                float hx = (facing == 1) ? x + 18 : x + width - 38;
                heartWeakSpot.set(hx, y + 28, 22, 22);

                stateTimer--;
                if (stateTimer <= 0) {
                    heartExposed = false;
                    state = TyrantState.PROWLING;
                }
                break;

            case ROARING_SUMMON:
                vx = 0;
                stateTimer--;
                if (stateTimer <= 0) {
                    state = TyrantState.PROWLING;
                }
                break;
        }
    }

    @Override
    public void takeDamage(int dmg, List<Particle> particles, Camera camera, Player player) {
        if (defeated) return;

        // Se o coração estiver exposto durante o atordoamento, toma dano CRÍTICO TRIPLO!
        if (heartExposed) {
            int critDmg = dmg * 3;
            super.takeDamage(critDmg, particles, camera, player);
            // Partículas de sangue crítico pulsante
            for (int i = 0; i < 10; i++) {
                particles.add(new Particle(heartWeakSpot.getCenterX(), heartWeakSpot.getCenterY(),
                        (float) (Math.random() * 8.0 - 4.0), (float) (-Math.random() * 5.0 - 1.0),
                        Particle.ParticleType.BLOOD, new Color(255, 40, 40), 35, 6));
            }
            camera.shake(6.0f, 15);
        } else {
            // Casca blindada reduz o dano normal pela metade
            int mitigated = Math.max(1, dmg / 2);
            super.takeDamage(mitigated, particles, camera, player);
        }
    }

    @Override
    public void render(Graphics2D g, float cameraX, float cameraY) {
        int drawX = (int) (x - cameraX);
        int drawY = (int) (y - cameraY);

        if (drawX < -150 || drawX > GameConfig.LOGICAL_WIDTH + 150) return;

        ProceduralSprites.renderTyrant(
                g,
                drawX, drawY,
                facing,
                animTick,
                state,
                heartExposed,
                hurt,
                defeated,
                defeatTimer
        );

        // Barra de vida imponente no topo
        renderBossHealthBar(g);
    }

    public boolean isHeartExposed() { return heartExposed; }
    public TyrantState getTyrantState() { return state; }
}
