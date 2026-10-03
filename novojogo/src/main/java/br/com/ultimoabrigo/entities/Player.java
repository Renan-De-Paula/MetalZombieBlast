package br.com.ultimoabrigo.entities;

import br.com.ultimoabrigo.assets.ProceduralSprites;
import br.com.ultimoabrigo.assets.SoundSystem;
import br.com.ultimoabrigo.core.GameConfig;
import br.com.ultimoabrigo.core.InputHandler;
import br.com.ultimoabrigo.util.AABB;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Player - Representa o herói Elias Rocha.
 * Implementa física completa, pulo variável, agachamento, tiro em 3 direções,
 * 4 armas selecionáveis, golpe com chave inglesa (K), arremesso de granadas (L),
 * vidas, 5 corações e invencibilidade temporária.
 */
public class Player extends Entity {

    // Estados de ação do jogador
    private boolean crouched = false;
    private boolean onGround = false;
    private boolean aimingUp = false;
    private boolean isShooting = false;
    private boolean isMelee = false;
    private int meleeProgress = 0;
    private final AABB meleeHitbox;

    // Atributos de sobrevivência
    private int hearts = GameConfig.PLAYER_MAX_HEARTS;
    private int lives = GameConfig.PLAYER_INITIAL_LIVES;
    private int invincibilityTimer = 0;
    private int score = 0;
    private boolean dead = false;
    private int deathTimer = 0;

    // Sistema de Armas
    private final WeaponType[] weaponInventory = {
            WeaponType.PISTOL,
            null
    };
    private final int[] ammo = {
            -1, // Pistola infinita
            0
    };
    private int currentWeaponIndex = 0;
    private int shootCooldown = 0;
    private int grenades = GameConfig.GRENADE_INITIAL_AMMO;
    private int grenadeCooldown = 0;

    // Animação
    private int animTick = 0;

    // Checkpoint atual para respawn
    private float checkpointX = 120;
    private float checkpointY = GameConfig.GROUND_Y - GameConfig.PLAYER_HEIGHT;

    public Player(float startX, float startY) {
        super(startX, startY, GameConfig.PLAYER_WIDTH, GameConfig.PLAYER_HEIGHT);
        this.meleeHitbox = new AABB(0, 0, GameConfig.MELEE_RANGE, GameConfig.PLAYER_HEIGHT);
        this.checkpointX = startX;
        this.checkpointY = startY;
    }

    public void handleInput(InputHandler input, List<Projectile> projectiles, List<Particle> particles) {
        if (dead) return;

        // Troca direta de armas pelas teclas 1, 2
        if (input.isWeaponSlot1()) currentWeaponIndex = 0;
        if (input.isWeaponSlot2() && weaponInventory[1] != null && ammo[1] > 0) currentWeaponIndex = 1;

        // Agachamento (S ou Seta Baixo)
        crouched = input.isCrouch() && onGround;
        if (crouched) {
            height = GameConfig.PLAYER_CROUCH_HEIGHT;
        } else {
            height = GameConfig.PLAYER_HEIGHT;
        }

        // Mira para cima (W + J ou Cima + J)
        aimingUp = (input.isUp() || input.isKeyDown(java.awt.event.KeyEvent.VK_UP)) && !crouched;

        // Movimento horizontal (A/D ou Setas)
        float targetVx = 0;
        if (!isMelee) {
            if (input.isLeft()) {
                targetVx = -GameConfig.PLAYER_WALK_SPEED;
                facing = -1;
            } else if (input.isRight()) {
                targetVx = GameConfig.PLAYER_WALK_SPEED;
                facing = 1;
            }
        }
        // Se agachado, move-se mais devagar
        if (crouched) {
            targetVx *= 0.4f;
        }
        vx = targetVx;

        // Pulo (W ou Espaço)
        if (input.isJumpJustPressed() && onGround && !crouched && !isMelee) {
            vy = GameConfig.PLAYER_JUMP_FORCE;
            onGround = false;
            SoundSystem.playJump();
        }

        // Ataque Melee com Chave Inglesa (K)
        if (input.isMeleeJustPressed() && !isMelee) {
            triggerMelee();
        }

        // Arremesso de Granada / Molotov (L)
        if (input.isGrenadeJustPressed() && grenades > 0 && grenadeCooldown <= 0) {
            throwGrenade(projectiles);
        }

        // Disparo com a arma equipada (J)
        isShooting = false;
        WeaponType curWeapon = weaponInventory[currentWeaponIndex];
        boolean fireTriggered = (curWeapon == WeaponType.PISTOL) ? input.isShootJustPressed() : input.isShoot();

        if (fireTriggered && shootCooldown <= 0 && !isMelee) {
            fireWeapon(curWeapon, projectiles, particles);
        }
    }

    private void fireWeapon(WeaponType weapon, List<Projectile> projectiles, List<Particle> particles) {
        // Verifica munição (exceto pistola)
        if (ammo[currentWeaponIndex] == 0) {
            currentWeaponIndex = 0; // Volta para pistola se acabar munição
            weapon = WeaponType.PISTOL;
        }

        if (ammo[currentWeaponIndex] > 0) {
            ammo[currentWeaponIndex]--;
        }

        isShooting = true;
        shootCooldown = weapon.getFireRateFrames();

        // Posição de saída do cano
        float spawnX = (facing == 1) ? x + width + 4 : x - 12;
        float spawnY = crouched ? y + 26 : y + 26;

        if (aimingUp) {
            spawnX = x + width / 2 - 2;
            spawnY = y - 10;
        }

        // Cria os projéteis conforme o tipo
        switch (weapon) {
            case PISTOL:
                float pvx = aimingUp ? 0 : facing * 14.0f;
                float pvy = aimingUp ? -14.0f : 0;
                projectiles.add(new Projectile(spawnX, spawnY, pvx, pvy, Projectile.ProjectileType.PISTOL_BULLET, weapon.getDamage(), true, 90));
                SoundSystem.playPistolShot();
                spawnCasing(particles, spawnX, spawnY);
                break;

            case SHOTGUN:
                for (int i = 0; i < GameConfig.SHOTGUN_PELLETS; i++) {
                    float spread = (float) ((i - 2) * 1.6);
                    float svx = aimingUp ? spread : facing * (12.0f + (float) Math.random() * 2.0f);
                    float svy = aimingUp ? -13.0f : spread;
                    projectiles.add(new Projectile(spawnX, spawnY, svx, svy, Projectile.ProjectileType.SHOTGUN_PELLET, weapon.getDamage(), true, 35));
                }
                SoundSystem.playShotgunShot();
                spawnCasing(particles, spawnX, spawnY);
                break;

            case RIFLE:
                float rvx = aimingUp ? (float) (Math.random() * 0.6 - 0.3) : facing * 18.0f;
                float rvy = aimingUp ? -18.0f : (float) (Math.random() * 0.6 - 0.3);
                projectiles.add(new Projectile(spawnX, spawnY, rvx, rvy, Projectile.ProjectileType.RIFLE_BULLET, weapon.getDamage(), true, 80));
                SoundSystem.playRifleShot();
                spawnCasing(particles, spawnX, spawnY);
                break;

            case FLAMETHROWER:
                for (int i = 0; i < 3; i++) {
                    float fvx = aimingUp ? (float) (Math.random() * 2.0 - 1.0) : facing * (6.0f + (float) Math.random() * 3.0f);
                    float fvy = aimingUp ? -8.0f : (float) (Math.random() * 2.0 - 1.0);
                    projectiles.add(new Projectile(spawnX, spawnY, fvx, fvy, Projectile.ProjectileType.FLAME, weapon.getDamage(), true, 22));
                }
                SoundSystem.playFlameHiss();
                break;
        }
    }

    private void spawnCasing(List<Particle> particles, float cx, float cy) {
        float cvx = -facing * (float) (Math.random() * 2.5 + 1.0);
        float cvy = -(float) (Math.random() * 3.0 + 2.0);
        particles.add(new Particle(cx, cy, cvx, cvy, Particle.ParticleType.CASING, new Color(220, 180, 50), 45, 3));
    }

    private void triggerMelee() {
        isMelee = true;
        meleeProgress = 0;
        SoundSystem.playWrenchHit();
    }

    private void throwGrenade(List<Projectile> projectiles) {
        grenades--;
        grenadeCooldown = 30;
        float gx = (facing == 1) ? x + width : x - 10;
        float gy = y + 15;
        float gvx = facing * 7.5f;
        float gvy = -9.0f;
        projectiles.add(new Projectile(gx, gy, gvx, gvy, Projectile.ProjectileType.GRENADE, GameConfig.GRENADE_DAMAGE, true, 120));
    }

    @Override
    public void update() {
        animTick++;

        // Cooldowns
        if (shootCooldown > 0) shootCooldown--;
        if (grenadeCooldown > 0) grenadeCooldown--;
        if (invincibilityTimer > 0) invincibilityTimer--;

        // Progresso do golpe melee com chave inglesa
        if (isMelee) {
            meleeProgress++;
            // Atualiza hitbox na frente do jogador
            float hx = (facing == 1) ? x + width : x - GameConfig.MELEE_RANGE;
            meleeHitbox.set(hx, y + 10, GameConfig.MELEE_RANGE, height - 10);

            if (meleeProgress >= GameConfig.MELEE_COOLDOWN_FRAMES) {
                isMelee = false;
                meleeProgress = 0;
            }
        }

        // Aplica gravidade e velocidade vertical
        if (!onGround) {
            vy += GameConfig.GRAVITY;
            if (vy > GameConfig.MAX_FALL_SPEED) {
                vy = GameConfig.MAX_FALL_SPEED;
            }
        }

        // Atualiza posições
        x += vx;
        y += vy;

        // Colisão com o solo (GROUND_Y)
        float floorY = GameConfig.GROUND_Y - height;
        if (y >= floorY) {
            y = floorY;
            vy = 0;
            onGround = true;
        } else {
            onGround = false;
        }

        // Impede que saia para trás do início da fase (0 px)
        if (x < 0) {
            x = 0;
        }

        updateBounds();

        // Tratamento de morte
        if (dead) {
            deathTimer++;
        }
    }

    public void takeDamage(int damage) {
        if (invincibilityTimer > 0 || dead) return;

        hearts -= damage;
        invincibilityTimer = GameConfig.PLAYER_INVINCIBILITY_FRAMES; // 1.5s piscando
        SoundSystem.playPlayerHurt();

        // Recuo de dano
        vx = -facing * 3.0f;
        vy = -3.5f;
        onGround = false;

        if (hearts <= 0) {
            hearts = 0;
            die();
        }
    }

    private void die() {
        dead = true;
        lives--;
        deathTimer = 0;
    }

    public void respawnAtCheckpoint() {
        x = checkpointX;
        y = checkpointY;
        vx = 0;
        vy = 0;
        hearts = GameConfig.PLAYER_MAX_HEARTS;
        dead = false;
        deathTimer = 0;
        invincibilityTimer = GameConfig.PLAYER_INVINCIBILITY_FRAMES;
        updateBounds();
    }

    public void setCheckpoint(float cx, float cy) {
        this.checkpointX = cx;
        this.checkpointY = cy;
    }

    public void heal(int amount) {
        hearts = Math.min(GameConfig.PLAYER_MAX_HEARTS, hearts + amount);
    }

        public void refillAmmo() {
        if (weaponInventory[1] != null) {
            refillAmmo(weaponInventory[1]);
        }
    }

    public void addGrenades(int count) {
        grenades = Math.min(GameConfig.GRENADE_MAX_AMMO, grenades + count);
    }

    public void addScore(int points) {
        this.score += points;
    }

    @Override
    public void render(Graphics2D g, float cameraX, float cameraY) {
        // Efeito de piscar durante invencibilidade
        if (invincibilityTimer > 0 && (animTick / 4) % 2 == 0) {
            return; // Oculta o frame para o efeito de piscar
        }

        int drawX = (int) (x - cameraX);
        int drawY = (int) (y - cameraY);

        ProceduralSprites.renderElias(
                g,
                drawX, drawY,
                facing,
                crouched,
                !onGround,
                Math.abs(vx) > 0.1f,
                animTick,
                aimingUp,
                isShooting,
                isMelee,
                meleeProgress,
                invincibilityTimer > 0,
                currentWeaponIndex
        );
    }

    // Getters
    public int getHearts() { return hearts; }
    public int getLives() { return lives; }
    public int getScore() { return score; }
    public int getGrenades() { return grenades; }
        public void equipSecondary(WeaponType newWeapon) {
        weaponInventory[1] = newWeapon;
        ammo[1] = newWeapon == WeaponType.SHOTGUN ? GameConfig.SHOTGUN_INITIAL_AMMO :
                  newWeapon == WeaponType.RIFLE ? GameConfig.RIFLE_INITIAL_AMMO :
                  GameConfig.FLAMETHROWER_INITIAL_AMMO;
        currentWeaponIndex = 1;
    }

    public void refillAmmo(WeaponType w) {
        if (weaponInventory[1] == w) {
            ammo[1] += w == WeaponType.SHOTGUN ? GameConfig.SHOTGUN_INITIAL_AMMO :
                       w == WeaponType.RIFLE ? GameConfig.RIFLE_INITIAL_AMMO :
                       GameConfig.FLAMETHROWER_INITIAL_AMMO;
        }
    }

    public WeaponType[] getWeaponInventory() { return weaponInventory; }
    public WeaponType getCurrentWeapon() { return weaponInventory[currentWeaponIndex]; }
    public int getCurrentAmmo() { return ammo[currentWeaponIndex]; }
    public boolean isDead() { return dead; }
    public int getDeathTimer() { return deathTimer; }
    public float getCheckpointX() { return checkpointX; }
    public float getCheckpointY() { return checkpointY; }
    public boolean isMeleeActive() { return isMelee && meleeProgress >= 6 && meleeProgress <= 18; }
    public AABB getMeleeHitbox() { return meleeHitbox; }
    public boolean isCrouched() { return crouched; }
    public boolean isOnGround() { return onGround; }
}
