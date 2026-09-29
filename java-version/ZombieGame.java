/*
 * Decompiled with CFR 0.152.
 */
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class ZombieGame
extends JPanel
implements ActionListener,
KeyListener,
MouseMotionListener,
MouseListener {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private Timer timer;
    private Player player;
    private Core houseCore;
    private Workbench workbench;
    private ArrayList<SolidWall> solidWalls;
    private ArrayList<Window> windows;
    private ArrayList<Door> doors;
    private ArrayList<ResourceNode> resources;
    private java.util.ArrayList<LootChest> chests;
    private ArrayList<Enemy> enemies;
    private ArrayList<Bullet> bullets;
    private ArrayList<Drop> drops;
    private ArrayList<DamageText> damageTexts;
    public boolean[][] exploredMap = new boolean[100][100];
    public int gameState = 0;
    public boolean isMapOpen = false;
    public boolean isInventoryOpen = false;
    private boolean[] keys = new boolean[256];
    boolean isMousePressed = false;
    boolean isAiming = false;
    public static long lastPlayerShootTime = 0L;
    double camX = 0.0;
    double camY = 0.0;
    public static double mouseX = 400.0;
    public static double mouseY = 300.0;
    private Random random = new Random();
    private int killCount = 0;
    private int coins = 0;
    private int materials = 0;
    public int wood = 10, iron = 0, lead = 0, nails = 10, screws = 0, fuelCans = 0;
    public int food = 0, medkit = 0, crystals = 0;
    public int maxBackpackCapacity = 20;
    public int chestWood = 0, chestIron = 0, chestLead = 0, chestNails = 0, chestScrews = 0;
    public int maxChestCapacity = 50;
    public int[] globalAmmo = new int[]{50, 100, 100, 20, 5, 0}; // 0:Pistol, 1:LightMG, 2:HeavyMG, 3:Shotgun, 4:Special, 5:Fuel
    private int playerLevel = 1;
    private int playerExp = 0;
    private int expToNextLevel = 10;
    private int currentWave = 1;
    private boolean isWaveActive = true;
    private int zombiesToSpawn = 15;
    private int zombiesSpawnedThisWave = 0;
    private long lastSpawnTime = 0L;
    private boolean isRewardMenu = false;
    private Weapon pendingReward = null;
    private boolean isPaused = false;
    private boolean isLevelUpMenu = false;
    private boolean isShopMenu = false;
    private Weapon[] levelUpWeapons = new Weapon[2];
    private String levelUpStat = "";
    private int dmgUpgradeCost = 50;
    private int hpUpgradeCost = 50;
    private Weapon[] comuns;
    private Weapon[] incomuns;
    private Weapon[] raras;
    private Weapon[] lendarias;
    private Weapon[] miticas;
    public static java.awt.image.BufferedImage[][] imgChests = new java.awt.image.BufferedImage[3][3];
    public static java.awt.image.BufferedImage[][] imgTrees = new java.awt.image.BufferedImage[3][4];
    public static java.awt.image.BufferedImage[] imgRocks = new java.awt.image.BufferedImage[3];
    public static java.util.Map<String, java.awt.image.BufferedImage[]> weaponFrames = new java.util.HashMap();
    public static java.awt.image.BufferedImage imgPlayerIdle;
    public static java.awt.image.BufferedImage[] imgAmmoLight = new java.awt.image.BufferedImage[3];
    public static java.awt.image.BufferedImage[] imgAmmoMedium = new java.awt.image.BufferedImage[3];
    public static java.awt.image.BufferedImage[] imgAmmoShotgun = new java.awt.image.BufferedImage[3];
    public static java.awt.image.BufferedImage[] imgAmmoRPG = new java.awt.image.BufferedImage[4];
    public static java.awt.image.BufferedImage[] imgAmmoGrenade = new java.awt.image.BufferedImage[4];
    private static BufferedImage imgPlayerShoot;
    private static BufferedImage imgSupplies;
    private static BufferedImage imgWorkbench;
    private static BufferedImage imgBgGrass;
    private static BufferedImage imgFloorWood;
    private static BufferedImage imgWall;
    private static BufferedImage imgItemExp;
    private static BufferedImage imgItemCoin;
    private static BufferedImage imgItemMaterial;
    public static BufferedImage[] imgWallT;
    public static BufferedImage[] imgWindowT;
    public static BufferedImage[] imgDoorT;
    private static BufferedImage imgPortaTrue;
    private static BufferedImage[] imgBasic;
    private static BufferedImage[] imgRunner;
    private static BufferedImage[] imgSuperior;
    private static BufferedImage[] imgBoss;
    private static BufferedImage[] imgSuperboss;

    public ZombieGame() {
        this.setPreferredSize(new Dimension(800, 600));
        this.setFocusable(true);
        this.addKeyListener(this);
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
        this.loadImages();
        this.initWeapons();
        this.initGameObjects();
        this.timer = new Timer(16, this);
        this.timer.start();
    }

    private void loadImages() {
        try {
            imgPlayerIdle = ImageIO.read(this.getClass().getResourceAsStream("/img/player_andando_arma_baixa.png"));
            imgPlayerShoot = ImageIO.read(this.getClass().getResourceAsStream("/img/player_atirando_arma_levantada.png"));
            imgSupplies = ImageIO.read(this.getClass().getResourceAsStream("/img/suprimentos.png"));
            imgWorkbench = ImageIO.read(this.getClass().getResourceAsStream("/img/bancada.png"));
            imgBgGrass = ImageIO.read(this.getClass().getResourceAsStream("/img/bg_grass.png"));
            imgFloorWood = ImageIO.read(this.getClass().getResourceAsStream("/img/floor_wood.png"));
            imgWall = ImageIO.read(this.getClass().getResourceAsStream("/img/wall.png"));
            imgItemExp = ImageIO.read(this.getClass().getResourceAsStream("/img/item_exp.png"));
            imgItemCoin = ImageIO.read(this.getClass().getResourceAsStream("/img/item_coin.png"));
            imgItemMaterial = ImageIO.read(this.getClass().getResourceAsStream("/img/item_material.png"));
            imgPortaTrue = ImageIO.read(this.getClass().getResourceAsStream("/img/porta_true.png"));
            for (int i = 0; i < 5; ++i) {
                try {
                    ZombieGame.imgBasic[i] = ImageIO.read(this.getClass().getResourceAsStream("/img/zombie_basic_v0_f" + i + ".png"));
                }
                catch (Exception exception) {
                    // empty catch block
                }
                try {
                    ZombieGame.imgRunner[i] = ImageIO.read(this.getClass().getResourceAsStream("/img/zombie_runner_v0_f" + i + ".png"));
                }
                catch (Exception exception) {
                    // empty catch block
                }
                try {
                    ZombieGame.imgSuperior[i] = ImageIO.read(this.getClass().getResourceAsStream("/img/zombie_superior_v0_f" + i + ".png"));
                }
                catch (Exception exception) {
                    // empty catch block
                }
                try {
                    ZombieGame.imgBoss[i] = ImageIO.read(this.getClass().getResourceAsStream("/img/zombie_boss_v0_f" + i + ".png"));
                }
                catch (Exception exception) {
                    // empty catch block
                }
                try {
                    ZombieGame.imgSuperboss[i] = ImageIO.read(this.getClass().getResourceAsStream("/img/zombie_superboss_v0_f" + i + ".png"));
                    continue;
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
        catch (Exception exception) {
            System.out.println("Carregando imagens da pasta local /img/...");
            try {
                int n;
                imgPlayerIdle = ImageIO.read(new File("img/player_andando_arma_baixa.png"));
                imgPlayerShoot = ImageIO.read(new File("img/player_atirando_arma_levantada.png"));
            for (int i=0; i<3; i++) {
                File rockF = new File("img/rock_model" + i + ".png");
                if (rockF.exists()) imgRocks[i] = ImageIO.read(rockF);
                for (int j=0; j<4; j++) {
                    File treeF = new File("img/tree_model" + i + "_f" + j + ".png");
                    if (treeF.exists()) imgTrees[i][j] = ImageIO.read(treeF);
                }
                for (int j=0; j<3; j++) {
                    File chestF = new File("img/chest_m" + i + "_f" + j + ".png");
                    if (chestF.exists()) imgChests[i][j] = ImageIO.read(chestF);
                }
            }
            String[] weapons = {"revolver", "glock", "ak47", "shotgun", "minigun", "rpg", "lancachamas", "lancagranadas", "smg", "miniuzi", "martelo"};
            for (String w : weapons) {
                BufferedImage[] frames = new BufferedImage[6];
                for (int i = 0; i < 6; i++) {
                    File wf = new File("img/weapon_" + w + "_f" + i + ".png");
                    if (wf.exists()) frames[i] = ImageIO.read(wf);
                }
                weaponFrames.put(w, frames);
            }
                imgSupplies = ImageIO.read(new File("img/suprimentos.png"));
                imgWorkbench = ImageIO.read(new File("img/bancada.png"));
                imgBgGrass = ImageIO.read(new File("img/bg_grass.png"));
                imgFloorWood = ImageIO.read(new File("img/floor_wood.png"));
                imgWall = ImageIO.read(new File("img/wall.png"));
                imgItemExp = ImageIO.read(new File("img/item_exp.png"));
                imgItemCoin = ImageIO.read(new File("img/item_coin.png"));
                imgItemMaterial = ImageIO.read(new File("img/item_material.png"));
                for (n = 1; n <= 3; ++n) {
                    try {
                        ZombieGame.imgWallT[(n - 1) * 2] = ImageIO.read(new File("img/wall_t" + n + "_h.png"));
                    }
                    catch (Exception exception2) {
                        // empty catch block
                    }
                    try {
                        ZombieGame.imgWallT[(n - 1) * 2 + 1] = ImageIO.read(new File("img/wall_t" + n + "_v.png"));
                    }
                    catch (Exception exception3) {
                        // empty catch block
                    }
                    try {
                        ZombieGame.imgWindowT[(n - 1) * 2] = ImageIO.read(new File("img/window_t" + n + "_h.png"));
                    }
                    catch (Exception exception4) {
                        // empty catch block
                    }
                    try {
                        ZombieGame.imgWindowT[(n - 1) * 2 + 1] = ImageIO.read(new File("img/window_t" + n + "_v.png"));
                        ZombieGame.imgDoorT[(n - 1) * 2] = ImageIO.read(new File("img/door_t" + n + "_h.png"));
                        ZombieGame.imgDoorT[(n - 1) * 2 + 1] = ImageIO.read(new File("img/door_t" + n + "_v.png"));
                        continue;
                    }
                    catch (Exception exception5) {
                        // empty catch block
                    }
                }
                imgPortaTrue = ImageIO.read(new File("img/porta_true.png"));
                for (n = 0; n < 5; ++n) {
                    try {
                        ZombieGame.imgBasic[n] = ImageIO.read(new File("img/zombie_basic_v0_f" + n + ".png"));
                    }
                    catch (Exception exception6) {
                        // empty catch block
                    }
                    try {
                        ZombieGame.imgRunner[n] = ImageIO.read(new File("img/zombie_runner_v0_f" + n + ".png"));
                    }
                    catch (Exception exception7) {
                        // empty catch block
                    }
                    try {
                        ZombieGame.imgSuperior[n] = ImageIO.read(new File("img/zombie_superior_v0_f" + n + ".png"));
                    }
                    catch (Exception exception8) {
                        // empty catch block
                    }
                    try {
                        ZombieGame.imgBoss[n] = ImageIO.read(new File("img/zombie_boss_v0_f" + n + ".png"));
                    }
                    catch (Exception exception9) {
                        // empty catch block
                    }
                    try {
                        ZombieGame.imgSuperboss[n] = ImageIO.read(new File("img/zombie_superboss_v0_f" + n + ".png"));
                        continue;
                    }
                    catch (Exception exception10) {
                        // empty catch block
                    }
                }
            }
            catch (Exception exception11) {
                System.out.println("Imagens nao encontradas.");
            }
        }
    }

    private void initWeapons() {
        Color color = Color.WHITE;
        Color color2 = Color.GREEN;
        Color color3 = new Color(0, 191, 255);
        Color color4 = new Color(138, 43, 226);
        Color color5 = new Color(255, 215, 0);
        this.comuns = new Weapon[]{new Weapon(this, "Pistola 9mm", "Comum", 15, 300L, 1, 0.0, color, 1000.0), new Weapon(this, "Revolver Antigo", "Comum", 25, 600L, 1, 0.0, color, 1000.0), new Weapon(this, "Escopeta de Caca", "Comum", 20, 1000L, 3, 0.3, color, 800.0), new Weapon(this, "Submetralhadora Uzi", "Comum", 10, 150L, 1, 0.1, color, 1000.0), new Weapon(this, "Fuzil de Ferrolho", "Comum", 40, 1200L, 1, 0.0, color, 1200.0), new Weapon(this, "Carabina Simples", "Comum", 18, 250L, 1, 0.0, color, 1000.0), new Weapon(this, "Arma de Pregos", "Comum", 12, 200L, 1, 0.2, color, 600.0), new Weapon(this, "Besta de Madeira", "Comum", 35, 800L, 1, 0.0, color, 900.0), new Weapon(this, "Pistola Dupla", "Comum", 12, 400L, 2, 0.1, color, 1000.0), new Weapon(this, "Mosquete", "Comum", 50, 1500L, 1, 0.0, color, 1000.0)};
        this.incomuns = new Weapon[]{new Weapon(this, "Glock Estendida", "Incomum", 22, 250L, 1, 0.0, color2, 1000.0), new Weapon(this, "Revolver Reforcado", "Incomum", 35, 500L, 1, 0.0, color2, 1000.0), new Weapon(this, "Escopeta Cano Duplo", "Incomum", 25, 800L, 4, 0.35, color2, 800.0), new Weapon(this, "SMG Tatica", "Incomum", 15, 120L, 1, 0.08, color2, 1000.0), new Weapon(this, "Fuzil de Precisao", "Incomum", 60, 1000L, 1, 0.0, color2, 1500.0), new Weapon(this, "M16 Burst", "Incomum", 20, 300L, 2, 0.05, color2, 1000.0), new Weapon(this, "Lanca-Pregos Motor", "Incomum", 18, 150L, 1, 0.1, color2, 700.0), new Weapon(this, "Besta Composta", "Incomum", 50, 600L, 1, 0.0, color2, 1200.0), new Weapon(this, "Pistolas Gemeas", "Incomum", 15, 300L, 2, 0.1, color2, 1000.0), new Weapon(this, "Rifle de Caca Avanc.", "Incomum", 70, 1200L, 1, 0.0, color2, 1200.0)};
        this.raras = new Weapon[]{new Weapon(this, "Pistola Silenciada", "Rara", 35, 200L, 1, 0.0, color3, 1000.0), new Weapon(this, "Magnum .44", "Rara", 60, 400L, 1, 0.0, color3, 1200.0), new Weapon(this, "Escopeta Tatica", "Rara", 40, 600L, 5, 0.3, color3, 900.0), new Weapon(this, "P90", "Rara", 25, 90L, 1, 0.05, color3, 1000.0), new Weapon(this, "Sniper AWP", "Rara", 120, 1000L, 1, 0.0, color3, 2000.0), new Weapon(this, "AK-47", "Rara", 45, 180L, 1, 0.05, color3, 1000.0), new Weapon(this, "Fuzil a Laser", "Rara", 35, 100L, 1, 0.0, color3, 1500.0), new Weapon(this, "Besta Explosiva", "Rara", 80, 500L, 1, 0.0, color3, 1000.0), new Weapon(this, "Desert Eagle Dupla", "Rara", 45, 300L, 2, 0.1, color3, 1200.0), new Weapon(this, "M4A1", "Rara", 40, 150L, 1, 0.02, color3, 1100.0)};
        this.lendarias = new Weapon[]{new Weapon(this, "Aniquiladora (Magnum)", "Lendaria", 120, 300L, 2, 0.05, color4, 1500.0), new Weapon(this, "Escopeta do Caos", "Lendaria", 80, 400L, 8, 0.5, color4, 1000.0), new Weapon(this, "Arauto da Morte (AWP)", "Lendaria", 300, 800L, 1, 0.0, color4, 2500.0), new Weapon(this, "AK-47 Dourada", "Lendaria", 80, 120L, 2, 0.08, color4, 1200.0), new Weapon(this, "Raio da Morte", "Lendaria", 60, 50L, 1, 0.0, color4, 2000.0)};
        this.miticas = new Weapon[]{new Weapon(this, "RPG", "Mitica", 600, 1500L, 1, 0.0, color5, 600.0), new Weapon(this, "Minigun", "Mitica (Ultra Rara)", 25, 40L, 1, 0.15, color5, 1200.0), new Weapon(this, "Lanca-Chamas", "Mitica", 12, 20L, 5, 0.6, color5, 400.0), new Weapon(this, "Lanca-Granadas", "Mitica", 250, 800L, 3, 0.2, color5, 700.0)};
    }

    private void initGameObjects() {
        this.player = new Player(this, 400.0, 250.0);
        this.player.weapon1 = null;
        this.player.activeSlot = 1;
        this.houseCore = new Core(this);
        this.workbench = new Workbench(this, 200.0, 200.0);
        this.solidWalls = new ArrayList();
        this.windows = new ArrayList();
        this.doors = new ArrayList();
        this.resources = new ArrayList();
        this.chests = new java.util.ArrayList();
        this.enemies = new ArrayList();
        this.bullets = new ArrayList();
        this.drops = new ArrayList();
          this.drops.add(new Drop(this, 420.0, 370.0, this.comuns[0]));
            this.drops.add(new Drop(this, 400.0, 350.0, this.comuns[1])); // Revolver
          this.drops.add(new Drop(this, 450.0, 350.0, this.comuns[3])); // Uzi
          this.drops.add(new Drop(this, 350.0, 350.0, new Weapon(this, "Faca de Combate", "Especial", 15, 300L, 1, 0.0, java.awt.Color.LIGHT_GRAY, 250.0)));
        this.damageTexts = new ArrayList();
        this.buildHouse();
        this.zombiesSpawnedThisWave = 0;
        this.zombiesToSpawn = 15;
        this.lastSpawnTime = System.currentTimeMillis();
    }

    private void buildHouse() {
        this.solidWalls.add(new SolidWall(this, 150, 150, 210, 40));
        this.doors.add(new Door(360, 150, 80, 40, true));
        this.solidWalls.add(new SolidWall(this, 440, 150, 210, 40));
        
        this.solidWalls.add(new SolidWall(this, 150, 410, 210, 40));
        this.doors.add(new Door(360, 410, 80, 40, true));
        this.solidWalls.add(new SolidWall(this, 440, 410, 210, 40));
        
        this.solidWalls.add(new SolidWall(this, 150, 190, 40, 40));
        this.windows.add(new Window(this, 150, 230, 40, 60, false));
        this.solidWalls.add(new SolidWall(this, 150, 290, 40, 20));
        this.windows.add(new Window(this, 150, 310, 40, 60, false));
        this.solidWalls.add(new SolidWall(this, 150, 370, 40, 40));
        
        this.solidWalls.add(new SolidWall(this, 610, 190, 40, 40));
        this.windows.add(new Window(this, 610, 230, 40, 60, false));
        this.solidWalls.add(new SolidWall(this, 610, 290, 40, 20));
        this.windows.add(new Window(this, 610, 310, 40, 60, false));
        this.solidWalls.add(new SolidWall(this, 610, 370, 40, 40));
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (this.gameState == 1 && !this.isPaused) {
            this.update();
        }
        this.repaint();
    }

    private void update() {
        Iterator<Bullet> iterator;
        
        
        Weapon weapon;
        this.player.update(this.keys, this.solidWalls, this.windows, this.doors);
        for (ResourceNode r : this.resources) r.update();
        for (LootChest c : this.chests) c.update();
        Weapon weapon2 = weapon = this.player.activeSlot == 1 ? this.player.weapon1 : this.player.weapon2;
        if (weapon != null && this.isMousePressed && System.currentTimeMillis() - this.player.lastShotTime > weapon.cooldown) {
            this.mouseAttack(weapon);
        }
        if (this.isWaveActive) {
            if (this.zombiesSpawnedThisWave < this.zombiesToSpawn) {
                long l = Math.max(250, 1500 - this.currentWave * 150);
                if (System.currentTimeMillis() - this.lastSpawnTime > l) {
                    this.spawnEnemy();
                    ++this.zombiesSpawnedThisWave;
                    this.lastSpawnTime = System.currentTimeMillis();
                }
            } else if (this.enemies.isEmpty()) {
                this.isWaveActive = false;
                this.triggerWaveReward();
            }
        }
        Iterator<Bullet> iterator2 = this.bullets.iterator();
        while (iterator2.hasNext()) {
            Bullet object2 = iterator2.next();
            object2.update();
            if (!object2.isExpired()) continue;
            if (object2.weaponType != null && (object2.weaponType.equals("rpg") || object2.weaponType.equals("lancagranadas")) && !object2.isExploding) {
                object2.isExploding = true;
                object2.explodeTime = System.currentTimeMillis();
                for (Enemy aoeE : ZombieGame.this.enemies) {
                    if (!aoeE.dead && Math.hypot(aoeE.x - object2.x, aoeE.y - object2.y) < 150.0) {
                        aoeE.health -= object2.damage;
                        ZombieGame.this.damageTexts.add(new DamageText(ZombieGame.this, aoeE.x, aoeE.y, object2.damage));
                        if (aoeE.health <= 0) { aoeE.dead = true; aoeE.deathTime = System.currentTimeMillis(); }
                    }
                }
                continue;
            }
            iterator2.remove();
        }
        java.util.Iterator<Enemy> itE = this.enemies.iterator();
        while (itE.hasNext()) {
            Enemy objE = itE.next();
            objE.update(this.houseCore, this.windows, this.solidWalls, this.player);
            if (objE.getBounds().intersects(this.houseCore.getBounds())) {
                this.houseCore.takeDamage(1);
            }
            if (objE.getBounds().intersects(this.player.getBounds())) {
                this.player.takeDamage();
            }
            java.util.Iterator<Bullet> itB = this.bullets.iterator();
            while (itB.hasNext()) {
                Bullet bullet = itB.next();
                if (!objE.getBounds().intersects(bullet.getBounds())) continue;
                if (bullet.type.equals("fire")) {
                    if (System.currentTimeMillis() - objE.lastFireDamageTime <= 300L) continue;
                    objE.health -= bullet.damage;
                    this.damageTexts.add(new DamageText(this, objE.x, objE.y, bullet.damage));
                    objE.lastFireDamageTime = System.currentTimeMillis();
                    continue;
                }
                objE.health -= bullet.damage;
                this.damageTexts.add(new DamageText(this, objE.x, objE.y, bullet.damage));
                itB.remove();
                break;
            }
            if (objE.health <= 0 && !objE.dead) {
                ++this.killCount;
                this.generateDrops(objE.x, objE.y, objE.expValue, objE.type);
                objE.changeState("death");
                objE.dead = true;
            }
            if (!objE.dead || System.currentTimeMillis() - objE.deathTime <= 2000L) continue;
            itE.remove();
        }
        java.util.Iterator<Drop> itD = this.drops.iterator();
        while (itD.hasNext()) {
            Drop iteratorD = itD.next();
            double d = Math.hypot(iteratorD.x - this.player.x, iteratorD.y - this.player.y);
            if (d < 40.0) {
                if (iteratorD.type == 0) {
                    this.collectExp(iteratorD.value);
                } else if (iteratorD.type == 1) {
                    this.coins += iteratorD.value;
                } else if (iteratorD.type == 2) {
                    this.materials += iteratorD.value;
                }
                else if (iteratorD.type >= 8 && iteratorD.type <= 13) {
                    this.globalAmmo[iteratorD.type - 8] += iteratorD.value;
                } else if (iteratorD.type == 18) {
                    this.equipWeapon(iteratorD.weaponDrop);
                } else {
                    int cap = this.wood + this.nails + this.iron + this.screws + this.lead + this.food + this.medkit + this.crystals + this.fuelCans;
                    if (cap + iteratorD.value > this.maxBackpackCapacity) continue; // Backpack full!
                    if (iteratorD.type == 3) this.wood += iteratorD.value;
                    else if (iteratorD.type == 4) this.nails += iteratorD.value;
                    else if (iteratorD.type == 5) this.iron += iteratorD.value;
                    else if (iteratorD.type == 6) this.screws += iteratorD.value;
                    else if (iteratorD.type == 7) this.lead += iteratorD.value;
                    else if (iteratorD.type == 14) this.food += iteratorD.value;
                    else if (iteratorD.type == 15) this.medkit += iteratorD.value;
                    else if (iteratorD.type == 16) this.crystals += iteratorD.value;
                    else if (iteratorD.type == 17) this.fuelCans += iteratorD.value;
                }
                itD.remove();
                continue;
            }
            if (d < 150.0) {
                double d2 = Math.atan2(this.player.y - iteratorD.y, this.player.x - iteratorD.x);
                iteratorD.x += Math.cos(d2) * 500.0 * 0.016;
                iteratorD.y += Math.sin(d2) * 500.0 * 0.016;
            }
        }
        
        java.util.Iterator<DamageText> itT = this.damageTexts.iterator();
        while (itT.hasNext()) {
            DamageText damageText = itT.next();
            damageText.update();
            if (damageText.life > 0) continue;
            itT.remove();
        }
        if (this.houseCore.health <= 0 || this.player.health <= 0) {
            this.isPaused = true;
        }
    }

    private void triggerWaveReward() {
        this.isPaused = true;
        this.isRewardMenu = true;
        this.pendingReward = this.generateRandomWeapon();
    }

    private Weapon generateRandomWeapon() {
        int n = this.random.nextInt(100);
        if (n < 45) {
            return this.comuns[this.random.nextInt(this.comuns.length)];
        }
        if (n < 75) {
            return this.incomuns[this.random.nextInt(this.incomuns.length)];
        }
        if (n < 90) {
            return this.raras[this.random.nextInt(this.raras.length)];
        }
        if (n < 97) {
            return this.lendarias[this.random.nextInt(this.lendarias.length)];
        }
        int n2 = this.random.nextInt(100);
        if (n2 < 40) {
            return this.miticas[0];
        }
        if (n2 < 80) {
            return this.miticas[2];
        }
        if (n2 < 95) {
            return this.miticas[3];
        }
        return this.miticas[1];
    }

    private void generateDrops(double d, double d2, int n, String string) {
        int n2;
        this.drops.add(new Drop(this, d, d2, 0, n));
        int n3 = 2 + this.random.nextInt(4);
        if (string.equals("boss")) {
            n3 += 10;
        }
        if (string.equals("superboss")) {
            n3 += 50;
        }
        for (n2 = 0; n2 < n3; ++n2) {
            this.drops.add(new Drop(this, d + (double)this.random.nextInt(30) - 15.0, d2 + (double)this.random.nextInt(30) - 15.0, 1, 1));
        }
        if (this.random.nextInt(100) < 40 || string.equals("boss") || string.equals("superboss")) {
            n2 = 1 + this.random.nextInt(2);
            if (string.equals("boss")) {
                n2 += 3;
            }
            if (string.equals("superboss")) {
                n2 += 10;
            }
            for (int i = 0; i < n2; ++i) {
                this.drops.add(new Drop(this, d + (double)this.random.nextInt(30) - 15.0, d2 + (double)this.random.nextInt(30) - 15.0, 2, 1));
            }
        }
    }

    private void spawnEnemy() {
        double d;
        double d2;
        int n = this.random.nextInt(4);
        if (n == 0) {
            d2 = this.camX + this.random.nextInt(800);
            d = this.camY - 100.0;
        } else if (n == 1) {
            d2 = this.camX + this.random.nextInt(800);
            d = this.camY + 700.0;
        } else if (n == 2) {
            d2 = this.camX - 100.0;
            d = this.camY + this.random.nextInt(600);
        } else {
            d2 = this.camX + 900.0;
            d = this.camY + this.random.nextInt(600);
        }
        String string = "basic";
        int n2 = this.random.nextInt(100);
        if (this.currentWave >= 10 && this.currentWave % 5 == 0 && n2 < 10) {
            string = "superboss";
        } else if (this.currentWave >= 5 && n2 < 15) {
            string = "boss";
        } else if (this.currentWave >= 2 && n2 < 35) {
            string = "runner";
        }
        this.enemies.add(new Enemy(d2, d, string, this.currentWave));
    }

    private void collectExp(int n) {
        this.playerExp += n;
        if (this.playerExp >= this.expToNextLevel) {
            this.playerExp -= this.expToNextLevel;
            ++this.playerLevel;
            this.expToNextLevel = (int)((double)this.expToNextLevel * 1.5);
            this.triggerLevelUp();
        }
    }

    private void triggerLevelUp() {
        this.isPaused = true;
        this.isLevelUpMenu = true;
        this.levelUpWeapons[0] = this.generateRandomWeapon();
        this.levelUpWeapons[1] = this.generateRandomWeapon();
        String[] stringArray = new String[]{"+ Vida Maxima", "+ Velocidade Player", "Curar Player 100%", "+ Dano Base (Todas)"};
        this.levelUpStat = stringArray[this.random.nextInt(stringArray.length)];
    }

    private void mouseAttack(Weapon weapon) {
        double d = Math.atan2(mouseY + this.camY - this.player.y, mouseX + this.camX - this.player.x);
        this.player.lastShotTime = System.currentTimeMillis();
        lastPlayerShootTime = System.currentTimeMillis();
        double d2 = 1000.0;
        String string = weapon.name.toLowerCase();
        if (string.contains("escopeta") || string.contains("shotgun")) {
            d2 = 250.0;
        } else if (string.contains("pistola") || string.contains("revolver") || string.contains("glock")) {
            d2 = 450.0;
        } else if (string.contains("smg") || string.contains("uzi") || string.contains("p90")) {
            d2 = 400.0;
        }
        if (weapon.name.equals("Lanca-Chamas")) {
            for (int i = 0; i < weapon.projectiles; ++i) {
                double d3 = ((double)i - (double)(weapon.projectiles - 1) / 2.0) * weapon.spread;
                this.bullets.add(new Bullet(this, this.player.x, this.player.y, d + d3, weapon.damage + this.player.bonusDamage, weapon.projSpeed, "fire", 180.0, weapon.typeName, d + d3));
            }
        } else {
            for (int i = 0; i < weapon.projectiles; ++i) {
                double d4 = ((double)i - (double)(weapon.projectiles - 1) / 2.0) * weapon.spread;
                this.bullets.add(new Bullet(this, this.player.x, this.player.y, d + d4, weapon.damage + this.player.bonusDamage, weapon.projSpeed, "normal", d2, weapon.typeName, d + d4));
            }
        }
    }

    @Override
    public void paintComponent(Graphics graphics) {
        int n;
        int n2;
        int n3;
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D)graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (this.gameState == 0) { this.drawMainMenu(graphics2D); return; }
        if (this.gameState == 2) { this.drawSettingsMenu(graphics2D); return; }
        if (this.gameState == 3) { this.drawCreditsMenu(graphics2D); return; }
        this.camX = this.player.x - 400.0;
        this.camY = this.player.y - 300.0;
        graphics2D.translate(-this.camX, -this.camY);
        if (imgBgGrass != null) {
            n3 = (int)this.camX - (int)this.camX % 320 - 320;
            n = n2 = (int)this.camY - (int)this.camY % 320 - 320;
            while ((double)n < this.camY + 600.0 + 320.0) {
                int n4 = n3;
                while ((double)n4 < this.camX + 800.0 + 320.0) {
                    graphics2D.drawImage(imgBgGrass, n4, n, 320, 320, null);
                    n4 += 320;
                }
                n += 320;
            }
        } else {
            graphics2D.setColor(new Color(34, 139, 34));
            graphics2D.fillRect((int)this.camX, (int)this.camY, 800, 600);
        }
        if (imgFloorWood != null) {
            Shape shape = graphics2D.getClip();
            graphics2D.clipRect(150, 150, 500, 300);
            for (n2 = 150; n2 < 450; n2 += 64) {
                for (n = 150; n < 650; n += 64) {
                    graphics2D.drawImage((Image)imgFloorWood, n, n2, null);
                }
            }
            graphics2D.setClip(shape);
        } else {
            graphics2D.setColor(new Color(101, 67, 33));
            graphics2D.fillRect(150, 150, 500, 300);
            graphics2D.setColor(new Color(80, 50, 20));
            for (n3 = 150; n3 < 450; n3 += 30) {
                graphics2D.drawLine(150, n3, 650, n3);
            }
        }
        this.houseCore.draw(graphics2D);
        this.workbench.draw(graphics2D);
        for (ResourceNode r : this.resources) if (r.type == 1) r.draw(graphics2D);
        for (LootChest c : this.chests) c.draw(graphics2D);
        for (SolidWall solidWall : this.solidWalls) {
            solidWall.draw(graphics2D);
        }
        for (Window window : this.windows) {
            window.draw(graphics2D);
        }
        for (Door d : this.doors) d.draw(graphics2D);
        for (Drop drop : this.drops) {
            drop.draw(graphics2D);
        }
        for (Bullet bullet : this.bullets) {
            bullet.draw(graphics2D);
        }
        for (Enemy enemy : this.enemies) {
            enemy.draw(graphics2D);
        }
        for (ResourceNode r : this.resources) if (r.type == 0 && r.y + 100 < this.player.y) r.draw(graphics2D);
        this.player.draw(graphics2D);
        for (ResourceNode r : this.resources) if (r.type == 0 && r.y + 100 >= this.player.y) r.draw(graphics2D);
        for (DamageText damageText : this.damageTexts) {
            damageText.draw(graphics2D);
        }
        graphics2D.translate(this.camX, this.camY);
        this.drawCrosshair(graphics2D);
        this.drawUI(graphics2D);
        if (!this.isPaused) {
            for (Window window : this.windows) {
                if (window.health >= window.maxHealth || !(Math.hypot(this.player.x - window.centerX, this.player.y - window.centerY) < 60.0)) continue;
                graphics2D.setColor(Color.WHITE);
                graphics2D.setFont(new Font("Arial", 1, 14));
                graphics2D.drawString("[R] Consertar (10 Mat.)", (int)window.centerX - 60, (int)window.centerY - 20);
            }
            if (Math.hypot(this.player.x - this.workbench.x, this.player.y - this.workbench.y) < 60.0) {
                graphics2D.setColor(Color.WHITE);
                graphics2D.setFont(new Font("Arial", 1, 16));
                graphics2D.drawString("[E] Bancada", (int)this.player.x - 40, (int)this.player.y - 40);
            }
        }
        if (this.isMapOpen) {
                this.drawMap(graphics2D, 150, 50, 500, 0.03, false);
            } else {
                this.drawMap(graphics2D, 620, 20, 160, 0.1, true);
            }
            if (this.isInventoryOpen) {
                this.drawInventory(graphics2D);
            }
            if (this.isRewardMenu) {
            this.drawRewardMenu(graphics2D);
        } else if (this.isLevelUpMenu) {
            this.drawLevelUpMenu(graphics2D);
        } else if (this.isShopMenu) {
            this.drawShopMenu(graphics2D);
        } else if (this.houseCore.health <= 0 || this.player.health <= 0) {
            graphics2D.setColor(new Color(0, 0, 0, 150));
            graphics2D.fillRect(0, 0, 800, 600);
            graphics2D.setColor(Color.RED);
            graphics2D.setFont(new Font("Arial", 1, 60));
            graphics2D.drawString("A CASA CAIU!", 200, 300);
            graphics2D.setColor(Color.WHITE);
            graphics2D.setFont(new Font("Arial", 1, 20));
            graphics2D.drawString("Aperte ENTER para reiniciar", 270, 350);
        }
    }

    private void drawRewardMenu(Graphics2D graphics2D) {
        graphics2D.setColor(new Color(0, 0, 0, 230));
        graphics2D.fillRect(0, 0, 800, 600);
        graphics2D.setColor(Color.YELLOW);
        graphics2D.setFont(new Font("Arial", 1, 40));
        graphics2D.drawString("HORDA " + this.currentWave + " CONCLUIDA!", 180, 100);
        graphics2D.setColor(Color.WHITE);
        graphics2D.setFont(new Font("Arial", 1, 24));
        graphics2D.drawString("MALA DE ARMAS (RECOMPENSA DE FIM DE ONDA)", 100, 160);
        int n = 250;
        int n2 = 220;
        graphics2D.setColor(new Color(50, 50, 50));
        graphics2D.fillRect(n, n2, 300, 200);
        graphics2D.setColor(this.pendingReward.color);
        graphics2D.drawRect(n, n2, 300, 200);
        graphics2D.drawRect(n - 1, n2 - 1, 302, 202);
        graphics2D.setFont(new Font("Arial", 1, 22));
        graphics2D.drawString(this.pendingReward.name, n + 20, n2 + 40);
        graphics2D.setFont(new Font("Arial", 1, 16));
        graphics2D.setColor(Color.WHITE);
        graphics2D.drawString("Raridade: " + this.pendingReward.rarity, n + 20, n2 + 75);
        graphics2D.drawString("Dano: " + this.pendingReward.damage, n + 20, n2 + 105);
        graphics2D.drawString("Tiros: " + this.pendingReward.projectiles, n + 20, n2 + 135);
        graphics2D.drawString("Cad\u00eancia: " + this.pendingReward.cooldown + "ms", n + 20, n2 + 165);
        graphics2D.setFont(new Font("Arial", 1, 18));
        graphics2D.setColor(Color.GREEN);
        graphics2D.drawString("Aperte [F] para Equipar na mao ativa", n - 100, n2 + 250);
        graphics2D.setColor(Color.GRAY);
        graphics2D.drawString("Aperte [ESC] para Ignorar", n + 200, n2 + 250);
    }

    private void drawCrosshair(Graphics2D graphics2D) {
        graphics2D.setColor(new Color(255, 0, 0, 150));
        graphics2D.setStroke(new BasicStroke(2.0f));
        graphics2D.drawOval((int)mouseX - 10, (int)mouseY - 10, 20, 20);
        graphics2D.drawLine((int)mouseX - 15, (int)mouseY, (int)mouseX + 15, (int)mouseY);
        graphics2D.drawLine((int)mouseX, (int)mouseY - 15, (int)mouseX, (int)mouseY + 15);
    }

    private void drawButton(java.awt.Graphics2D g, String text, int x, int y, int w, int h) {
        g.setColor(new java.awt.Color(50, 50, 50));
        g.fillRect(x, y, w, h);
        g.setColor(java.awt.Color.WHITE);
        g.drawRect(x, y, w, h);
        g.setFont(new java.awt.Font("Arial", 1, 20));
        java.awt.FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(text);
        g.drawString(text, x + (w - tw)/2, y + (h + fm.getAscent())/2 - 3);
    }

    private void drawMainMenu(java.awt.Graphics2D g) {
        g.setColor(new java.awt.Color(20, 20, 20));
        g.fillRect(0, 0, 800, 600);
        g.setColor(java.awt.Color.RED);
        g.setFont(new java.awt.Font("Arial", 1, 60));
        g.drawString("ZOMBIE SURVIVAL", 120, 150);
        drawButton(g, "JOGAR", 300, 250, 200, 50);
        drawButton(g, "CONFIGURAÇÕES", 300, 320, 200, 50);
        drawButton(g, "CRÉDITOS", 300, 390, 200, 50);
    }

    private void drawSettingsMenu(java.awt.Graphics2D g) {
        g.setColor(new java.awt.Color(20, 20, 20));
        g.fillRect(0, 0, 800, 600);
        g.setColor(java.awt.Color.WHITE);
        g.setFont(new java.awt.Font("Arial", 1, 40));
        g.drawString("CONFIGURAÇÕES", 220, 150);
        g.setFont(new java.awt.Font("Arial", 0, 20));
        g.drawString("Música: Ligado", 320, 250);
        g.drawString("Efeitos: Ligado", 320, 300);
        g.drawString("Dificuldade: Normal", 320, 350);
        drawButton(g, "VOLTAR", 300, 500, 200, 50);
    }

    private void drawCreditsMenu(java.awt.Graphics2D g) {
        g.setColor(new java.awt.Color(20, 20, 20));
        g.fillRect(0, 0, 800, 600);
        g.setColor(java.awt.Color.WHITE);
        g.setFont(new java.awt.Font("Arial", 1, 40));
        g.drawString("CRÉDITOS", 290, 150);
        g.setFont(new java.awt.Font("Arial", 0, 20));
        g.drawString("Desenvolvido com Antigravit", 260, 250);
        g.drawString("Design: Você!", 320, 300);
        drawButton(g, "VOLTAR", 300, 500, 200, 50);
    }

    private void drawInventory(java.awt.Graphics2D g) {
        int w = 600, h = 500;
        int x = (800 - w) / 2;
        int y = (600 - h) / 2;
        g.setColor(new java.awt.Color(40, 40, 40, 230));
        g.fillRect(x, y, w, h);
        g.setColor(java.awt.Color.WHITE);
        g.setFont(new java.awt.Font("Arial", 1, 12));
        g.drawString("CURRENCIES", x + 20, y + 30);
        g.setFont(new java.awt.Font("Arial", 0, 12));
        g.drawString("Munição Global: PISTOLA["+globalAmmo[0]+"] FUZIL["+globalAmmo[1]+"] PESADA["+globalAmmo[2]+"] SHOTGUN["+globalAmmo[3]+"] EXPLOSIVO["+globalAmmo[4]+"]", x + 20, y + 50);
        g.setFont(new java.awt.Font("Arial", 1, 12));
        int currentCapacity = this.wood + this.nails + this.iron + this.screws + this.lead + this.food + this.medkit + this.crystals + this.fuelCans;
        g.drawString("RESOURCES (Peso: " + currentCapacity + " / " + this.maxBackpackCapacity + ")", x + 20, y + 90);
        int startX = x + 20;
        int startY = y + 110;
        int slotSize = 50;
        int padding = 10;
        String[] resNames = {"Wood", "Nails", "Iron", "Screws", "Lead", "Food", "Medkit", "Fuel", "Crystals"};
        int[] resCounts = {wood, nails, iron, screws, lead, food, medkit, fuelCans, crystals};
        java.awt.Color[] resColors = {new java.awt.Color(139, 69, 19), java.awt.Color.GRAY, java.awt.Color.DARK_GRAY, java.awt.Color.LIGHT_GRAY, java.awt.Color.BLACK, java.awt.Color.GREEN, java.awt.Color.WHITE, java.awt.Color.RED, java.awt.Color.MAGENTA};
        int col = 0, row = 0;
        for (int i = 0; i < 14; i++) {
            int cx = startX + col * (slotSize + padding);
            int cy = startY + row * (slotSize + padding);
            g.setColor(new java.awt.Color(60, 60, 60));
            g.fillRect(cx, cy, slotSize, slotSize);
            g.setColor(new java.awt.Color(100, 100, 100));
            g.drawRect(cx, cy, slotSize, slotSize);
            if (i < resCounts.length && resCounts[i] > 0) {
                g.setColor(resColors[i]);
                g.fillRect(cx + 10, cy + 10, slotSize - 20, slotSize - 20);
                g.setColor(java.awt.Color.WHITE);
                g.setFont(new java.awt.Font("Arial", 0, 9));
                g.drawString(resNames[i], cx + 2, cy + 10);
                g.setFont(new java.awt.Font("Arial", 1, 12));
                g.drawString(String.valueOf(resCounts[i]), cx + slotSize - 15, cy + slotSize - 5);
            }
            col++; if (col == 7) { col = 0; row++; }
        }
        g.setColor(java.awt.Color.WHITE);
        g.setFont(new java.awt.Font("Arial", 1, 12));
        g.drawString("LOADOUT", x + 20, y + 260);
        Weapon[] weapons = {player.weapon1, player.weapon2};
        for(int i = 0; i < 5; i++) {
            int cx = x + 20 + i * (slotSize + padding + 10);
            int cy = y + 280;
            g.setColor(new java.awt.Color(60, 60, 60));
            g.fillRect(cx, cy, slotSize + 10, slotSize + 10);
            if (i < 2 && weapons[i] != null) {
                java.awt.Color rC = weapons[i].rarity.equals("Lendaria") ? new java.awt.Color(255, 140, 0) :
                                   weapons[i].rarity.equals("Epica") ? new java.awt.Color(138, 43, 226) :
                                   weapons[i].rarity.equals("Rara") ? new java.awt.Color(0, 191, 255) : new java.awt.Color(50, 205, 50);
                g.setColor(rC);
                g.fillRect(cx, cy, slotSize + 10, slotSize + 10);
                g.setColor(java.awt.Color.WHITE);
                g.fillRect(cx + 15, cy + 15, 30, 30);
                g.setColor(java.awt.Color.BLACK);
                g.setFont(new java.awt.Font("Arial", 1, 10));
                g.drawString(weapons[i].name.substring(0, Math.min(6, weapons[i].name.length())), cx + 2, cy + 12);
                if (weapons[i].ammoType >= 0) {
                    g.setColor(java.awt.Color.WHITE);
                    g.setFont(new java.awt.Font("Arial", 1, 14));
                    g.drawString(String.valueOf(globalAmmo[weapons[i].ammoType]), cx + 35, cy + 55);
                }
            }
            g.setColor(new java.awt.Color(100, 100, 100));
            g.drawRect(cx, cy, slotSize + 10, slotSize + 10);
        }
        g.setColor(new java.awt.Color(30, 30, 30));
        g.fillRect(x + 20, y + 380, w - 40, 70);
        Weapon aW = player.activeSlot == 1 ? player.weapon1 : player.weapon2;
        if (aW != null) {
            g.setColor(java.awt.Color.WHITE);
            g.setFont(new java.awt.Font("Arial", 1, 16));
            g.drawString(aW.name.toUpperCase(), x + 30, y + 400);
            g.setFont(new java.awt.Font("Arial", 1, 12));
            java.awt.Color rC = aW.rarity.equals("Lendaria") ? new java.awt.Color(255, 140, 0) :
                               aW.rarity.equals("Epica") ? new java.awt.Color(138, 43, 226) :
                               aW.rarity.equals("Rara") ? new java.awt.Color(0, 191, 255) : new java.awt.Color(50, 205, 50);
            g.setColor(rC);
            g.drawString(aW.rarity.toUpperCase(), x + 30, y + 430);
            g.setColor(java.awt.Color.GRAY);
            g.drawString("Dano: " + aW.damage + " | Range: " + aW.cooldown, x + 150, y + 430);
        }
        g.setColor(java.awt.Color.WHITE);
        g.setFont(new java.awt.Font("Arial", 1, 12));
        g.drawString("[TAB] FECHAR", x + w - 120, y + h - 15);
    }

    private void drawMap(java.awt.Graphics2D g, int sx, int sy, int size, double scale, boolean isCircular) {
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
        if (isCircular) {
            g2.setClip(new java.awt.geom.Ellipse2D.Float(sx, sy, size, size));
        } else {
            g2.setClip(sx, sy, size, size);
        }
        g2.setColor(new java.awt.Color(20, 20, 20));
        g2.fillRect(sx, sy, size, size);
        g2.translate(sx + size / 2, sy + size / 2);
        g2.setColor(new java.awt.Color(34, 139, 34));
        for (int cx = 0; cx < 100; cx++) {
            for (int cy = 0; cy < 100; cy++) {
                if (this.exploredMap[cx][cy]) {
                    int worldX = cx * 200 - 10000;
                    int worldY = cy * 200 - 10000;
                    int drawX = (int) ((worldX - this.player.x) * scale);
                    int drawY = (int) ((worldY - this.player.y) * scale);
                    int drawW = (int) (200 * scale) + 1;
                    g2.fillRect(drawX, drawY, drawW, drawW);
                }
            }
        }
        int hx = (int) ((400 - this.player.x) * scale);
        int hy = (int) ((300 - this.player.y) * scale);
        g2.setColor(java.awt.Color.BLUE);
        g2.fillRect(hx - 5, hy - 5, 10, 10);
        for (ResourceNode r : this.resources) {
            int cx = (int) Math.floor((r.x + 10000) / 200.0);
            int cy = (int) Math.floor((r.y + 10000) / 200.0);
            if (cx >= 0 && cx < 100 && cy >= 0 && cy < 100 && this.exploredMap[cx][cy]) {
                int rx = (int) ((r.x - this.player.x) * scale);
                int ry = (int) ((r.y - this.player.y) * scale);
                g2.setColor(r.type == 0 ? new java.awt.Color(0, 100, 0) : java.awt.Color.GRAY);
                g2.fillOval(rx - 2, ry - 2, 4, 4);
            }
        }
        for (LootChest c : this.chests) {
            if (c.state != 2) {
                int cx = (int) Math.floor((c.x + 10000) / 200.0);
                int cy = (int) Math.floor((c.y + 10000) / 200.0);
                if (cx >= 0 && cx < 100 && cy >= 0 && cy < 100 && this.exploredMap[cx][cy]) {
                    int rx = (int) ((c.x - this.player.x) * scale);
                    int ry = (int) ((c.y - this.player.y) * scale);
                    g2.setColor(java.awt.Color.YELLOW);
                    g2.fillRect(rx - 3, ry - 3, 6, 6);
                }
            }
        }
        g2.setColor(java.awt.Color.RED);
        g2.fillOval(-3, -3, 6, 6);
        g2.dispose();
        if (isCircular) {
            g.setColor(new java.awt.Color(101, 67, 33));
            g.setStroke(new java.awt.BasicStroke(6));
            g.drawOval(sx, sy, size, size);
            g.setColor(java.awt.Color.WHITE);
            g.setFont(new java.awt.Font("Arial", 1, 14));
            g.drawString("N", sx + size / 2 - 5, sy + 15);
            g.drawString("S", sx + size / 2 - 5, sy + size - 5);
            g.drawString("W", sx + 5, sy + size / 2 + 5);
            g.drawString("E", sx + size - 15, sy + size / 2 + 5);
            double distHome = Math.hypot(400 - this.player.x, 300 - this.player.y);
            if (distHome * scale > size / 2) {
                double angle = Math.atan2(300 - this.player.y, 400 - this.player.x);
                int indX = sx + size / 2 + (int) (Math.cos(angle) * (size / 2 - 10));
                int indY = sy + size / 2 + (int) (Math.sin(angle) * (size / 2 - 10));
                g.setColor(java.awt.Color.CYAN);
                g.fillOval(indX - 4, indY - 4, 8, 8);
                g.setFont(new java.awt.Font("Arial", 0, 9));
                g.drawString("BASE", indX - 12, indY + 12);
            }
            g.setColor(java.awt.Color.WHITE);
            g.setFont(new java.awt.Font("Arial", 0, 12));
            g.drawString("X: " + (int) this.player.x + " Y: " + (int) this.player.y, sx + size / 2 - 35, sy + size + 20);
        } else {
            g.setColor(java.awt.Color.WHITE);
            g.setStroke(new java.awt.BasicStroke(4));
            g.drawRect(sx, sy, size, size);
            g.setFont(new java.awt.Font("Arial", 1, 18));
            g.drawString("MAPA MUNDI (M para fechar)", sx + 10, sy - 10);
        }
    }

    private void drawUI(Graphics2D graphics2D) {
        graphics2D.setColor(Color.BLACK);
        graphics2D.fillRect(10, 10, 780, 15);
        graphics2D.setColor(new Color(0, 150, 255));
        int n = (int)(780.0 * ((double)this.playerExp / (double)this.expToNextLevel));
        graphics2D.fillRect(10, 10, n, 15);
        graphics2D.setColor(Color.WHITE);
        graphics2D.setFont(new Font("Arial", 1, 12));
        graphics2D.drawString("Level " + this.playerLevel, 740, 22);
        graphics2D.setColor(Color.BLACK);
        graphics2D.fillRect(10, 570, 200, 20);
        graphics2D.setColor(Color.GREEN);
        int n2 = (int)(200.0 * ((double)this.player.health / (double)this.player.maxHealth));
        graphics2D.fillRect(10, 570, Math.max(0, n2), 20);
        graphics2D.setColor(Color.WHITE);
        graphics2D.drawString("Player HP: " + this.player.health, 15, 585);
        graphics2D.setColor(Color.BLACK);
        graphics2D.fillRect(590, 570, 200, 20);
        graphics2D.setColor(Color.CYAN);
        int n3 = (int)(200.0 * ((double)this.houseCore.health / (double)this.houseCore.maxHealth));
        graphics2D.fillRect(590, 570, Math.max(0, n3), 20);
        graphics2D.setColor(Color.WHITE);
        graphics2D.drawString("Casa HP: " + this.houseCore.health, 595, 585);
        Weapon aw = this.player.activeSlot == 1 ? this.player.weapon1 : this.player.weapon2;
        graphics2D.setColor(java.awt.Color.WHITE);
        graphics2D.setFont(new java.awt.Font("Arial", 1, 14));
        if (aw != null && aw.ammoType >= 0) {
            graphics2D.drawString("ARMA: " + aw.name + " | Muni: " + aw.ammo + "/" + aw.maxAmmo + " | Reserva: " + this.globalAmmo[aw.ammoType], 300, 585);
        }
        graphics2D.setFont(new java.awt.Font("Arial", 0, 12));
        graphics2D.drawString("Mad:" + this.wood + " Pregos:" + this.nails + " Ferro:" + this.iron + " Parf:" + this.screws + " Chum:" + this.lead, 15, 80);
        int currentCapacity = this.wood + this.nails + this.iron + this.screws + this.lead + this.food + this.medkit + this.crystals + this.fuelCans;
        graphics2D.drawString("MOCHILA: " + currentCapacity + " / " + this.maxBackpackCapacity, 15, 100);
        int chestCurrent = this.chestWood + this.chestIron + this.chestLead + this.chestNails + this.chestScrews;
        graphics2D.drawString("BAU DA CASA: " + chestCurrent + " / " + this.maxChestCapacity + " (Aperte E no centro da casa para depositar)", 15, 120);
        graphics2D.setFont(new Font("Arial", 1, 20));
        if (this.isWaveActive) {
            int n4 = this.zombiesToSpawn - this.zombiesSpawnedThisWave;
            int n5 = this.enemies.size();
            graphics2D.setColor(Color.WHITE);
            graphics2D.drawString("Horda " + this.currentWave + " - Zumbis: " + (n4 + n5), 300, 50);
        } else {
            graphics2D.setColor(Color.GREEN);
            graphics2D.drawString("Limpando resto...", 330, 50);
        }
        graphics2D.setFont(new Font("Arial", 1, 16));
        graphics2D.setColor(Color.YELLOW);
        graphics2D.drawString("Moedas: " + this.coins, 280, 75);
        graphics2D.setColor(Color.LIGHT_GRAY);
        graphics2D.drawString("Materiais: " + this.materials, 420, 75);
        graphics2D.setFont(new Font("Arial", 1, 16));
        if (this.player.weapon1 == null) {
            graphics2D.setColor(java.awt.Color.GRAY);
            graphics2D.drawString("1: Vazio", 10, 50);
        } else {
            graphics2D.setColor(this.player.weapon1.color);
            String string = "1: " + this.player.weapon1.name;

        if (this.player.isReloading) return;
        if (this.player.activeSlot == 1) {
            string = "> " + string + " <";
        }
        graphics2D.drawString(string, 10, 50);
        }
        if (this.playerLevel >= 5 || this.player.weapon2 != null) {
            graphics2D.setColor(this.player.weapon2 == null ? Color.GRAY : this.player.weapon2.color);
            String string2 = "2: " + (this.player.weapon2 == null ? "Vazio" : this.player.weapon2.name);
            if (this.player.activeSlot == 2) {
                string2 = "> " + string2 + " <";
            }
            graphics2D.drawString(string2, 10, 70);
            graphics2D.setColor(Color.WHITE);
            graphics2D.drawString("[Q] Trocar de Arma", 10, 90);
        } else {
            graphics2D.setColor(Color.GRAY);
            graphics2D.drawString("[Slot 2 Destrava N\u00edvel 5]", 10, 70);
        }
    }

    private void drawLevelUpMenu(Graphics2D graphics2D) {
        int n;
        int n2;
        graphics2D.setColor(new Color(0, 0, 0, 200));
        graphics2D.fillRect(0, 0, 800, 600);
        graphics2D.setColor(Color.YELLOW);
        graphics2D.setFont(new Font("Arial", 1, 40));
        graphics2D.drawString("LEVEL UP!", 300, 80);
        graphics2D.setFont(new Font("Arial", 1, 20));
        graphics2D.setColor(Color.WHITE);
        graphics2D.drawString("Aperte 1, 2 ou 3 para escolher uma ARMA ou STATUS", 150, 120);
        for (n2 = 0; n2 < 2; ++n2) {
            n = 100 + n2 * 200;
            int n3 = 150;
            graphics2D.setColor(new Color(50, 50, 50));
            graphics2D.fillRect(n, n3, 180, 250);
            graphics2D.setColor(this.levelUpWeapons[n2].color);
            graphics2D.drawRect(n, n3, 180, 250);
            graphics2D.setFont(new Font("Arial", 1, 16));
            graphics2D.drawString(this.levelUpWeapons[n2].name, n + 10, n3 + 40);
            graphics2D.setColor(Color.WHITE);
            graphics2D.setFont(new Font("Arial", 0, 14));
            graphics2D.drawString("Raridade: " + this.levelUpWeapons[n2].rarity, n + 10, n3 + 70);
            graphics2D.drawString("Dano: " + this.levelUpWeapons[n2].damage, n + 10, n3 + 100);
            graphics2D.drawString("Tiros: " + this.levelUpWeapons[n2].projectiles, n + 10, n3 + 130);
            graphics2D.drawString("Cooldown: " + this.levelUpWeapons[n2].cooldown + "ms", n + 10, n3 + 160);
            graphics2D.setFont(new Font("Arial", 1, 18));
            graphics2D.drawString("Aperte [" + (n2 + 1) + "]", n + 40, n3 + 230);
        }
        n2 = 500;
        n = 150;
        graphics2D.setColor(Color.DARK_GRAY);
        graphics2D.fillRect(n2, n, 180, 250);
        graphics2D.setColor(Color.WHITE);
        graphics2D.drawRect(n2, n, 180, 250);
        graphics2D.setFont(new Font("Arial", 1, 18));
        graphics2D.drawString("STATUS", n2 + 50, n + 40);
        graphics2D.setColor(Color.CYAN);
        graphics2D.drawString(this.levelUpStat, n2 + 10, n + 120);
        graphics2D.setColor(Color.WHITE);
        graphics2D.drawString("Aperte [3]", n2 + 40, n + 230);
    }

    private void drawShopMenu(Graphics2D graphics2D) {
        graphics2D.setColor(new Color(0, 0, 0, 220));
        graphics2D.fillRect(0, 0, 800, 600);
        graphics2D.setColor(Color.ORANGE);
        graphics2D.setFont(new Font("Arial", 1, 40));
        graphics2D.drawString("BANCADA DO JOGADOR", 170, 100);
        graphics2D.setColor(Color.YELLOW);
        graphics2D.setFont(new Font("Arial", 1, 24));
        graphics2D.drawString("Moedas: " + this.coins, 330, 150);
        graphics2D.setColor(Color.WHITE);
        graphics2D.setFont(new Font("Arial", 1, 20));
        graphics2D.drawString("Aperte E ou ESC para Sair", 280, 550);
        this.drawShopCard(graphics2D, 0, "+ Dano Base", this.dmgUpgradeCost, "Aperte 1");
        this.drawShopCard(graphics2D, 1, "+ Vida Max", this.hpUpgradeCost, "Aperte 2");
    }

    private void drawShopCard(Graphics2D graphics2D, int n, String string, int n2, String string2) {
        int n3 = 200 + n * 200;
        int n4 = 200;
        if (this.coins >= n2) {
            graphics2D.setColor(new Color(0, 100, 0));
        } else {
            graphics2D.setColor(new Color(100, 0, 0));
        }
        graphics2D.fillRect(n3, n4, 180, 250);
        graphics2D.setColor(Color.WHITE);
        graphics2D.drawRect(n3, n4, 180, 250);
        graphics2D.setFont(new Font("Arial", 1, 18));
        graphics2D.drawString(string, n3 + 10, n4 + 100);
        graphics2D.setColor(Color.YELLOW);
        graphics2D.drawString(n2 + " Moedas", n3 + 10, n4 + 140);
        graphics2D.setColor(Color.WHITE);
        graphics2D.drawString(string2, n3 + 40, n4 + 230);
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        mouseX = mouseEvent.getX();
        mouseY = mouseEvent.getY();
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        mouseX = mouseEvent.getX();
        mouseY = mouseEvent.getY();
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        block21: {
            block19: {
                block20: {
                    if (keyEvent.getKeyCode() < 256) {
                        this.keys[keyEvent.getKeyCode()] = true;
                    }
                    if (keyEvent.getKeyCode() == 77) { this.isMapOpen = !this.isMapOpen; }
                    if (keyEvent.getKeyCode() == 9) { this.isInventoryOpen = !this.isInventoryOpen; }
                    if (!this.isRewardMenu) break block19;
                    if (keyEvent.getKeyCode() != 70) break block20;
                    this.equipWeapon(this.pendingReward);
                    this.nextWave();
                    break block21;
                }
                if (keyEvent.getKeyCode() != 27) break block21;
                this.nextWave();
                break block21;
            }
            if (this.isLevelUpMenu) {
                if (keyEvent.getKeyCode() == 49) {
                    this.applyLevelUp(0);
                }
                if (keyEvent.getKeyCode() == 50) {
                    this.applyLevelUp(1);
                }
                if (keyEvent.getKeyCode() == 51) {
                    this.applyLevelUp(2);
                }
            } else if (this.isShopMenu) {
                if (keyEvent.getKeyCode() == 27 || keyEvent.getKeyCode() == 69) {
                    this.isShopMenu = false;
                    this.isPaused = false;
                }
                if (keyEvent.getKeyCode() == 49) {
                    this.buyUpgrade("dano");
                }
                if (keyEvent.getKeyCode() == 50) {
                    this.buyUpgrade("hp");
                }
            } else if (this.houseCore.health <= 0 || this.player.health <= 0) {
                if (keyEvent.getKeyCode() == 10) {
                    this.resetGame();
                }
            } else {
                if (keyEvent.getKeyCode() == 81 && (this.playerLevel >= 5 || this.player.weapon2 != null)) {
                    int n = this.player.activeSlot = this.player.activeSlot == 1 ? 2 : 1;
                    if (this.player.activeSlot == 2 && this.player.weapon2 == null) {
                        this.player.activeSlot = 1;
                    }
                }
                if (keyEvent.getKeyCode() == 69 && Math.hypot(this.player.x - this.workbench.x, this.player.y - this.workbench.y) < 60.0) {
                    this.isPaused = true;
                    this.isShopMenu = true;
                    this.keys[69] = false;
                }
                Weapon wAct = this.player.activeSlot == 1 ? this.player.weapon1 : this.player.weapon2;
                if (keyEvent.getKeyCode() == 82 && wAct != null && wAct.ammoType >= 0) {
                    this.player.isReloading = true;
                    this.player.reloadStartTime = System.currentTimeMillis();
                }
                if (keyEvent.getKeyCode() == 82) {
                    for (Window window : this.windows) {
                        if (window.health >= window.maxHealth || !(Math.hypot(this.player.x - window.centerX, this.player.y - window.centerY) < 60.0) || this.materials < 10) continue;
                        this.materials -= 10;
                        window.health = window.maxHealth;
                    }
                }
            }
        }
    }

    private void equipWeapon(Weapon weapon) {
        if (this.playerLevel >= 5 && this.player.weapon2 == null) {
            this.player.weapon2 = weapon;
            this.player.activeSlot = 2;
        } else if (this.player.isReloading) return;
        if (this.player.activeSlot == 1) {
            this.player.weapon1 = weapon;
        } else {
            this.player.weapon2 = weapon;
        }
    }

    private void nextWave() {
        ++this.currentWave;
        this.zombiesToSpawn += 15;
        this.zombiesSpawnedThisWave = 0;
        this.lastSpawnTime = System.currentTimeMillis();
        this.isWaveActive = true;
        this.isRewardMenu = false;
        this.isPaused = false;
    }

    private void buyUpgrade(String string) {
        if (string.equals("dano") && this.coins >= this.dmgUpgradeCost) {
            this.coins -= this.dmgUpgradeCost;
            this.player.bonusDamage += 5;
            this.dmgUpgradeCost += 30;
        } else if (string.equals("hp") && this.coins >= this.hpUpgradeCost) {
            this.coins -= this.hpUpgradeCost;
            this.player.maxHealth += 20;
            this.player.health += 20;
            this.hpUpgradeCost += 30;
        }
    }

    private void applyLevelUp(int n) {
        if (n == 0) {
            this.equipWeapon(this.levelUpWeapons[0]);
        } else if (n == 1) {
            this.equipWeapon(this.levelUpWeapons[1]);
        } else if (n == 2) {
            if (this.levelUpStat.equals("+ Vida Maxima")) {
                this.player.maxHealth += 30;
                this.player.health += 30;
            } else if (this.levelUpStat.equals("+ Velocidade Player")) {
                this.player.speed += 30.0;
            } else if (this.levelUpStat.equals("Curar Player 100%")) {
                this.player.health = this.player.maxHealth;
            } else if (this.levelUpStat.equals("+ Dano Base (Todas)")) {
                this.player.bonusDamage += 10;
            }
        }
        this.isLevelUpMenu = false;
        this.isPaused = false;
    }

    private void resetGame() {
        this.killCount = 0;
        this.coins = 0;
        this.materials = 0;
        this.playerLevel = 1;
        this.playerExp = 0;
        this.expToNextLevel = 10;
        this.currentWave = 1;
        this.dmgUpgradeCost = 50;
        this.hpUpgradeCost = 50;
        this.initGameObjects();
        this.isPaused = false;
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() < 256) {
            this.keys[keyEvent.getKeyCode()] = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        int mx = mouseEvent.getX();
        int my = mouseEvent.getY();
        if (this.gameState == 0) {
            if (mx >= 300 && mx <= 500 && my >= 250 && my <= 300) {
                this.gameState = 1;
            } else if (mx >= 300 && mx <= 500 && my >= 320 && my <= 370) {
                this.gameState = 2;
            } else if (mx >= 300 && mx <= 500 && my >= 390 && my <= 440) {
                this.gameState = 3;
            }
            return;
        }
        if (this.gameState == 2 || this.gameState == 3) {
            if (mx >= 300 && mx <= 500 && my >= 500 && my <= 550) {
                this.gameState = 0;
            }
            return;
        }
        if (mouseEvent.getButton() == 1) {
            this.isMousePressed = true;
        }
        if (mouseEvent.getButton() == 3) {
            this.isAiming = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == 1) {
            this.isMousePressed = false;
        }
        if (mouseEvent.getButton() == 3) {
            this.isAiming = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
    }

    public static void main(String[] stringArray) {
        JFrame jFrame = new JFrame("House Defense - Animated Zombies Update");
        ZombieGame zombieGame = new ZombieGame();
        jFrame.add(zombieGame);
        jFrame.pack();
        jFrame.setDefaultCloseOperation(3);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }

    static {
        imgWallT = new BufferedImage[6];
        imgWindowT = new BufferedImage[6];
        imgDoorT = new BufferedImage[6];
        imgBasic = new BufferedImage[5];
        imgRunner = new BufferedImage[5];
        imgSuperior = new BufferedImage[5];
        imgBoss = new BufferedImage[5];
        imgSuperboss = new BufferedImage[5];
    }

    class Weapon {
        String name;
        String typeName;
        int maxAmmo;
        int ammo;
        int ammoType;
        double weight = 1.0;
        String rarity;
        int damage;
        long cooldown;
        int projectiles;
        double spread;
        Color color;
        double projSpeed;

        Weapon(ZombieGame zombieGame, String string, String string2, int n, long l, int n2, double d, Color color, double d2) {
            this.name = string;
            this.typeName = "glock";
            this.maxAmmo = 30;
            this.ammo = 30;
            this.ammoType = 0;
            this.rarity = string2;
            this.damage = n;
            this.cooldown = l;
            this.projectiles = n2;
            this.spread = d;
            this.color = color;
            this.projSpeed = d2;
            
            String nLower = this.name.toLowerCase();
            if (nLower.contains("martelo")) { this.typeName = "martelo"; this.ammoType = -1; this.maxAmmo = 1; }
            else if (nLower.contains("faca")) { this.typeName = "faca"; this.ammoType = -1; this.maxAmmo = 1; this.weight = 1.0; }

            else if (nLower.contains("glock") || nLower.contains("pistola")) { this.typeName = "glock"; this.ammoType = 0; this.maxAmmo = 15; }
            else if (nLower.contains("revolver") || nLower.contains("magnum") || nLower.contains("eagle")) { this.typeName = "revolver"; this.ammoType = 0; this.maxAmmo = 6; }
            else if (nLower.contains("smg") || nLower.contains("p90")) { this.typeName = "smg"; this.ammoType = 1; this.maxAmmo = 30; }
            else if (nLower.contains("uzi")) { this.typeName = "miniuzi"; this.ammoType = 1; this.maxAmmo = 30; }
            else if (nLower.contains("ak-47") || nLower.contains("m16") || nLower.contains("fuzil") || nLower.contains("m4")) { this.typeName = "ak47"; this.ammoType = 2; this.maxAmmo = 30; this.weight = 0.8; }
            else if (nLower.contains("minigun")) { this.typeName = "minigun"; this.ammoType = 2; this.maxAmmo = 100; this.weight = 0.5; }
            else if (nLower.contains("escopeta")) { this.typeName = "shotgun"; this.ammoType = 3; this.maxAmmo = 8; this.weight = 0.8; }
            else if (nLower.contains("rpg")) { this.typeName = "rpg"; this.ammoType = 4; this.maxAmmo = 1; this.weight = 0.6; }
            else if (nLower.contains("granada")) { this.typeName = "lancagranadas"; this.ammoType = 4; this.maxAmmo = 1; this.weight = 0.7; }
            else if (nLower.contains("chamas")) { this.typeName = "lancachamas"; this.ammoType = 5; this.maxAmmo = 50; this.weight = 0.7; }
            else { this.typeName = "glock"; this.ammoType = 0; this.maxAmmo = 10; }
            this.ammo = this.maxAmmo;
        }
    }

    class Player {
        double x;
        double y;
        boolean isMoving = false;
        long wobbleTime = 0;
        double speed = 250.0;
        int maxHealth = 100;
        int health = 100;
        int bonusDamage = 0;
        long lastDamageTime = 0L;
        long lastShotTime = 0L;
        boolean isReloading = false;
        long reloadStartTime = 0L;
        Weapon weapon1;
        Weapon weapon2;
        int activeSlot = 1;

        Player(ZombieGame zombieGame, double d, double d2) {
            this.x = d;
            this.y = d2;
        }

        void update(boolean[] blArray, java.util.ArrayList<SolidWall> arrayList, java.util.ArrayList<Window> windows, java.util.ArrayList<Door> doors) {
            double d3 = 0.0;
            double d4 = 0.0;
            int pCellX = (int) Math.floor((this.x + 10000) / 200.0);
            int pCellY = (int) Math.floor((this.y + 10000) / 200.0);
            for (int cx = pCellX - 4; cx <= pCellX + 4; cx++) {
                for (int cy = pCellY - 4; cy <= pCellY + 4; cy++) {
                    if (cx >= 0 && cx < 100 && cy >= 0 && cy < 100) {
                        ZombieGame.this.exploredMap[cx][cy] = true;
                    }
                }
            }
            Weapon wActive = this.activeSlot == 1 ? this.weapon1 : this.weapon2;
            if (wActive == null && ZombieGame.this.isMousePressed) ZombieGame.this.isMousePressed = false;
            double wWeight = (wActive != null) ? wActive.weight : 1.0;
            double currSpeed = this.speed * wWeight;
            if (ZombieGame.this.isAiming) currSpeed *= 0.5;
            if (blArray[37] || blArray[65]) d3 = -currSpeed;
            if (blArray[39] || blArray[68]) d3 = currSpeed;
            if (blArray[38] || blArray[87]) d4 = -currSpeed;
            if (blArray[40] || blArray[83]) d4 = currSpeed;
            if (d3 != 0.0 && d4 != 0.0) { d3 *= 0.7071; d4 *= 0.7071; }
            this.isMoving = (d3 != 0.0 || d4 != 0.0);
            if (blArray[69]) {
                blArray[69] = false;
                // 1. Doors
                for (Door doorItem : doors) {
                    if (Math.hypot(doorItem.x+doorItem.width/2 - this.x, doorItem.y+doorItem.height/2 - this.y) < 80) {
                        doorItem.toggle();
                    }
                }
                // 2. Chests
                for (LootChest c : ZombieGame.this.chests) {
                    if (c.state == 0 && Math.hypot((c.x+30) - this.x, (c.y+30) - this.y) < 80) {
                        c.state = 1;
                        c.openTime = System.currentTimeMillis();
                    }
                }
                // 3. HomeStorage
                if (Math.hypot(ZombieGame.this.houseCore.x - this.x, ZombieGame.this.houseCore.y - this.y) < 80) {
                    ZombieGame.this.chestWood += ZombieGame.this.wood; ZombieGame.this.wood = 0;
                    ZombieGame.this.chestNails += ZombieGame.this.nails; ZombieGame.this.nails = 0;
                    ZombieGame.this.chestIron += ZombieGame.this.iron; ZombieGame.this.iron = 0;
                    ZombieGame.this.chestScrews += ZombieGame.this.screws; ZombieGame.this.screws = 0;
                    ZombieGame.this.chestLead += ZombieGame.this.lead; ZombieGame.this.lead = 0;
                }
            }
            if (this.isMoving) this.wobbleTime += 16;
            else this.wobbleTime = 0;
            double d2 = this.x + d3 * 0.016;
            java.awt.Rectangle boundsX = new java.awt.Rectangle((int)d2 - 15, (int)this.y - 15, 30, 30);
            boolean canMoveX = true;
            for (SolidWall w : arrayList) if (w.getBounds().intersects(boundsX)) canMoveX = false;
            for (Window w : windows) if (w.health > 0 && w.getBounds().intersects(boundsX)) canMoveX = false;
            for (Door doorItem : doors) if (doorItem.getBounds().intersects(boundsX)) canMoveX = false;
            if (canMoveX) this.x = d2;
            double d = this.y + d4 * 0.016;
            java.awt.Rectangle boundsY = new java.awt.Rectangle((int)this.x - 15, (int)d - 15, 30, 30);
            boolean canMoveY = true;
            for (SolidWall w : arrayList) if (w.getBounds().intersects(boundsY)) canMoveY = false;
            for (Window w : windows) if (w.health > 0 && w.getBounds().intersects(boundsY)) canMoveY = false;
            for (Door doorItem : doors) if (doorItem.getBounds().intersects(boundsY)) canMoveY = false;
            if (canMoveY) this.y = d;
            if (this.isReloading && System.currentTimeMillis() - this.reloadStartTime > 1500L) {
                this.isReloading = false;
                Weapon aw = this.activeSlot == 1 ? this.weapon1 : this.weapon2;
                int needed = aw.maxAmmo - aw.ammo;
                if (needed > 0 && ZombieGame.this.globalAmmo[aw.ammoType] > 0) {
                    int take = Math.min(needed, ZombieGame.this.globalAmmo[aw.ammoType]);
                    ZombieGame.this.globalAmmo[aw.ammoType] -= take;
                    aw.ammo += take;
                }
            }
        }

        void takeDamage() {
            if (System.currentTimeMillis() - this.lastDamageTime > 500L) {
                this.health -= 10;
                this.lastDamageTime = System.currentTimeMillis();
            }
        }

        Rectangle getBounds() {
            return new Rectangle((int)this.x - 15, (int)this.y - 15, 30, 30);
        }

        void draw(Graphics2D graphics2D) {
            boolean bl;
            Weapon activeWeapon = this.activeSlot == 1 ? this.weapon1 : this.weapon2;
              if (activeWeapon == null) return;
            int frame = 0; // 0=Idle
            if (ZombieGame.this.isAiming) {
                frame = 1;
                if (ZombieGame.this.isMousePressed) frame = 2;
            }
            if (System.currentTimeMillis() - this.lastShotTime < 150L) frame = 2;
            if (this.isReloading) {
                long rElapsed = System.currentTimeMillis() - this.reloadStartTime;
                frame = rElapsed < 1000 ? 3 : 4;
            } else if (activeWeapon.ammo <= 0) frame = 5;
            
            BufferedImage bufferedImage = imgPlayerIdle;
            if (ZombieGame.weaponFrames.containsKey(activeWeapon.typeName)) {
                java.awt.image.BufferedImage[] frames = ZombieGame.weaponFrames.get(activeWeapon.typeName);
                if (frames[frame] != null) bufferedImage = frames[frame];
            }
            boolean bl3 = bl = System.currentTimeMillis() - this.lastDamageTime < 500L && System.currentTimeMillis() % 200L < 100L;
            if (bufferedImage != null) {
                double dx = mouseX - this.x;
                double dy = mouseY - this.y;
                double d = Math.atan2(dy, dx);
                if (this.isMoving && !ZombieGame.this.isAiming) {
                    d += Math.sin(this.wobbleTime * 0.015) * 0.2;
                }
                AffineTransform affineTransform = graphics2D.getTransform();
                graphics2D.translate(this.x, this.y);
                graphics2D.rotate(d);
                if (bl) {
                    graphics2D.setColor(new Color(255, 0, 0, 100));
                    graphics2D.fillOval(-25, -25, 50, 50);
                }
                int n = bufferedImage.getWidth();
                int n2 = bufferedImage.getHeight();
                double d2 = Math.min(80.0 / (double)n, 80.0 / (double)n2);
                if (this.isMoving && !ZombieGame.this.isAiming) {
                    d2 *= 1.0 + Math.abs(Math.sin(this.wobbleTime * 0.015)) * 0.05;
                }
                int n3 = (int)((double)n * d2);
                int n4 = (int)((double)n2 * d2);
                graphics2D.drawImage(bufferedImage, -n3 / 2, -n4 / 2, n3, n4, null);
                graphics2D.setTransform(affineTransform);
            } else {
                if (bl) {
                    graphics2D.setColor(Color.RED);
                } else {
                    graphics2D.setColor(Color.BLUE);
                }
                graphics2D.fillOval((int)this.x - 20, (int)this.y - 20, 40, 40);
            }
        }
    }

        class Door {
        int x, y, width, height;
        boolean isHorizontal, isOpen = false;
        Door(int x, int y, int w, int h, boolean horiz) {
            this.x=x; this.y=y; width=w; height=h; isHorizontal=horiz;
        }
        java.awt.Rectangle getBounds() { return isOpen ? new java.awt.Rectangle(0,0,0,0) : new java.awt.Rectangle(x, y, width, height); }
        void draw(java.awt.Graphics2D g) {
            if (isOpen) return;
            java.awt.image.BufferedImage img = isHorizontal ? imgDoorT[0] : imgDoorT[1];
            if (img != null) g.drawImage(img, x, y, width, height, null);
            else { g.setColor(new java.awt.Color(139, 69, 19)); g.fillRect(x, y, width, height); }
        }
        void toggle() { isOpen = !isOpen; }
    }
    class Core {
        double x = 400.0;
        double y = 300.0;
        int maxHealth = 1500;
        int health = 1500;
        long lastDamageTime = 0L;

        Core(ZombieGame zombieGame) {
        }

        void takeDamage(int n) {
            if (System.currentTimeMillis() - this.lastDamageTime > 500L) {
                this.health -= n;
                this.lastDamageTime = System.currentTimeMillis();
            }
        }

        Rectangle getBounds() {
            return new Rectangle((int)this.x - 20, (int)this.y - 20, 40, 40);
        }

        void draw(Graphics2D graphics2D) {
            boolean bl;
            boolean bl2 = bl = System.currentTimeMillis() - this.lastDamageTime < 500L && System.currentTimeMillis() % 200L < 100L;
            if (imgSupplies != null) {
                if (bl) {
                    graphics2D.setComposite(AlphaComposite.getInstance(3, 0.5f));
                    graphics2D.setColor(Color.RED);
                    graphics2D.fillOval((int)this.x - 25, (int)this.y - 25, 50, 50);
                    graphics2D.setComposite(AlphaComposite.getInstance(3, 1.0f));
                }
                graphics2D.drawImage(imgSupplies, (int)this.x - 25, (int)this.y - 25, 50, 50, null);
            } else {
                if (bl) {
                    graphics2D.setColor(Color.RED);
                } else {
                    graphics2D.setColor(Color.CYAN);
                }
                graphics2D.fillOval((int)this.x - 20, (int)this.y - 20, 40, 40);
            }
        }
    }

    class Workbench {
        double x;
        double y;

        Workbench(ZombieGame zombieGame, double d, double d2) {
            this.x = d;
            this.y = d2;
        }

        void draw(Graphics2D graphics2D) {
            if (imgWorkbench != null) {
                graphics2D.drawImage(imgWorkbench, (int)this.x, (int)this.y, 60, 60, null);
            } else {
                graphics2D.setColor(Color.GRAY);
                graphics2D.fillRect((int)this.x, (int)this.y, 60, 60);
            }
        }
    }

    class SolidWall {
        int x;
        int y;
        int width;
        int height;

        SolidWall(ZombieGame zombieGame, int n, int n2, int n3, int n4) {
            this.x = n;
            this.y = n2;
            this.width = n3;
            this.height = n4;
        }

        Rectangle getBounds() {
            return new Rectangle(this.x, this.y, this.width, this.height);
        }

        void draw(Graphics2D graphics2D) {
            BufferedImage bufferedImage;
            boolean bl = this.width > this.height;
            BufferedImage bufferedImage2 = bufferedImage = bl ? imgWallT[4] : imgWallT[5];
            if (bufferedImage != null) {
                graphics2D.drawImage(bufferedImage, this.x, this.y, this.width, this.height, null);
            } else {
                graphics2D.setColor(Color.DARK_GRAY);
                graphics2D.fillRect(this.x, this.y, this.width, this.height);
            }
        }
    }

    class Window {
        int x;
        int y;
        int width;
        int height;
        int health;
        int maxHealth;
        double centerX;
        double centerY;
        boolean isHorizontal;

        Window(ZombieGame zombieGame, int n, int n2, int n3, int n4, boolean bl) {
            this.x = n;
            this.y = n2;
            this.width = n3;
            this.height = n4;
            this.isHorizontal = bl;
            this.centerX = (double)n + (double)n3 / 2.0;
            this.centerY = (double)n2 + (double)n4 / 2.0;
            this.maxHealth = 100;
            this.health = 100;
        }

        Rectangle getBounds() {
            return new Rectangle(this.x, this.y, this.width, this.height);
        }

        void draw(Graphics2D graphics2D) {
            graphics2D.setColor(new Color(0, 0, 0, 100));
            graphics2D.fillRect(this.x, this.y, this.width, this.height);
            if (this.health > 0) {
                BufferedImage bufferedImage;
                BufferedImage bufferedImage2 = bufferedImage = this.isHorizontal ? imgWindowT[4] : imgWindowT[5];
                if (bufferedImage != null) {
                    graphics2D.drawImage(bufferedImage, this.x, this.y, this.width, this.height, null);
                } else {
                    graphics2D.setColor(new Color(139, 69, 19));
                    graphics2D.fillRect(this.x, this.y, this.width, this.height);
                }
                graphics2D.setColor(Color.GREEN);
                if (this.isHorizontal) {
                    int n = (int)((double)this.width * ((double)this.health / (double)this.maxHealth));
                    graphics2D.fillRect(this.x + (this.width - n) / 2, this.y + this.height / 2 - 2, n, 4);
                } else {
                    int n = (int)((double)this.height * ((double)this.health / (double)this.maxHealth));
                    graphics2D.fillRect(this.x + this.width / 2 - 2, this.y + (this.height - n) / 2, 4, n);
                }
            }
        }
    }

    class Bullet {
        double startX;
        double startY;
        double x;
        double y;
        double velX;
        double velY;
        int damage;
        double speed;
        String type;
        double maxRange;
        long spawnTime;
        String weaponType;
        double angle;
        boolean isExploding = false;
        long explodeTime = 0L;

        Bullet(ZombieGame zombieGame, double d, double d2, double d3, int n, double d4, String string, double d5, String weaponType, double angle) {
            this.startX = d;
            this.startY = d2;
            this.x = d;
            this.y = d2;
            this.damage = n;
            this.speed = d4;
            this.velX = Math.cos(d3) * d4;
            this.velY = Math.sin(d3) * d4;
            this.type = string;
            this.maxRange = d5;
            this.spawnTime = System.currentTimeMillis();
            this.weaponType = weaponType;
            this.angle = angle;
        }

        void update() {
            if (this.isExploding) return;
            if (this.type.equals("fire")) {
                double d = Math.hypot(this.x - this.startX, this.y - this.startY);
                if (d < this.maxRange) {
                    this.x += this.velX * 0.016;
                    this.y += this.velY * 0.016;
                }
            } else {
                this.x += this.velX * 0.016;
                this.y += this.velY * 0.016;
            }
        }

        boolean isExpired() {
            if (this.isExploding) return System.currentTimeMillis() - this.explodeTime > 300L;
            if (this.type.equals("fire")) {
                return System.currentTimeMillis() - this.spawnTime > 3000L;
            }
            return Math.hypot(this.x - this.startX, this.y - this.startY) > this.maxRange;
        }

        Rectangle getBounds() {
            if (this.type.equals("fire")) {
                return new Rectangle((int)this.x - 20, (int)this.y - 20, 40, 40);
            }
            return new Rectangle((int)this.x - 5, (int)this.y - 5, 10, 10);
        }

        void draw(Graphics2D graphics2D) {
            if (this.type.equals("fire")) {
                graphics2D.setColor(new Color(255, 100, 0, 150));
                graphics2D.fillOval((int)this.x - 20, (int)this.y - 20, 40, 40);
                graphics2D.setColor(new Color(255, 200, 0, 200));
                graphics2D.fillOval((int)this.x - 10, (int)this.y - 10, 20, 20);
            } else {
                graphics2D.setColor(Color.YELLOW);
                graphics2D.fillOval((int)this.x - 5, (int)this.y - 5, 10, 10);
            }
        }
    }

    class ResourceNode {
        int x, y, type, model, health = 5;
        long lastWindFrame = 0;
        int windFrame = 0;
        ResourceNode(int x, int y, int t, int m) { this.x = x; this.y = y; this.type = t; this.model = m; }
        java.awt.Rectangle getBounds() {
            if (type == 0) return new java.awt.Rectangle(x + 20, y + 80, 40, 40); // Tree trunk
            else return new java.awt.Rectangle(x, y, 60, 60); // Rock
        }
        void update() {
            if (type == 0 && System.currentTimeMillis() - lastWindFrame > 250) {
                windFrame = (windFrame + 1) % 4;
                lastWindFrame = System.currentTimeMillis();
            }
        }
        void draw(java.awt.Graphics2D g) {
            if (type == 0) {
                if (imgTrees[model][windFrame] != null) {
                    g.drawImage(imgTrees[model][windFrame], x - 40, y - 40, 160, 160, null);
                }
            } else {
                if (imgRocks[model] != null) {
                    g.drawImage(imgRocks[model], x, y, 60, 60, null);
                }
            }
        }
    }
    class LootChest {
        int x, y, model, state = 0;
        long openTime = 0;
        LootChest(int x, int y, int m) { this.x = x; this.y = y; this.model = m; }
        void draw(java.awt.Graphics2D g) {
            if (imgChests[model][state] != null) {
                g.drawImage(imgChests[model][state], x, y, 60, 60, null);
            }
        }
        void update() {
            if (state == 1 && System.currentTimeMillis() - openTime > 200) {
                state = 2; // Fully open
                // Drop items!
                if (model == 0) {
                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 3, 5)); // Wood
                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 4, 5)); // Nails
                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 14, 2)); // Food
                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 8, 20)); // Ammo T1
                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 9, 30)); // Ammo T2
                } else if (model == 1) {
                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 5, 5)); // Iron
                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 6, 5)); // Screws
                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 15, 1)); // Medkit
                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 10, 40)); // Ammo T3
                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 11, 10)); // Ammo T4
                } else if (model == 2) {
                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 7, 5)); // Lead
                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 16, 2)); // Crystals
                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 17, 1)); // Fuel
                    ZombieGame.this.drops.add(new Drop(ZombieGame.this, x+30, y+30, 12, 5)); // Ammo T5
                }
            }
        }
    }
    class Enemy {
        double x;
        double y;
        double speed;
        int health;
        int expValue;
        int size;
        String type;
        Color color;
        long lastFireDamageTime = 0L;
        double angle = 0.0;
        int frameCount = 0;
        long lastFrameChange = 0L;
        String state = "walk";
        boolean dead = false;
        long stateStartTime = 0L;
        long attackCooldown = 0L;
        long deathTime = 0L;

        Enemy(double d, double d2, String string, int n) {
            this.x = d;
            this.y = d2;
            this.type = string;
            int n2 = 1 + n / 3;
            if (string.equals("basic")) {
                this.health = 10 * n2;
                this.speed = 50.0;
                this.expValue = 1;
                this.color = new Color(0, 100, 0);
                this.size = 30;
            } else if (string.equals("runner")) {
                this.health = 25 * n2;
                this.speed = 100.0;
                this.expValue = 2;
                this.color = new Color(200, 50, 50);
                this.size = 25;
            } else if (string.equals("boss")) {
                this.health = 100 * n2;
                this.speed = 35.0;
                this.expValue = 10;
                this.color = new Color(100, 100, 100);
                this.size = 60;
            } else if (string.equals("superboss")) {
                this.health = 500 * n2;
                this.speed = 25.0;
                this.expValue = 50;
                this.color = new Color(138, 43, 226);
                this.size = 80;
            }
        }

        void changeState(String string) {
            if (this.state.equals("death")) {
                return;
            }
            if (!this.state.equals(string)) {
                this.state = string;
                this.stateStartTime = System.currentTimeMillis();
                this.lastFrameChange = System.currentTimeMillis();
                if (string.equals("idle")) {
                    this.frameCount = 0;
                } else if (string.equals("walk")) {
                    this.frameCount = 1;
                } else if (string.equals("attack")) {
                    this.frameCount = 3;
                } else if (string.equals("death")) {
                    this.frameCount = 4;
                    this.deathTime = System.currentTimeMillis();
                }
            }
        }

        void update(Core core, ArrayList<Window> arrayList, ArrayList<SolidWall> arrayList2, Player player) {
            boolean bl;
            if (this.health <= 0 && !this.dead) {
                this.changeState("death");
                this.dead = true;
            }
            if (this.dead) {
                this.frameCount = 4;
                return;
            }
            double d = 0.0;
            double d2 = 0.0;
            boolean bl2 = this.x > 150.0 && this.x < 650.0 && this.y > 150.0 && this.y < 450.0;
            double d3 = Math.hypot(player.x - this.x, player.y - this.y);
            boolean bl3 = player.x <= 150.0 || player.x >= 650.0 || player.y <= 150.0 || player.y >= 450.0;
            boolean bl4 = System.currentTimeMillis() - lastPlayerShootTime < 2000L && d3 < 1200.0;
            boolean bl5 = bl = this.type.equals("runner") || d3 < 400.0 && bl3 || bl4;
            if (bl || bl2) {
                d = player.x;
                d2 = player.y;
            } else {
                Window window2 = null;
                double d4 = 999999.0;
                for (Window objW : arrayList) {
                    double window = Math.hypot(objW.centerX - this.x, objW.centerY - this.y);
                    if (!(window < d4)) continue;
                    d4 = window;
                    window2 = objW;
                }
                if (window2 != null) {
                    d = window2.centerX;
                    d2 = window2.centerY;
                } else {
                    d = ZombieGame.this.houseCore.x;
                    d2 = ZombieGame.this.houseCore.y;
                }
            }
            this.angle = Math.atan2(d2 - this.y, d - this.x);
            double d6 = Math.hypot(d - this.x, d2 - this.y);
            if (d6 < (double)(this.size / 2 + 20) && System.currentTimeMillis() - this.attackCooldown > 1000L) {
                this.changeState("attack");
                this.attackCooldown = System.currentTimeMillis();
            }
            if (this.state.equals("attack")) {
                this.frameCount = 3;
                if (System.currentTimeMillis() - this.stateStartTime > 500L) {
                    this.changeState("walk");
                }
                return;
            }
            double d4 = this.speed;
            for (Window window : arrayList) {
                if (!window.getBounds().contains(this.x, this.y)) continue;
                if (window.health > 0) {
                    d4 = 0.0;
                    window.health = window.health - (this.type.equals("boss") ? 3 : (this.type.equals("superboss") ? 10 : 1));
                    this.changeState("attack");
                    break;
                }
                d4 = this.speed * 0.3;
                break;
            }
            if (d4 > 0.0 && !this.state.equals("attack")) {
                double vx = Math.cos(this.angle) * d4 * 0.016;
                double vy = Math.sin(this.angle) * d4 * 0.016;
                
                java.awt.Rectangle boundsX = new java.awt.Rectangle((int)(this.x + vx) - this.size/2, (int)this.y - this.size/2, this.size, this.size);
                boolean canX = true;
                for (SolidWall w : arrayList2) if (w.getBounds().intersects(boundsX)) canX = false;
                for (Door dr : ZombieGame.this.doors) if (dr.getBounds().intersects(boundsX)) {
                    canX = false;
                    this.changeState("attack");
                }
                if (canX) this.x += vx;

                java.awt.Rectangle boundsY = new java.awt.Rectangle((int)this.x - this.size/2, (int)(this.y + vy) - this.size/2, this.size, this.size);
                boolean canY = true;
                for (SolidWall w : arrayList2) if (w.getBounds().intersects(boundsY)) canY = false;
                for (Door dr : ZombieGame.this.doors) if (dr.getBounds().intersects(boundsY)) {
                    canY = false;
                    this.changeState("attack");
                }
                if (canY) this.y += vy;

                if (canX || canY) {
                    this.changeState("walk");
                    if (System.currentTimeMillis() - this.lastFrameChange > 150L) {
                        this.frameCount = this.frameCount == 1 ? 2 : 1;
                        this.lastFrameChange = System.currentTimeMillis();
                    }
                }
            } else if (d4 == 0.0 && !this.state.equals("attack")) {
                this.changeState("idle");
            }
        }

        Rectangle getBounds() {
            return new Rectangle((int)this.x - this.size / 2, (int)this.y - this.size / 2, this.size, this.size);
        }

        void draw(Graphics2D graphics2D) {
            BufferedImage bufferedImage = null;
            if (this.type.equals("basic") && imgBasic[this.frameCount] != null) {
                bufferedImage = imgBasic[this.frameCount];
            } else if (this.type.equals("runner") && imgRunner[this.frameCount] != null) {
                bufferedImage = imgRunner[this.frameCount];
            } else if (this.type.equals("boss") && imgBoss[this.frameCount] != null) {
                bufferedImage = imgBoss[this.frameCount];
            } else if (this.type.equals("superboss") && imgSuperboss[this.frameCount] != null) {
                bufferedImage = imgSuperboss[this.frameCount];
            }
            if (bufferedImage != null) {
                AffineTransform affineTransform = graphics2D.getTransform();
                graphics2D.translate(this.x, this.y);
                graphics2D.rotate(this.angle);
                int n = bufferedImage.getWidth();
                int n2 = bufferedImage.getHeight();
                double d = Math.min((double)this.size * 1.5 / (double)n, (double)this.size * 1.5 / (double)n2);
                int n3 = (int)((double)n * d);
                int n4 = (int)((double)n2 * d);
                graphics2D.drawImage(bufferedImage, -n3 / 2, -n4 / 2, n3, n4, null);
                graphics2D.setTransform(affineTransform);
            } else {
                graphics2D.setColor(this.color);
                graphics2D.fillRect((int)this.x - this.size / 2, (int)this.y - this.size / 2, this.size, this.size);
                graphics2D.setColor(Color.BLACK);
                graphics2D.drawRect((int)this.x - this.size / 2, (int)this.y - this.size / 2, this.size, this.size);
            }
        }
    }

    class DamageText {
        double x;
        double y;
        int damage;
        int life = 40;

        DamageText(ZombieGame zombieGame, double d, double d2, int n) {
            this.x = d;
            this.y = d2;
            this.damage = n;
        }

        void update() {
            this.y -= 0.48;
            --this.life;
        }

        void draw(Graphics2D graphics2D) {
            graphics2D.setColor(new Color(255, 255, 255, Math.min(255, this.life * 6)));
            graphics2D.setFont(new Font("Arial", 1, 14));
            graphics2D.drawString(String.valueOf(this.damage), (int)this.x - 10, (int)this.y);
        }
    }

    class Drop {
        double x;
        double y;
        int type;
        int value;
        Weapon weaponDrop = null;

        Drop(ZombieGame zombieGame, double d, double d2, int n, int n2) {
            this.x = d;
            this.y = d2;
            this.type = n;
            this.value = n2;
        }
        Drop(ZombieGame zombieGame, double d, double d2, Weapon w) {
            this.x = d;
            this.y = d2;
            this.type = 18;
            this.value = 1;
            this.weaponDrop = w;
        }

        void draw(Graphics2D graphics2D) {
            if (this.type == 0 && imgItemExp != null) {
                graphics2D.drawImage((Image)imgItemExp, (int)this.x - 8, (int)this.y - 8, null);
            } else if (this.type == 1 && imgItemCoin != null) {
                graphics2D.drawImage((Image)imgItemCoin, (int)this.x - 8, (int)this.y - 8, null);
            } else if (this.type == 2 && imgItemMaterial != null) {
                graphics2D.drawImage((Image)imgItemMaterial, (int)this.x - 8, (int)this.y - 8, null);
            } else if (this.type == 0) {
                graphics2D.setColor(this.value > 4 ? Color.BLUE : Color.GREEN);
                graphics2D.fillPolygon(new int[]{(int)this.x, (int)this.x + 6, (int)this.x, (int)this.x - 6}, new int[]{(int)this.y - 6, (int)this.y, (int)this.y + 6, (int)this.y}, 4);
            } else if (this.type == 1) {
                graphics2D.setColor(Color.YELLOW);
                graphics2D.fillOval((int)this.x - 4, (int)this.y - 4, 8, 8);
                graphics2D.setColor(new Color(200, 150, 0));
                graphics2D.drawOval((int)this.x - 4, (int)this.y - 4, 8, 8);
            } else if (this.type == 18) {
                graphics2D.setColor(this.weaponDrop.color);
                graphics2D.fillRect((int)this.x - 6, (int)this.y - 6, 12, 12);
                graphics2D.setColor(java.awt.Color.WHITE);
                graphics2D.drawString(this.weaponDrop.name, (int)this.x - 15, (int)this.y - 15);
            } else if (this.type == 2) {
                graphics2D.setColor(Color.LIGHT_GRAY);
                graphics2D.fillRect((int)this.x - 4, (int)this.y - 4, 8, 8);
            } else if (this.type >= 3 && this.type <= 7) {
                graphics2D.setColor(new java.awt.Color(139, 69, 19)); // Brown for crafting
                graphics2D.fillRect((int)this.x - 4, (int)this.y - 4, 8, 8);
            } else if (this.type >= 8 && this.type <= 13) {
                graphics2D.setColor(new java.awt.Color(255, 0, 0)); // Red for Ammo
                graphics2D.fillRect((int)this.x - 4, (int)this.y - 4, 8, 4);
            }
        }
    }
}

