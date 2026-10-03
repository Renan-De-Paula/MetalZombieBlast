package br.com.ultimoabrigo.world;

import br.com.ultimoabrigo.assets.SoundSystem;
import br.com.ultimoabrigo.core.GameConfig;
import br.com.ultimoabrigo.entities.Entity;
import br.com.ultimoabrigo.entities.Particle;
import br.com.ultimoabrigo.entities.Pickup;
import br.com.ultimoabrigo.entities.Player;
import br.com.ultimoabrigo.entities.WalkerZombie;
import br.com.ultimoabrigo.entities.Zombie;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;

/**
 * Obstacle - Representa obstáculos recorrentes do cenário:
 * barricadas de madeira, carros abandonados, caixotes, cercas de arame farpado,
 * poças tóxicas, barris explosivos, troncos/vigas caindo telegrafados e fogueiras.
 */
public class Obstacle extends Entity {

    public enum ObstacleType {
        WOODEN_BARRICADE(40, 60, 8, true, true),
        ABANDONED_CAR(110, 48, 25, true, false),
        CRATE_DEBRIS(42, 42, 5, true, true),
        BARBED_WIRE(48, 36, 6, false, true),
        TOXIC_PUDDLE(64, 12, 9999, false, false),
        EXPLOSIVE_BARREL(34, 48, 2, true, true),
        FALLING_HAZARD(70, 26, 9999, true, false),
        BONFIRE(44, 30, 9999, false, false),
        LANDMINE(24, 12, 1, false, true),
        CHEST(50, 40, 3, true, true);

        final int defaultW;
        final int defaultH;
        final int defaultHp;
        final boolean solid;
        final boolean destructible;

        ObstacleType(int defaultW, int defaultH, int defaultHp, boolean solid, boolean destructible) {
            this.defaultW = defaultW;
            this.defaultH = defaultH;
            this.defaultHp = defaultHp;
            this.solid = solid;
            this.destructible = destructible;
        }
    }

    private final ObstacleType type;
    private final int stageNumber;
    private int hp;
    private final int maxHp;
    private boolean exploded = false;
    private boolean carAmbushed = false;

    // Controle do tronco/viga caindo
    private boolean fallingTelegraphed = false;
    private int telegraphTimer = 48; // 0.8s telegrafando poeira
    private boolean isFalling = false;
    private boolean fallenGrounded = false;

    // Dano contínuo
    private int damageTick = 0;

    public Obstacle(float x, float y, ObstacleType type, int stageNumber) {
        super(x, y, type.defaultW, type.defaultH);
        this.type = type;
        this.stageNumber = stageNumber;
        this.hp = type.defaultHp;
        this.maxHp = type.defaultHp;

        if (type == ObstacleType.FALLING_HAZARD) {
            this.fallingTelegraphed = true;
            this.telegraphTimer = 48;
            this.y = -60; // Começa acima da tela
            updateBounds();
        }
    }

    @Override
    public void update() {
        damageTick++;

        // Atualiza queda de tronco/viga
        if (type == ObstacleType.FALLING_HAZARD) {
            if (fallingTelegraphed) {
                telegraphTimer--;
                if (telegraphTimer <= 0) {
                    fallingTelegraphed = false;
                    isFalling = true;
                    vy = 11.5f;
                }
            } else if (isFalling) {
                y += vy;
                if (y + height >= GameConfig.GROUND_Y) {
                    y = GameConfig.GROUND_Y - height;
                    isFalling = false;
                    fallenGrounded = true;
                    SoundSystem.playExplosion();
                }
                updateBounds();
            }
        }
    }

    public void takeDamage(int dmg, List<Particle> particles, List<Zombie> zombies, List<Pickup> pickups, Player player, Camera camera) {
        if (!type.destructible || !active) return;

        hp -= dmg;

        // Partículas de estilhaço de madeira/metal
        Color partColor = (type == ObstacleType.WOODEN_BARRICADE || type == ObstacleType.CRATE_DEBRIS)
                ? new Color(130, 85, 45) : new Color(180, 180, 190);
        for (int i = 0; i < 4; i++) {
            particles.add(new Particle(x + width / 2, y + height / 2,
                    (float) (Math.random() * 4.0 - 2.0), (float) (-Math.random() * 3.0 - 1.0),
                    Particle.ParticleType.SPARK, partColor, 25, 4));
        }

        // Se for barril explosivo, detona imediatamente
        if (type == ObstacleType.EXPLOSIVE_BARREL && hp <= 0 && !exploded) {
            explode(particles, zombies, pickups, player, camera);
        } else if (hp <= 0) {
            destroy();
            SoundSystem.playWrenchHit();
            // Chance de drop ao destruir caixotes/barricadas
            if (Math.random() < 0.30) {
                pickups.add(new Pickup(x, GameConfig.GROUND_Y - 35, Pickup.PickupType.SCRAP_METAL));
            }
        }
    }

    public void explode(List<Particle> particles, List<Zombie> zombies, List<Pickup> pickups, Player player, Camera camera) {
        if (exploded) return;
        exploded = true;
        destroy();
        SoundSystem.playExplosion();
        camera.shake(9.0f, 25);

        // Explosão em área enorme (raio 130px)
        float blastRadius = 130;
        for (int i = 0; i < 35; i++) {
            float ang = (float) (Math.random() * Math.PI * 2);
            float spd = (float) (Math.random() * 7.0 + 2.0);
            Color c = (i % 2 == 0) ? GameConfig.COLOR_METAL_SLUG_YELLOW : GameConfig.COLOR_DANGER_RED;
            particles.add(new Particle(x + width / 2, y + height / 2,
                    (float) Math.cos(ang) * spd, (float) (Math.sin(ang) * spd - 2.5),
                    Particle.ParticleType.EXPLOSION, c, 40, 14));
        }
        for (int i = 0; i < 20; i++) {
            particles.add(new Particle(x + width / 2, y + height / 2,
                    (float) (Math.random() * 4.0 - 2.0), (float) (-Math.random() * 4.0 - 1.0),
                    Particle.ParticleType.SMOKE, new Color(60, 60, 60), 55, 18));
        }

        // Elimina ou causa dano em zumbis no raio
        for (Zombie z : zombies) {
            if (!z.isDead()) {
                float dist = (float) Math.hypot(z.getX() - (x + width / 2), z.getY() - (y + height / 2));
                if (dist <= blastRadius) {
                    int knock = (z.getX() > x) ? 1 : -1;
                    z.takeDamage(10, knock, particles, pickups, player);
                }
            }
        }

        // Dano no próprio jogador se estiver colado
        float playerDist = (float) Math.hypot(player.getX() - (x + width / 2), player.getY() - (y + height / 2));
        if (playerDist <= blastRadius * 0.75f) {
            player.takeDamage(1);
        }
    }

    /**
     * Verifica interações de contato físico e dano com o jogador.
     */
    public void handlePlayerInteraction(Player player, List<Zombie> zombies, List<Particle> particles, List<Pickup> pickups, Camera camera) {
        if (!active) return;

        // Se for mina terrestre, detona ao pisar
        if (type == ObstacleType.LANDMINE && bounds.intersects(player.getBounds())) {
            explode(particles, zombies, pickups, player, camera);
            return;
        }

        // Se for carro abandonado com emboscada de zumbi escondido
        if (type == ObstacleType.ABANDONED_CAR && !carAmbushed) {
            if (Math.abs(player.getX() - x) < 130) {
                carAmbushed = true;
                // Emboscada: 1 ou 2 zumbis saem de trás do carro!
                zombies.add(new WalkerZombie(x + 20, GameConfig.GROUND_Y - GameConfig.WALKER_HEIGHT));
                SoundSystem.playZombieGroan();
            }
        }

        // Se for arame farpado, causa dano imediato ao encostar
        if (type == ObstacleType.BARBED_WIRE && bounds.intersects(player.getBounds())) {
            player.takeDamage(1);
            camera.shake(3.0f, 10);
            return;
        }

        // Se for poça tóxica ou fogueira, causa dano a cada intervalo se pisar dentro
        if ((type == ObstacleType.TOXIC_PUDDLE || type == ObstacleType.BONFIRE) && bounds.intersects(player.getBounds())) {
            if (damageTick % 45 == 0) {
                player.takeDamage(1);
            }
            return;
        }

        // Se for tronco/viga caindo no jogador
        if (type == ObstacleType.FALLING_HAZARD && isFalling && bounds.intersects(player.getBounds())) {
            player.takeDamage(2);
            camera.shake(8.0f, 20);
            return;
        }

        // Colisão sólida (plataformas por onde Elias pode andar por cima)
        if (type.solid) {
            float playerFootY = player.getY() + player.getHeight();
            float playerPrevFootY = playerFootY - player.getVy();

            // Pousar em cima do obstáculo
            if (player.getX() + player.getWidth() > x + 6 && player.getX() < x + width - 6) {
                if (playerFootY >= y && playerPrevFootY <= y + 14 && player.getVy() >= 0) {
                    player.setPosition(player.getX(), y - player.getHeight());
                    player.setVelocity(player.getVx(), 0);
                    return;
                }
            }

            // Bloqueio horizontal lateral se estiver na mesma altura
            if (bounds.intersects(player.getBounds()) && playerFootY > y + 10) {
                if (player.getVx() > 0 && player.getX() < x) {
                    player.setPosition(x - player.getWidth(), player.getY());
                } else if (player.getVx() < 0 && player.getX() > x) {
                    player.setPosition(x + width, player.getY());
                }
            }
        }
    }

    @Override
    public void render(Graphics2D g, float cameraX, float cameraY) {
        int drawX = (int) (x - cameraX);
        int drawY = (int) (y - cameraY);

        if (drawX < -150 || drawX > GameConfig.LOGICAL_WIDTH + 150) return;

        // Se estiver telegrafando queda com poeira
        if (type == ObstacleType.FALLING_HAZARD && fallingTelegraphed) {
            renderDustTelegraph(g, drawX);
            return;
        }

        switch (type) {
            case WOODEN_BARRICADE:
                renderWoodenBarricade(g, drawX, drawY);
                break;
            case ABANDONED_CAR:
                renderAbandonedCar(g, drawX, drawY);
                break;
            case CRATE_DEBRIS:
                renderCrate(g, drawX, drawY);
                break;
            case BARBED_WIRE:
                renderBarbedWire(g, drawX, drawY);
                break;
            case TOXIC_PUDDLE:
                renderToxicPuddle(g, drawX, drawY);
                break;
            case EXPLOSIVE_BARREL:
                renderExplosiveBarrel(g, drawX, drawY);
                break;
            case FALLING_HAZARD:
                renderFallingHazard(g, drawX, drawY);
                break;
            case BONFIRE:
                renderBonfire(g, drawX, drawY);
                break;
                        case CHEST:
                renderChest(g, drawX, drawY);
                break;
            case LANDMINE:
                java.awt.image.BufferedImage mineImg = br.com.ultimoabrigo.assets.AssetLoader.loadImage("/assets/items/mine.png");
                // Afundar a mina no chão e deixá-la pequena
                g.drawImage(mineImg, drawX, drawY + 4, width, height, null);
                break;
        }

        // Barra de integridade em obstáculos destrutíveis danificados
        if (type.destructible && hp < maxHp && hp > 0) {
            int bw = width;
            int bh = 4;
            int by = drawY - 8;
            g.setColor(Color.BLACK);
            g.fillRect(drawX, by, bw, bh);
            g.setColor(new Color(220, 180, 50));
            g.fillRect(drawX, by, (int) (bw * ((float) hp / maxHp)), bh);
            g.setColor(Color.WHITE);
            g.drawRect(drawX, by, bw, bh);
        }
    }

    private void renderDustTelegraph(Graphics2D g, int drawX) {
        g.setColor(new Color(200, 190, 170, 140));
        for (int i = 0; i < 8; i++) {
            int dx = drawX + (i * 9);
            int dy = GameConfig.GROUND_Y - 50 + (int) ((Math.sin(damageTick * 0.4 + i) + 1) * 20);
            g.fillOval(dx, dy, 6, 6);
        }
        g.setFont(new Font("Impact", Font.PLAIN, 12));
        g.setColor(GameConfig.COLOR_DANGER_RED);
        g.drawString("⚠ PERIGO", drawX + 10, GameConfig.GROUND_Y - 70);
    }

    private void renderWoodenBarricade(Graphics2D g, int dx, int dy) {
        g.setColor(new Color(110, 75, 45));
        g.fillRect(dx + 4, dy, 10, height);
        g.fillRect(dx + width - 14, dy, 10, height);
        // Tábuas transversais pregadas
        g.setColor(new Color(138, 95, 55));
        for (int i = 0; i < 4; i++) {
            g.fillRect(dx, dy + 6 + (i * 13), width, 10);
            g.setColor(Color.BLACK);
            g.fillRect(dx + 6, dy + 10 + (i * 13), 2, 2);
            g.fillRect(dx + width - 8, dy + 10 + (i * 13), 2, 2);
            g.setColor(new Color(138, 95, 55));
        }
        // Marcas de sangue nas tábuas
        g.setColor(GameConfig.COLOR_BLOOD_DARK);
        g.fillRect(dx + 12, dy + 18, 14, 8);
    }

    private void renderAbandonedCar(Graphics2D g, int dx, int dy) {
        Color body = (stageNumber == 2) ? new Color(40, 50, 70) : new Color(115, 60, 45); // Viaduto/Polícia ou Rural enferrujado
        // Carroceria principal
        g.setColor(body);
        g.fillRect(dx + 6, dy + 18, width - 12, 22);
        // Cabine / Teto
        g.fillRect(dx + 25, dy + 2, 55, 18);
        // Vidros estilhaçados escuros
        g.setColor(new Color(25, 30, 35));
        g.fillRect(dx + 30, dy + 5, 20, 13);
        g.fillRect(dx + 55, dy + 5, 20, 13);
        // Pneus murchos
        g.setColor(new Color(20, 20, 20));
        g.fillOval(dx + 16, dy + 32, 18, 16);
        g.fillOval(dx + width - 34, dy + 32, 18, 16);
        // Ferrugem
        g.setColor(new Color(150, 70, 35));
        g.fillRect(dx + 8, dy + 22, 16, 8);
        g.fillRect(dx + 70, dy + 24, 20, 6);
    }

    private void renderCrate(Graphics2D g, int dx, int dy) {
        g.setColor(new Color(145, 105, 65));
        g.fillRect(dx, dy, width, height);
        g.setColor(new Color(90, 60, 35));
        g.drawRect(dx, dy, width, height);
        g.drawLine(dx, dy, dx + width, dy + height);
        g.drawLine(dx + width, dy, dx, dy + height);
    }

    private void renderBarbedWire(Graphics2D g, int dx, int dy) {
        // Estacas de madeira ou metal
        g.setColor(new Color(70, 55, 40));
        g.fillRect(dx + 4, dy, 6, height);
        g.fillRect(dx + width - 10, dy, 6, height);
        // Fios farpados em espiral
        g.setColor(new Color(170, 175, 185));
        for (int i = 0; i < 3; i++) {
            int wy = dy + 6 + i * 11;
            g.drawLine(dx, wy, dx + width, wy);
            for (int k = 8; k < width - 8; k += 10) {
                g.drawLine(dx + k, wy - 3, dx + k + 3, wy + 3);
            }
        }
    }

    private void renderToxicPuddle(Graphics2D g, int dx, int dy) {
        g.setColor(new Color(40, 190, 40, 210));
        g.fillOval(dx, dy, width, height);
        // Bolhas tóxicas
        int bubbleX = dx + (damageTick * 2) % (width - 10);
        g.setColor(new Color(180, 255, 120));
        g.fillOval(bubbleX, dy + 2, 5, 5);
    }

    private void renderExplosiveBarrel(Graphics2D g, int dx, int dy) {
        // Tambor vermelho de combustível
        g.setColor(new Color(195, 30, 30));
        g.fillRoundRect(dx + 2, dy, width - 4, height, 6, 6);
        // Faixas amarelas de advertência
        g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
        g.fillRect(dx + 2, dy + 14, width - 4, 6);
        g.fillRect(dx + 2, dy + 30, width - 4, 6);
        // Símbolo de fogo
        g.setFont(new Font("Impact", Font.PLAIN, 11));
        g.setColor(Color.WHITE);
        g.drawString("TNT", dx + 8, dy + 26);
    }

    private void renderFallingHazard(Graphics2D g, int dx, int dy) {
        if (stageNumber == 3) {
            // Viga de aço pesada industrial
            g.setColor(new Color(90, 95, 105));
            g.fillRect(dx, dy, width, height);
            g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
            g.fillRect(dx + 4, dy + 4, width - 8, 4);
        } else {
            // Tronco de pinheiro maciço
            g.setColor(new Color(75, 50, 30));
            g.fillRoundRect(dx, dy, width, height, 8, 8);
            g.setColor(new Color(50, 35, 20));
            g.fillOval(dx + width - 10, dy + 2, 8, height - 4);
        }
    }

        private void renderChest(Graphics2D g, int x, int y) {
        // Baú de suprimentos militar
        g.setColor(new Color(60, 70, 60)); // Verde militar escuro
        g.fillRect(x, y, width, height);
        g.setColor(new Color(40, 50, 40));
        g.drawRect(x, y, width, height);
        // Fechadura/Detalhes
        g.setColor(new Color(200, 200, 200));
        g.fillRect(x + width/2 - 4, y + height/2 - 4, 8, 8);
        g.setColor(br.com.ultimoabrigo.core.GameConfig.COLOR_METAL_SLUG_YELLOW);
        g.drawString("SUPPLY", x + 2, y + 15);
    }

    private void renderBonfire(Graphics2D g, int dx, int dy) {
        // Troncos na base
        g.setColor(new Color(50, 35, 20));
        g.fillRect(dx + 4, dy + height - 8, width - 8, 8);
        // Chamas animadas
        int f = (damageTick / 4) % 3;
        g.setColor(GameConfig.COLOR_DANGER_RED);
        g.fillOval(dx + 8, dy + 4 - f * 2, width - 16, height - 6);
        g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
        g.fillOval(dx + 12, dy + 8 - f * 3, width - 24, height - 12);
    }

    public ObstacleType getType() { return type; }
    public boolean isSolid() { return type.solid; }
}
