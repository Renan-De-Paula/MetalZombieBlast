package br.com.ultimoabrigo.ui;

import br.com.ultimoabrigo.core.GameConfig;
import br.com.ultimoabrigo.core.GameState;
import br.com.ultimoabrigo.core.GameStateHandler;
import br.com.ultimoabrigo.core.InputHandler;
import br.com.ultimoabrigo.core.StateManager;
import br.com.ultimoabrigo.entities.Boss;
import br.com.ultimoabrigo.entities.MrXBoss;
import br.com.ultimoabrigo.entities.NemesisBoss;
import br.com.ultimoabrigo.entities.TyrantBoss;
import br.com.ultimoabrigo.entities.Particle;
import br.com.ultimoabrigo.entities.Pickup;
import br.com.ultimoabrigo.entities.Player;
import br.com.ultimoabrigo.entities.Projectile;
import br.com.ultimoabrigo.entities.Zombie;
import br.com.ultimoabrigo.world.Level;
import br.com.ultimoabrigo.world.Obstacle;
import br.com.ultimoabrigo.world.Spawner;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * PlayingState - Estado de jogo ativo (Fase de 1000 metros).
 * Orquestra Elias Rocha, hordas de zumbis, obstáculos recorrentes,
 * combate contra o chefe da fase, projéteis, pickups, partículas e HUD.
 */
public class PlayingState implements GameStateHandler {

    private final StateManager stateManager;
    private Player player;
    private Level level;
    private Spawner spawner;
    private Boss currentBoss = null;

    private final List<Zombie> zombies = new ArrayList<>();
    private final List<Obstacle> obstacles = new ArrayList<>();
    private final List<Projectile> projectiles = new ArrayList<>();
    private final List<Particle> particles = new ArrayList<>();
    private final List<Pickup> pickups = new ArrayList<>();

    private int currentStage = 1;
    private int distanceMeters = 0;
    private int zombiesKilled = 0;
    private br.com.ultimoabrigo.entities.Pickup pendingWeaponPickup = null;
    private boolean bossSpawned = false;

    public PlayingState(StateManager stateManager) {
        this.stateManager = stateManager;
        initStage(1, false);
    }

    public void initStage(int stageNumber, boolean keepPlayer) {
        this.currentStage = stageNumber;
        this.level = new Level(stageNumber);
        this.spawner = new Spawner(stageNumber);
        
        if (!keepPlayer || this.player == null) {
            this.player = new Player(120, GameConfig.GROUND_Y - GameConfig.PLAYER_HEIGHT);
        } else {
            this.player.respawnAtCheckpoint();
        }
        
        this.currentBoss = null;
        this.bossSpawned = false;
        this.zombies.clear();
        this.obstacles.clear();
        this.projectiles.clear();
        this.particles.clear();
        this.pickups.clear();
        this.zombiesKilled = 0;

        // Distribui suprimentos iniciais
        pickups.add(new Pickup(280, GameConfig.GROUND_Y - 40, Pickup.PickupType.AMMO_BOX));
        pickups.add(new Pickup(420, GameConfig.GROUND_Y - 40, Pickup.PickupType.MEDKIT));
        pickups.add(new Pickup(560, GameConfig.GROUND_Y - 40, Pickup.PickupType.GRENADE_PACK));

        // Spawna a primeira tela
        spawner.update(player.getX(), zombies, obstacles, pickups);
    }

    @Override
    public void enter() {
        if (player == null) {
            initStage(currentStage, false);
        } else if (player.isDead()) {
            initStage(currentStage, true);
        }
    }

    @Override
    public void update(InputHandler input) {
        if (input.isPauseJustPressed()) {
            stateManager.setState(GameState.PAUSED);
            return;
        }

        if (player.isDead()) {
            player.update();
            if (player.getDeathTimer() > 80) {
                stateManager.setState(GameState.GAME_OVER);
            }
            return;
        }

        // 1. Controles do herói Elias Rocha
        player.handleInput(input, projectiles, particles);
        player.update();

        // 2. Atualiza o gerador procedural tela a tela (Spawner)
        spawner.update(player.getX(), zombies, obstacles, pickups);

        // 3. Atualiza o mundo (câmera, parallax, solo, checkpoints, travamento de 1000m)
        level.update(player);

        // 4. Atualiza distância percorrida
        distanceMeters = (int) (player.getX() / GameConfig.PIXELS_PER_METER);

        // 5. Acionamento e gerenciamento do Boss aos 1000 metros (32.000 px)
        if (distanceMeters >= GameConfig.METERS_PER_PHASE && !bossSpawned) {
            spawnBossForStage();
        }

        // Restringe jogador e boss na arena fechada para nao travarem no cenario
        if (level.getCamera().isLockedInArena()) {
            float camX = level.getCamera().getX();
            if (player.getX() < camX) {
                player.setX(camX);
            }
            if (player.getX() + player.getWidth() > camX + GameConfig.LOGICAL_WIDTH - 30) {
                player.setX(camX + GameConfig.LOGICAL_WIDTH - 30 - player.getWidth());
            }

            if (currentBoss != null && !currentBoss.isDefeated()) {
                if (currentBoss.getX() < camX) {
                    currentBoss.setX(camX);
                }
                if (currentBoss.getX() + currentBoss.getWidth() > camX + GameConfig.LOGICAL_WIDTH - 30) {
                    currentBoss.setX(camX + GameConfig.LOGICAL_WIDTH - 30 - currentBoss.getWidth());
                }
            }
        }

        if (currentBoss != null) {
            currentBoss.update();
            currentBoss.updateBossAI(player, projectiles, particles, level.getCamera(), obstacles, zombies);

            // Vitória da fase: quando a sequência de derrota do boss é concluída
            if (currentBoss.isDefeatSequenceComplete()) {
                PhaseClearState clearState = (PhaseClearState) stateManager.getStateHandler(GameState.PHASE_CLEAR);
                if (clearState != null) {
                    clearState.setPhaseStats(currentStage, zombiesKilled, player.getScore());
                }
                stateManager.setState(GameState.PHASE_CLEAR);
                return;
            }
        }

        // 6. Atualiza Obstáculos e colisões
        Iterator<Obstacle> obsIt = obstacles.iterator();
        while (obsIt.hasNext()) {
            Obstacle obs = obsIt.next();
            obs.update();
            obs.handlePlayerInteraction(player, zombies, particles, pickups, level.getCamera());
            if (!obs.isActive()) {
                obsIt.remove();
            }
        }

        // 7. Atualiza Zumbis
        Iterator<Zombie> zIt = zombies.iterator();
        while (zIt.hasNext()) {
            Zombie z = zIt.next();
            z.update();
            z.updateAI(player, particles);
            if (!z.isActive()) {
                zIt.remove();
            }
        }

        // 8. Atualiza Projéteis e colisões contra Zumbis, Obstáculos e Boss
        Iterator<Projectile> projIt = projectiles.iterator();
        while (projIt.hasNext()) {
            Projectile p = projIt.next();
            p.update();

            // Explosão de granada em área
            if (p.isExploded()) {
                p.spawnExplosionParticles(particles);
                level.getCamera().shake(6.0f, 18);

                // Dano em Zumbis
                for (Zombie z : zombies) {
                    if (!z.isDead()) {
                        float dist = (float) Math.hypot(z.getX() - p.getX(), z.getY() - p.getY());
                        if (dist <= GameConfig.GRENADE_EXPLOSION_RADIUS) {
                            int knock = (z.getX() > p.getX()) ? 1 : -1;
                            z.takeDamage(p.getDamage(), knock, particles, pickups, player);
                            if (z.isDead()) zombiesKilled++;
                        }
                    }
                }

                // Dano no Boss
                if (currentBoss != null && !currentBoss.isDefeated()) {
                    float dist = (float) Math.hypot(currentBoss.getX() + currentBoss.getWidth() / 2 - p.getX(), currentBoss.getY() + currentBoss.getHeight() / 2 - p.getY());
                    if (dist <= GameConfig.GRENADE_EXPLOSION_RADIUS) {
                        currentBoss.takeDamage(p.getDamage(), particles, level.getCamera(), player);
                    }
                }

                // Dano em obstáculos e detonação de barris
                for (Obstacle obs : obstacles) {
                    if (obs.isActive()) {
                        float dist = (float) Math.hypot(obs.getX() - p.getX(), obs.getY() - p.getY());
                        if (dist <= GameConfig.GRENADE_EXPLOSION_RADIUS) {
                            obs.takeDamage(p.getDamage(), particles, zombies, pickups, player, level.getCamera());
                        }
                    }
                }
            }

            // Colisão de tiros do jogador com o Boss
            if (p.isActive() && p.isFromPlayer() && !p.isExploded() && p.getType() != Projectile.ProjectileType.GRENADE && currentBoss != null && !currentBoss.isDefeated()) {
                if (p.getBounds().intersects(currentBoss.getBounds())) {
                    currentBoss.takeDamage(p.getDamage(), particles, level.getCamera(), player);
                    p.destroy();
                }
            }

            // Colisão de tiros com Zumbis comuns
            if (p.isActive() && p.isFromPlayer() && !p.isExploded() && p.getType() != Projectile.ProjectileType.GRENADE) {
                for (Zombie z : zombies) {
                    if (!z.isDead() && p.getBounds().intersects(z.getBounds())) {
                        int knock = (p.getVx() >= 0) ? 1 : -1;
                        z.takeDamage(p.getDamage(), knock, particles, pickups, player);
                        if (z.isDead()) zombiesKilled++;
                        p.destroy();
                        break;
                    }
                }
            }

            // Colisão de tiros com Obstáculos
            if (p.isActive() && p.isFromPlayer() && !p.isExploded() && p.getType() != Projectile.ProjectileType.GRENADE) {
                for (Obstacle obs : obstacles) {
                    if (obs.isActive() && p.getBounds().intersects(obs.getBounds())) {
                        obs.takeDamage(p.getDamage(), particles, zombies, pickups, player, level.getCamera());
                        p.destroy();
                        break;
                    }
                }
            }

            // Colisão de projéteis hostis (disparados por bosses) com o jogador
            if (p.isActive() && !p.isFromPlayer() && p.getBounds().intersects(player.getBounds())) {
                player.takeDamage(p.getDamage());
                p.destroy();
            }

            if (!p.isActive()) {
                projIt.remove();
            }
        }

        // 9. Golpe Melee de Chave Inglesa (K)
        if (player.isMeleeActive()) {
            if (currentBoss != null && !currentBoss.isDefeated()) {
                if (player.getMeleeHitbox().intersects(currentBoss.getBounds())) {
                    currentBoss.takeDamage(GameConfig.MELEE_DAMAGE, particles, level.getCamera(), player);
                    level.getCamera().shake(5.0f, 12);
                }
            }
            for (Zombie z : zombies) {
                if (!z.isDead() && player.getMeleeHitbox().intersects(z.getBounds())) {
                    z.takeDamage(GameConfig.MELEE_DAMAGE, player.getFacing(), particles, pickups, player);
                    level.getCamera().shake(4.0f, 10);
                    if (z.isDead()) zombiesKilled++;
                }
            }
            for (Obstacle obs : obstacles) {
                if (obs.isActive() && player.getMeleeHitbox().intersects(obs.getBounds())) {
                    obs.takeDamage(GameConfig.MELEE_DAMAGE, particles, zombies, pickups, player, level.getCamera());
                    level.getCamera().shake(3.0f, 8);
                }
            }
        }

        // 10. Coleta de Suprimentos
        if (pendingWeaponPickup == null) {
            Iterator<Pickup> pickIt = pickups.iterator();
            while (pickIt.hasNext()) {
                Pickup p = pickIt.next();
                p.update();
                if (p.getBounds().intersects(player.getBounds())) {
                    if (p.getType() == Pickup.PickupType.SHOTGUN_WEAPON || p.getType() == Pickup.PickupType.RIFLE_WEAPON || p.getType() == Pickup.PickupType.FLAMETHROWER_WEAPON) {
                        br.com.ultimoabrigo.entities.WeaponType currentSec = player.getWeaponInventory()[1];
                        br.com.ultimoabrigo.entities.WeaponType newWep = null;
                        if (p.getType() == Pickup.PickupType.SHOTGUN_WEAPON) newWep = br.com.ultimoabrigo.entities.WeaponType.SHOTGUN;
                        if (p.getType() == Pickup.PickupType.RIFLE_WEAPON) newWep = br.com.ultimoabrigo.entities.WeaponType.RIFLE;
                        if (p.getType() == Pickup.PickupType.FLAMETHROWER_WEAPON) newWep = br.com.ultimoabrigo.entities.WeaponType.FLAMETHROWER;

                        if (currentSec == null || currentSec != newWep) {
                            pendingWeaponPickup = p; // Pausa para o prompt
                        } else {
                            player.refillAmmo(newWep);
                            p.destroy();
                            br.com.ultimoabrigo.assets.SoundSystem.playPickup();
                        }
                    } else {
                        p.applyToPlayer(player);
                    }
                }
                if (!p.isActive()) {
                    pickIt.remove();
                }
            }
        } else {
            // Lógica do prompt
            if (input.isShootJustPressed()) { // Aceita (J)
                br.com.ultimoabrigo.entities.WeaponType newWep = null;
                if (pendingWeaponPickup.getType() == Pickup.PickupType.SHOTGUN_WEAPON) newWep = br.com.ultimoabrigo.entities.WeaponType.SHOTGUN;
                if (pendingWeaponPickup.getType() == Pickup.PickupType.RIFLE_WEAPON) newWep = br.com.ultimoabrigo.entities.WeaponType.RIFLE;
                if (pendingWeaponPickup.getType() == Pickup.PickupType.FLAMETHROWER_WEAPON) newWep = br.com.ultimoabrigo.entities.WeaponType.FLAMETHROWER;
                
                player.equipSecondary(newWep);
                pendingWeaponPickup.destroy();
                br.com.ultimoabrigo.assets.SoundSystem.playPickup();
                pendingWeaponPickup = null;
            } else if (input.isMeleeJustPressed()) { // Recusa (K)
                pendingWeaponPickup.destroy();
                pendingWeaponPickup = null;
            }
            return; // Bloqueia o jogo enquanto a UI de troca está ativa
        }

        // 11. Atualiza Partículas
        Iterator<Particle> partIt = particles.iterator();
        while (partIt.hasNext()) {
            Particle pt = partIt.next();
            pt.update();
            if (!pt.isActive()) {
                partIt.remove();
            }
        }
    }

    private void spawnBossForStage() {
        bossSpawned = true;
        float arenaCamX = GameConfig.STAGE_WIDTH_PIXELS - GameConfig.LOGICAL_WIDTH;
        level.getCamera().lockAt(arenaCamX);

        if (currentStage == 1) {
            // Boss 1: MR. X no pátio do posto abandonado
            currentBoss = new MrXBoss(arenaCamX + 680, GameConfig.GROUND_Y - 102);
            // Barris explosivos na arena para uso tático por Elias
            obstacles.add(new Obstacle(arenaCamX + 380, GameConfig.GROUND_Y - 48, Obstacle.ObstacleType.EXPLOSIVE_BARREL, 1));
            obstacles.add(new Obstacle(arenaCamX + 540, GameConfig.GROUND_Y - 48, Obstacle.ObstacleType.EXPLOSIVE_BARREL, 1));
            obstacles.add(new Obstacle(arenaCamX + 750, GameConfig.GROUND_Y - 48, Obstacle.ObstacleType.EXPLOSIVE_BARREL, 1));
            obstacles.add(new Obstacle(arenaCamX + 460, GameConfig.GROUND_Y - 42, Obstacle.ObstacleType.CRATE_DEBRIS, 1));
        } else if (currentStage == 2) {
            // Boss 2: TYRANT no pátio da Clínica Rural e Delegacia
            currentBoss = new TyrantBoss(arenaCamX + 680, GameConfig.GROUND_Y - 108);
            obstacles.add(new Obstacle(arenaCamX + 320, GameConfig.GROUND_Y - 48, Obstacle.ObstacleType.ABANDONED_CAR, 2));
            obstacles.add(new Obstacle(arenaCamX + 580, GameConfig.GROUND_Y - 48, Obstacle.ObstacleType.EXPLOSIVE_BARREL, 2));
            obstacles.add(new Obstacle(arenaCamX + 760, GameConfig.GROUND_Y - 36, Obstacle.ObstacleType.BARBED_WIRE, 2));
        } else if (currentStage == 3) {
            // Boss 3: NEMESIS no Bunker
            currentBoss = new NemesisBoss(arenaCamX + 680, GameConfig.GROUND_Y - 95);
            // Caixotes de contenção biológica (CRATE) no bunker
            obstacles.add(new Obstacle(arenaCamX + 300, GameConfig.GROUND_Y - 42, Obstacle.ObstacleType.CRATE_DEBRIS, 3));
            obstacles.add(new Obstacle(arenaCamX + 450, GameConfig.GROUND_Y - 48, Obstacle.ObstacleType.EXPLOSIVE_BARREL, 3));
            obstacles.add(new Obstacle(arenaCamX + 700, GameConfig.GROUND_Y - 42, Obstacle.ObstacleType.CRATE_DEBRIS, 3));
        }
    }

    @Override
    public void render(Graphics2D g) {
        float camX = level.getCamera().getX();
        float camY = level.getCamera().getY();

        // 1. Parallax e Solo do mundo
        level.renderBackground(g);

        // 2. Obstáculos
        for (Obstacle obs : obstacles) {
            obs.render(g, camX, camY);
        }

        // 3. Suprimentos
        for (Pickup p : pickups) {
            p.render(g, camX, camY);
        }

        // 4. Zumbis
        for (Zombie z : zombies) {
            z.render(g, camX, camY);
        }

        // 5. Boss da Fase (se ativo)
        if (currentBoss != null) {
            currentBoss.render(g, camX, camY);
        }

        // 6. Jogador Elias Rocha
        player.render(g, camX, camY);

        // 7. Projéteis
        for (Projectile p : projectiles) {
            p.render(g, camX, camY);
        }

        // 8. Partículas
        for (Particle pt : particles) {
            pt.render(g, camX, camY);
        }

        // 9. Overlays de frente do Level
        level.renderForegroundOverlay(g);

        // 10. HUD Metal Slug
        HUD.render(g, player, distanceMeters, currentStage, spawner.getDifficultyManager());

        if (pendingWeaponPickup != null) {
            String wepName = pendingWeaponPickup.getType() == Pickup.PickupType.SHOTGUN_WEAPON ? "ESCOPETA" :
                             pendingWeaponPickup.getType() == Pickup.PickupType.RIFLE_WEAPON ? "FUZIL" : "LANÇA-CHAMAS";
            int pw = 300;
            int ph = 100;
            int px = (GameConfig.LOGICAL_WIDTH - pw) / 2;
            int py = (GameConfig.LOGICAL_HEIGHT - ph) / 2 - 50;

            g.setColor(new Color(15, 20, 28, 230));
            g.fillRoundRect(px, py, pw, ph, 8, 8);
            g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
            g.drawRoundRect(px, py, pw, ph, 8, 8);

            g.setFont(new Font("Impact", Font.PLAIN, 20));
            g.setColor(Color.WHITE);
            String title = "NOVA ARMA: " + wepName;
            g.drawString(title, px + (pw - g.getFontMetrics().stringWidth(title)) / 2, py + 30);

            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.setColor(new Color(200, 200, 200));
            String sub = "Deseja equipar esta arma?";
            g.drawString(sub, px + (pw - g.getFontMetrics().stringWidth(sub)) / 2, py + 55);

            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.setColor(Color.GREEN);
            g.drawString("[J] ACEITAR", px + 40, py + 85);
            g.setColor(Color.RED);
            g.drawString("[K] RECUSAR", px + 170, py + 85);
        }
    }

    @Override
    public void exit() {
    }

    public Level getLevel() { return level; }
    public Player getPlayer() { return player; }
    public Boss getCurrentBoss() { return currentBoss; }
    public int getZombiesKilled() { return zombiesKilled; }
}
