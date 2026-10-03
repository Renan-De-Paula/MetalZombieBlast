package br.com.ultimoabrigo.entities;

import br.com.ultimoabrigo.assets.SoundSystem;
import br.com.ultimoabrigo.core.GameConfig;

import java.awt.Graphics2D;
import java.util.List;

/**
 * Zombie - Classe abstrata base para os zumbis infectados pelo vírus SDNA.
 * Gerencia vida, estados de animação, dano, recuo (knockback), sangue escuro e decomposição de cadáver.
 */
public abstract class Zombie extends Entity {

    public enum ZombieType {
        WALKER, RUNNER, TANK
    }

    protected final ZombieType zombieType;
    protected int hp;
    protected final int maxHp;
    protected float baseSpeed;
    protected int scoreValue;
    protected int attackCooldown = 0;

    // Estados de dano e morte
    protected boolean hurt = false;
    protected int hurtTimer = 0;
    protected boolean dead = false;
    protected int deathTimer = 0;
    protected int corpseDecayTimer = 180; // 3 segundos antes do corpo afundar e sumir

    // Animação
    protected int animTick = 0;
    protected boolean onGround = true;
    protected float customDropRate = 0.35f;

    public Zombie(float x, float y, int width, int height, ZombieType zombieType, int hp, float baseSpeed, int scoreValue) {
        super(x, y, width, height);
        this.zombieType = zombieType;
        this.hp = hp;
        this.maxHp = hp;
        this.baseSpeed = baseSpeed;
        this.scoreValue = scoreValue;
    }

    public void applySpeedMultiplier(float mult) {
        this.baseSpeed *= mult;
    }

    public void setDropRate(float rate) {
        this.customDropRate = rate;
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

        if (attackCooldown > 0) {
            attackCooldown--;
        }

        if (dead) {
            deathTimer++;
            corpseDecayTimer--;
            if (corpseDecayTimer <= 0) {
                destroy();
            }
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

        // Chão
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

    public abstract void updateAI(Player player, List<Particle> particles);

    public void takeDamage(int dmg, int knockbackDir, List<Particle> particles, List<Pickup> pickups, Player player) {
        if (dead) return;

        hp -= dmg;
        hurt = true;
        hurtTimer = 10;
        SoundSystem.playZombieHit();

        // Recuo físico (knockback)
        vx = knockbackDir * 2.5f;

        // Respingos de sangue escuro e vísceras
        for (int i = 0; i < 8; i++) {
            float pvx = (float) (Math.random() * 5.0 - 2.5) + knockbackDir * 1.5f;
            float pvy = (float) (-Math.random() * 4.0 - 1.0);
            particles.add(new Particle(x + width / 2, y + height / 2, pvx, pvy, Particle.ParticleType.BLOOD, GameConfig.COLOR_BLOOD_DARK, 35, 5));
        }

        if (hp <= 0) {
            hp = 0;
            die(particles, pickups, player);
        }
    }

    protected void die(List<Particle> particles, List<Pickup> pickups, Player player) {
        dead = true;
        deathTimer = 0;
        vx = 0;
        SoundSystem.playZombieDeath();
        player.addScore(scoreValue);

        // Grande jorro de sangue escuro ao morrer
        for (int i = 0; i < 16; i++) {
            float pvx = (float) (Math.random() * 6.0 - 3.0);
            float pvy = (float) (-Math.random() * 5.0 - 1.5);
            particles.add(new Particle(x + width / 2, y + height / 2, pvx, pvy, Particle.ParticleType.BLOOD, GameConfig.COLOR_BLOOD_DARK, 45, 6));
        }

        // Chance de drop de itens (munição, vida, granadas ou peças de metal)
        if (Math.random() < customDropRate) {
            double roll = Math.random();
            if (roll < 0.30) {
                pickups.add(new Pickup(x, GameConfig.GROUND_Y - 35, Pickup.PickupType.AMMO_BOX));
            } else if (roll < 0.50) {
                pickups.add(new Pickup(x, GameConfig.GROUND_Y - 35, Pickup.PickupType.MEDKIT));
            } else if (roll < 0.70) {
                pickups.add(new Pickup(x, GameConfig.GROUND_Y - 35, Pickup.PickupType.GRENADE_PACK));
            } else {
                pickups.add(new Pickup(x, GameConfig.GROUND_Y - 35, Pickup.PickupType.SCRAP_METAL));
            }
        }
    }

    public boolean isDead() { return dead; }
    public ZombieType getZombieType() { return zombieType; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
}
