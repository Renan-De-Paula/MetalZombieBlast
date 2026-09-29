import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class ZombieGame extends JPanel implements ActionListener, KeyListener {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private Timer timer;
    private Player player;
    private ArrayList<Enemy> enemies;
    private ArrayList<Bullet> bullets;
    private ArrayList<Particle> particles;

    private boolean[] keys = new boolean[256];

    private BufferedImage bgImage;
    private BufferedImage playerImg;
    private BufferedImage basicImg;
    private BufferedImage runnerImg;
    private BufferedImage bruteImg;

    private int cameraX = 0;
    private int score = 0;
    private int coins = 0;
    private int distance = 0;
    private boolean bossSpawned = false;
    private Random random = new Random();

    public ZombieGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        loadImages();

        player = new Player(100, 400);
        enemies = new ArrayList<>();
        bullets = new ArrayList<>();
        particles = new ArrayList<>();

        timer = new Timer(16, this); // ~60 FPS
        timer.start();
    }

    private void loadImages() {
        try {
            bgImage = ImageIO.read(getClass().getResource("/assets/war_bg.jpg"));
            playerImg = ImageIO.read(getClass().getResource("/assets/player.png"));
            basicImg = ImageIO.read(getClass().getResource("/assets/zombie_basic.png"));
            runnerImg = ImageIO.read(getClass().getResource("/assets/zombie_runner.png"));
            bruteImg = ImageIO.read(getClass().getResource("/assets/zombie_brute.png"));
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagens do classpath.");
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }

    private void update() {
        player.update(keys);

        // Atualiza a câmera para seguir o jogador (se passar do meio da tela)
        if (player.x > cameraX + WIDTH / 2) {
            cameraX = (int) player.x - WIDTH / 2;
        }

        // Calcula a distância
        distance = Math.max(0, (int) player.x / 10);

        // Spawna inimigos conforme o jogador avança
        if (random.nextInt(100) < 2) { // chance a cada frame
            spawnEnemy();
        }

        // Spawn Boss a 1000m
        if (distance >= 1000 && !bossSpawned) {
            bossSpawned = true;
            enemies.add(new Enemy(cameraX + WIDTH + 200, 400, "boss"));
        }

        // Atualiza balas
        Iterator<Bullet> bit = bullets.iterator();
        while (bit.hasNext()) {
            Bullet b = bit.next();
            b.update();
            if (b.x < cameraX || b.x > cameraX + WIDTH) {
                bit.remove();
            }
        }

        // Atualiza inimigos
        Iterator<Enemy> eit = enemies.iterator();
        while (eit.hasNext()) {
            Enemy en = eit.next();
            en.update(player);

            // Verifica colisão do inimigo com o jogador
            if (en.getBounds().intersects(player.getBounds())) {
                player.takeDamage();
            }

            // Verifica colisão da bala com o inimigo
            boolean hit = false;
            Iterator<Bullet> bIt2 = bullets.iterator();
            while (bIt2.hasNext()) {
                Bullet b = bIt2.next();
                if (en.getBounds().intersects(b.getBounds())) {
                    en.health -= b.damage;
                    createParticles(b.x, b.y, Color.YELLOW, 5);
                    bIt2.remove();
                    hit = true;
                    break;
                }
            }

            if (en.health <= 0) {
                coins += en.coinReward;
                createParticles(en.x, en.y, Color.RED, 20);
                eit.remove();
            } else if (en.x < cameraX - 200) {
                eit.remove();
            }
        }

        // Atualiza particulas
        Iterator<Particle> pit = particles.iterator();
        while (pit.hasNext()) {
            Particle p = pit.next();
            p.update();
            if (p.life <= 0) {
                pit.remove();
            }
        }
    }

    private void spawnEnemy() {
        int spawnX = cameraX + WIDTH + 100;
        int spawnY = 400; // Altura do chão fixo
        String type = "basic";
        int r = random.nextInt(100);
        if (r < 10) type = "miniboss";
        else if (r < 40) type = "runner";
        else if (r < 70) type = "brute";
        
        enemies.add(new Enemy(spawnX, spawnY, type));
    }

    private void createParticles(double x, double y, Color c, int count) {
        for (int i = 0; i < count; i++) {
            particles.add(new Particle(x, y, c));
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Desenha Fundo
        if (bgImage != null) {
            int bgX = -(cameraX / 3) % bgImage.getWidth();
            g2d.drawImage(bgImage, bgX, 0, null);
            g2d.drawImage(bgImage, bgX + bgImage.getWidth(), 0, null);
        }

        // Translação da Câmera
        g2d.translate(-cameraX, 0);

        // Chão
        g2d.setColor(new Color(58, 95, 11)); // Verde escuro
        g2d.fillRect(cameraX, 500, WIDTH, 100);
        g2d.setColor(new Color(76, 175, 80));
        g2d.fillRect(cameraX, 500, WIDTH, 10);

        // Desenha Jogador
        player.draw(g2d);

        // Desenha Balas
        for (Bullet b : bullets) {
            b.draw(g2d);
        }

        // Desenha Inimigos
        for (Enemy en : enemies) {
            en.draw(g2d);
        }

        // Desenha Partículas
        for (Particle p : particles) {
            p.draw(g2d);
        }

        g2d.translate(cameraX, 0);

        // UI Fixa
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Courier New", Font.BOLD, 20));
        g2d.drawString("Distância: " + distance + "m", 20, 30);
        g2d.setColor(Color.YELLOW);
        g2d.drawString("Moedas: " + coins, 20, 60);
        g2d.setColor(Color.RED);
        g2d.drawString("HP: " + player.health, 20, 90);

        if (distance >= 1000 && bossSpawned) {
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Courier New", Font.BOLD, 40));
            g2d.drawString("ALERTA DE BOSS!", 200, 100);
        }
    }

    // --- Inputs ---
    @Override public void keyPressed(KeyEvent e) { 
        if (e.getKeyCode() < 256) keys[e.getKeyCode()] = true; 
    }
    @Override public void keyReleased(KeyEvent e) { 
        if (e.getKeyCode() < 256) keys[e.getKeyCode()] = false; 
    }
    @Override public void keyTyped(KeyEvent e) {}

    // --- Classes Internas ---

    class Player {
        double x, y;
        double velX, velY;
        int health = 5;
        boolean facingLeft = false;
        long lastDamageTime = 0;
        
        // Arma: Pistola
        long lastShotTime = 0;
        int fireRate = 400; // 400ms de intervalo (Pistola)

        Player(double x, double y) {
            this.x = x;
            this.y = y;
        }

        void update(boolean[] keys) {
            if (keys[KeyEvent.VK_LEFT]) { velX = -200; facingLeft = true; }
            else if (keys[KeyEvent.VK_RIGHT]) { velX = 200; facingLeft = false; }
            else { velX = 0; }

            if (keys[KeyEvent.VK_UP] && y >= 400) {
                velY = -400; // Pulo
            }
            
            if (keys[KeyEvent.VK_Z]) {
                shoot();
            }

            // Gravidade
            velY += 15;
            
            x += velX * 0.016;
            y += velY * 0.016;

            // Chão
            if (y > 400) {
                y = 400;
                velY = 0;
            }

            // Limite esquerdo da tela
            if (x < cameraX + 20) x = cameraX + 20;
        }

        void shoot() {
            if (System.currentTimeMillis() - lastShotTime > fireRate) {
                lastShotTime = System.currentTimeMillis();
                int dir = facingLeft ? -1 : 1;
                bullets.add(new Bullet(x + (facingLeft ? -10 : 80), y + 40, dir * 800));
            }
        }

        void takeDamage() {
            if (System.currentTimeMillis() - lastDamageTime > 1000) {
                health--;
                lastDamageTime = System.currentTimeMillis();
                if (health <= 0) {
                    // Respawn
                    x = cameraX + 100;
                    y = 400;
                    health = 5;
                }
            }
        }

        Rectangle getBounds() {
            return new Rectangle((int)x + 20, (int)y + 20, 60, 80);
        }

        void draw(Graphics2D g) {
            boolean flashing = (System.currentTimeMillis() - lastDamageTime < 1000) && (System.currentTimeMillis() % 200 < 100);
            if (flashing) return;

            if (playerImg != null) {
                int drawWidth = 100;
                int drawHeight = 100;
                if (facingLeft) {
                    g.drawImage(playerImg, (int)x + drawWidth, (int)y, -drawWidth, drawHeight, null);
                } else {
                    g.drawImage(playerImg, (int)x, (int)y, drawWidth, drawHeight, null);
                }
            } else {
                g.setColor(Color.GREEN);
                g.fillRect((int)x, (int)y, 60, 80);
            }
        }
    }

    class Enemy {
        double x, y;
        int health;
        double speed;
        String type;
        int coinReward;
        int width = 80, height = 100;

        Enemy(double x, double y, String type) {
            this.x = x;
            this.y = y;
            this.type = type;

            if (type.equals("basic")) {
                health = 30; speed = 60; coinReward = 10;
            } else if (type.equals("runner")) {
                health = 15; speed = 150; coinReward = 10; width = 70; height = 90;
            } else if (type.equals("brute")) {
                health = 100; speed = 30; coinReward = 10; width = 100; height = 120;
            } else if (type.equals("miniboss")) {
                health = 300; speed = 40; coinReward = 25; width = 120; height = 150;
            } else if (type.equals("boss")) {
                health = 1500; speed = 45; coinReward = 100; width = 200; height = 250;
            }
            
            this.y = 500 - height;
        }

        void update(Player p) {
            int dir = p.x < x ? -1 : 1;
            x += speed * dir * 0.016;
        }

        Rectangle getBounds() {
            return new Rectangle((int)x, (int)y, width, height);
        }

        void draw(Graphics2D g) {
            BufferedImage img = basicImg;
            if (type.equals("runner")) img = runnerImg;
            else if (type.equals("brute") || type.equals("miniboss") || type.equals("boss")) img = bruteImg;

            boolean facingLeft = player.x < x;

            if (img != null) {
                if (facingLeft) {
                    g.drawImage(img, (int)x + width, (int)y, -width, height, null);
                } else {
                    g.drawImage(img, (int)x, (int)y, width, height, null);
                }
            } else {
                g.setColor(Color.RED);
                g.fillRect((int)x, (int)y, width, height);
            }
        }
    }

    class Bullet {
        double x, y;
        double velX;
        int damage = 15;

        Bullet(double x, double y, double velX) {
            this.x = x;
            this.y = y;
            this.velX = velX;
        }

        void update() {
            x += velX * 0.016;
        }

        Rectangle getBounds() {
            return new Rectangle((int)x, (int)y, 16, 6);
        }

        void draw(Graphics2D g) {
            g.setColor(Color.YELLOW);
            g.fillRect((int)x, (int)y, 16, 6);
        }
    }

    class Particle {
        double x, y, velX, velY;
        int life;
        Color c;

        Particle(double x, double y, Color c) {
            this.x = x; this.y = y; this.c = c;
            this.velX = (random.nextDouble() - 0.5) * 300;
            this.velY = (random.nextDouble() - 0.5) * 300;
            this.life = 20 + random.nextInt(20);
        }

        void update() {
            x += velX * 0.016;
            y += velY * 0.016;
            life--;
        }

        void draw(Graphics2D g) {
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.min(255, life * 10)));
            g.fillRect((int)x, (int)y, 6, 6);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Zombie Game (Java Edition)");
        ZombieGame game = new ZombieGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
