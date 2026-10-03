package br.com.ultimoabrigo.assets;

import br.com.ultimoabrigo.core.GameConfig;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * ProceduralSprites - Renderizador de alta fidelidade visual estilo Metal Slug para Elias Rocha,
 * Zumbis Caminhantes (Walker) e Corredores (Runner).
 */
public class ProceduralSprites {

    // Paleta de Elias Rocha
    private static final Color SKIN_BASE = new Color(228, 185, 150);
    private static final Color SKIN_SHADOW = new Color(192, 145, 110);
    private static final Color BEARD_COLOR = new Color(42, 28, 18);
    private static final Color SHIRT_GREEN = new Color(74, 90, 52);
    private static final Color VEST_COLOR = new Color(92, 78, 55);
    private static final Color VEST_POUCH = new Color(60, 50, 35);
    private static final Color PANTS_DARK = new Color(48, 52, 58);
    private static final Color KNEE_PAD = new Color(25, 25, 25);
    private static final Color BOOTS_COLOR = new Color(30, 26, 24);
    private static final Color GLOVE_COLOR = new Color(35, 35, 35);
    private static final Color WRENCH_HANDLE = new Color(195, 35, 35);
    private static final Color WRENCH_STEEL = new Color(185, 190, 200);

    // Paleta dos Zumbis
    private static final Color ZOMBIE_WALKER_SKIN = new Color(112, 138, 115);
    private static final Color ZOMBIE_WALKER_SHIRT = new Color(70, 60, 55);
    private static final Color ZOMBIE_WALKER_PANTS = new Color(45, 50, 60);

    private static final Color ZOMBIE_RUNNER_SKIN = new Color(145, 120, 135);
    private static final Color ZOMBIE_RUNNER_RAGS = new Color(85, 35, 35);
    private static final Color CLAW_COLOR = new Color(25, 20, 25);

    // ==========================================
    // RENDERIZADOR DE ELIAS ROCHA
    // ==========================================
    public static void renderElias(
            Graphics2D g,
            int x, int y,
            int facing,
            boolean crouched,
            boolean inAir,
            boolean isMoving,
            int animFrame,
            boolean isAimingUp,
            boolean isShooting,
            boolean isMelee,
            int meleeProgress,
            boolean isHurt,
            int currentWeaponIndex
    ) {
        Graphics2D g2 = (Graphics2D) g.create();

        if (isHurt) {
            g2.setColor(new Color(255, 60, 60, 200));
        }

        if (facing == -1) {
            g2.translate(x + GameConfig.PLAYER_WIDTH, y);
            g2.scale(-1, 1);
        } else {
            g2.translate(x, y);
        }

        int baseY = crouched ? 28 : 0;

        renderLegs(g2, crouched, inAir, isMoving, animFrame, baseY);
        renderTorso(g2, crouched, baseY);
        renderHead(g2, crouched, isAimingUp, baseY);

        if (isMelee) {
            renderMeleeWrench(g2, meleeProgress, baseY);
        } else {
            renderArmsAndWeapon(g2, crouched, isAimingUp, isShooting, currentWeaponIndex, baseY);
        }

        g2.dispose();
    }

    private static void renderLegs(Graphics2D g, boolean crouched, boolean inAir, boolean isMoving, int animFrame, int baseY) {
        int legY = baseY + 48;

        if (crouched) {
            g.setColor(PANTS_DARK);
            g.fillRect(8, legY, 26, 14);
            g.setColor(KNEE_PAD);
            g.fillRoundRect(26, legY + 2, 10, 10, 3, 3);
            g.setColor(BOOTS_COLOR);
            g.fillRect(6, legY + 12, 18, 10);
            g.fillRect(24, legY + 12, 18, 10);
            return;
        }

        if (inAir) {
            g.setColor(PANTS_DARK);
            g.fillRect(10, legY, 12, 16);
            g.fillRect(26, legY - 2, 12, 14);
            g.setColor(KNEE_PAD);
            g.fillRect(11, legY + 8, 10, 6);
            g.fillRect(27, legY + 6, 10, 6);
            g.setColor(BOOTS_COLOR);
            g.fillRect(8, legY + 16, 15, 8);
            g.fillRect(26, legY + 12, 15, 8);
            return;
        }

        if (isMoving) {
            int step = (animFrame / 6) % 4;
            int offsetL = (step == 0 || step == 1) ? -4 : 4;
            int offsetR = -offsetL;

            g.setColor(PANTS_DARK);
            g.fillRect(12 + offsetL, legY, 11, 16);
            g.fillRect(25 + offsetR, legY, 11, 16);
            g.setColor(KNEE_PAD);
            g.fillRect(12 + offsetL, legY + 6, 11, 6);
            g.fillRect(25 + offsetR, legY + 6, 11, 6);
            g.setColor(BOOTS_COLOR);
            g.fillRect(10 + offsetL, legY + 16, 15, 8);
            g.fillRect(23 + offsetR, legY + 16, 15, 8);
        } else {
            g.setColor(PANTS_DARK);
            g.fillRect(11, legY, 12, 16);
            g.fillRect(25, legY, 12, 16);
            g.setColor(KNEE_PAD);
            g.fillRect(11, legY + 6, 12, 6);
            g.fillRect(25, legY + 6, 12, 6);
            g.setColor(BOOTS_COLOR);
            g.fillRect(9, legY + 16, 15, 8);
            g.fillRect(24, legY + 16, 16, 8);
        }
    }

    private static void renderTorso(Graphics2D g, boolean crouched, int baseY) {
        int torsoY = baseY + 20;
        g.setColor(SHIRT_GREEN);
        g.fillRect(10, torsoY, 28, 30);
        g.setColor(VEST_COLOR);
        g.fillRect(12, torsoY + 2, 24, 25);
        g.setColor(VEST_POUCH);
        g.fillRect(14, torsoY + 6, 9, 8);
        g.fillRect(25, torsoY + 6, 9, 8);
        g.fillRect(14, torsoY + 16, 9, 8);
        g.fillRect(25, torsoY + 16, 9, 8);
        g.setColor(Color.BLACK);
        g.fillRect(10, torsoY + 27, 28, 4);
        g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
        g.fillRect(21, torsoY + 27, 6, 4);
    }

    private static void renderHead(Graphics2D g, boolean crouched, boolean isAimingUp, int baseY) {
        int headX = 14;
        int headY = baseY + (isAimingUp ? 0 : 2);
        g.setColor(SKIN_BASE);
        g.fillOval(headX, headY, 20, 20);
        g.setColor(SKIN_SHADOW);
        g.drawArc(headX, headY, 19, 19, 90, 180);
        g.setColor(BEARD_COLOR);
        g.fillRect(headX + 9, headY + 7, 7, 2);
        g.setColor(Color.BLACK);
        g.fillRect(headX + 11, headY + 9, 3, 2);
        g.setColor(Color.WHITE);
        g.fillRect(headX + 13, headY + 9, 1, 1);
        g.setColor(BEARD_COLOR);
        g.fillRect(headX + 7, headY + 11, 14, 11);
        g.fillRect(headX + 10, headY + 19, 12, 4);
        g.fillRect(headX + 12, headY + 12, 7, 3);
        g.setColor(SKIN_SHADOW);
        g.fillRect(headX + 3, headY + 8, 4, 6);
    }

    private static void renderArmsAndWeapon(Graphics2D g, boolean crouched, boolean isAimingUp, boolean isShooting, int weaponIndex, int baseY) {
        int armY = baseY + 24;

        if (isAimingUp) {
            g.setColor(SKIN_BASE);
            g.fillRect(18, armY - 14, 8, 18);
            g.setColor(GLOVE_COLOR);
            g.fillRect(17, armY - 18, 9, 6);
            g.setColor(new Color(25, 25, 25));
            g.fillRect(19, armY - 38, 6, 22);

            if (isShooting) {
                g.setColor(GameConfig.COLOR_MUZZLE_FLASH);
                g.fillOval(16, armY - 50, 12, 14);
                g.setColor(Color.WHITE);
                g.fillOval(18, armY - 46, 8, 8);
            }
        } else {
            g.setColor(SKIN_BASE);
            g.fillRect(18, armY + 2, 16, 7);
            g.setColor(GLOVE_COLOR);
            g.fillRect(32, armY + 1, 8, 8);

            switch (weaponIndex) {
                case 1: // ESCOPETA
                    g.setColor(new Color(40, 30, 25));
                    g.fillRect(26, armY + 4, 8, 5);
                    g.setColor(new Color(50, 50, 55));
                    g.fillRect(34, armY + 2, 22, 6);
                    break;
                case 2: // FUZIL AUTOMÁTICO
                    g.setColor(new Color(25, 25, 30));
                    g.fillRect(28, armY + 1, 24, 7);
                    g.fillRect(36, armY + 7, 5, 8);
                    break;
                case 3: // LANÇA-CHAMAS
                    g.setColor(new Color(180, 50, 40));
                    g.fillRect(2, armY - 2, 10, 16);
                    g.setColor(new Color(50, 50, 55));
                    g.fillRect(26, armY + 1, 22, 8);
                    g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
                    g.fillRect(44, armY, 4, 10);
                    break;
                default: // PISTOLA
                    g.setColor(new Color(30, 30, 30));
                    g.fillRect(32, armY + 2, 14, 6);
                    break;
            }

            if (isShooting) {
                int flashX = (weaponIndex == 1 ? 56 : weaponIndex == 2 ? 52 : 46);
                g.setColor(GameConfig.COLOR_MUZZLE_FLASH);
                g.fillOval(flashX, armY - 4, 16, 16);
                g.setColor(Color.WHITE);
                g.fillOval(flashX + 2, armY - 1, 10, 10);
            }
        }
    }

    private static void renderMeleeWrench(Graphics2D g, int progress, int baseY) {
        int armY = baseY + 24;
        float swingAngle = (float) (-Math.PI / 4.0 + (progress / (float) GameConfig.MELEE_COOLDOWN_FRAMES) * Math.PI);

        Graphics2D gWrench = (Graphics2D) g.create();
        gWrench.translate(28, armY + 4);
        gWrench.rotate(swingAngle);

        g.setColor(SKIN_BASE);
        g.fillRect(16, armY + 2, 14, 8);
        g.setColor(GLOVE_COLOR);
        g.fillRect(28, armY + 1, 8, 9);

        gWrench.setColor(WRENCH_HANDLE);
        gWrench.fillRect(0, -4, 28, 8);
        gWrench.setColor(WRENCH_STEEL);
        gWrench.fillRect(26, -9, 14, 18);
        gWrench.setColor(Color.BLACK);
        gWrench.fillRect(32, -4, 8, 8);

        gWrench.setColor(new Color(255, 255, 255, 160));
        gWrench.drawArc(-10, -25, 60, 50, -45, 90);

        gWrench.dispose();
    }

    // ==========================================
    // RENDERIZADOR DO ZUMBI CAMINHANTE (WALKER)
    // ==========================================
    public static void renderWalker(
            Graphics2D g,
            int x, int y,
            int facing,
            int animTick,
            boolean attacking,
            boolean isHurt,
            boolean dead,
            int deathTimer,
            int decayTimer
    ) {
        Graphics2D g2 = (Graphics2D) g.create();

        // Desvanecimento na decomposição final
        if (dead && decayTimer < 60) {
            float alpha = Math.max(0f, (float) decayTimer / 60.0f);
            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha));
        }

        if (facing == -1) {
            g2.translate(x + GameConfig.WALKER_WIDTH, y);
            g2.scale(-1, 1);
        } else {
            g2.translate(x, y);
        }

        if (dead) {
            // Corpo caído no chão com sangue escuro
            g2.setColor(GameConfig.COLOR_BLOOD_DARK);
            g2.fillOval(0, 52, 48, 16);
            g2.setColor(ZOMBIE_WALKER_PANTS);
            g2.fillRect(4, 50, 24, 12);
            g2.setColor(ZOMBIE_WALKER_SHIRT);
            g2.fillRect(20, 48, 20, 14);
            g2.setColor(ZOMBIE_WALKER_SKIN);
            g2.fillOval(36, 44, 16, 14);
            g2.dispose();
            return;
        }

        // Se ferido, pisca avermelhado
        Color skin = isHurt ? Color.RED : ZOMBIE_WALKER_SKIN;

        // Pernas trôpegas (passos arrastados)
        int step = (animTick / 10) % 4;
        int stepOffset = (step == 0 || step == 1) ? -3 : 3;
        g2.setColor(ZOMBIE_WALKER_PANTS);
        g2.fillRect(8 + stepOffset, 48, 10, 20);
        g2.fillRect(22 - stepOffset, 48, 10, 20);

        // Tronco / Camisa rasgada manchada de sangue
        g2.setColor(ZOMBIE_WALKER_SHIRT);
        g2.fillRect(8, 20, 24, 28);
        g2.setColor(GameConfig.COLOR_BLOOD_DARK);
        g2.fillRect(14, 26, 12, 16);

        // Cabeça apodrecida e olhos amarelos doentios
        g2.setColor(skin);
        g2.fillOval(10, 2, 20, 20);
        g2.setColor(new Color(255, 230, 80)); // Olhos infectados
        g2.fillRect(20, 8, 4, 3);
        g2.setColor(GameConfig.COLOR_BLOOD_DARK);
        g2.fillRect(16, 14, 8, 4); // Mandíbula sangrenta

        // Braços estendidos para frente
        g2.setColor(skin);
        if (attacking) {
            // Golpe duplo para frente
            g2.fillRect(18, 22, 24, 8);
            g2.setColor(GameConfig.COLOR_BLOOD_DARK);
            g2.fillRect(38, 20, 6, 12);
        } else {
            // Braços trôpegos balançando
            int armBob = (step == 0) ? -2 : 2;
            g2.fillRect(16, 24 + armBob, 20, 6);
        }

        g2.dispose();
    }

    // ==========================================
    // RENDERIZADOR DO ZUMBI CORREDOR (RUNNER)
    // ==========================================
        // ==========================================
    // RENDERIZADOR DO ZUMBI FORTÃO (TANK)
    // ==========================================
    public static void renderTank(
            Graphics2D g,
            int x, int y,
            int facing,
            int animTick,
            boolean attacking,
            boolean isHurt,
            boolean dead,
            int deathTimer,
            int decayTimer
    ) {
        Graphics2D g2 = (Graphics2D) g.create();

        if (dead && decayTimer < 60) {
            float alpha = Math.max(0f, (float) decayTimer / 60.0f);
            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha));
        }

        if (facing == -1) {
            g2.translate(x + br.com.ultimoabrigo.core.GameConfig.TANK_WIDTH, y);
            g2.scale(-1, 1);
        } else {
            g2.translate(x, y);
        }

        if (dead) {
            g2.setColor(br.com.ultimoabrigo.core.GameConfig.COLOR_BLOOD_DARK);
            g2.fillOval(0, 60, 60, 20);
            g2.setColor(new Color(60, 50, 40));
            g2.fillRect(10, 55, 30, 16);
            g2.setColor(new Color(90, 100, 90));
            g2.fillOval(40, 50, 20, 18);
            g2.dispose();
            return;
        }

        Color skin = isHurt ? Color.RED : new Color(90, 100, 90); // Pele esverdeada/cinza musculosa
        Color pants = new Color(60, 50, 40);

        int step = (animTick / 14) % 4;
        int stepOffset = (step == 0 || step == 1) ? -4 : 4;
        g2.setColor(pants);
        g2.fillRect(14 + stepOffset, 50, 14, 26);
        g2.fillRect(32 - stepOffset, 50, 14, 26);

        // Tronco grande
        g2.setColor(skin);
        g2.fillRoundRect(10, 15, 40, 35, 10, 10);
        g2.setColor(br.com.ultimoabrigo.core.GameConfig.COLOR_BLOOD_DARK);
        g2.fillRect(16, 26, 20, 20);

        // Cabeça
        g2.setColor(skin);
        g2.fillOval(20, -5, 20, 20);
        g2.setColor(Color.RED); 
        g2.fillRect(28, 0, 6, 4);

        // Braços
        g2.setColor(skin);
        if (attacking) {
            g2.fillRect(25, 20, 30, 12);
        } else {
            int armBob = (step == 0) ? -2 : 2;
            g2.fillRect(20, 20 + armBob, 14, 25);
        }

        g2.dispose();
    }

    // ==========================================
    public static void renderRunner(
            Graphics2D g,
            int x, int y,
            int facing,
            int animTick,
            boolean isTelegraphing,
            boolean isLeaping,
            boolean isHurt,
            boolean dead,
            int deathTimer,
            int decayTimer
    ) {
        Graphics2D g2 = (Graphics2D) g.create();

        if (dead && decayTimer < 60) {
            float alpha = Math.max(0f, (float) decayTimer / 60.0f);
            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha));
        }

        if (facing == -1) {
            g2.translate(x + GameConfig.RUNNER_WIDTH, y);
            g2.scale(-1, 1);
        } else {
            g2.translate(x, y);
        }

        if (dead) {
            g2.setColor(GameConfig.COLOR_BLOOD_DARK);
            g2.fillOval(0, 48, 46, 16);
            g2.setColor(ZOMBIE_RUNNER_RAGS);
            g2.fillRect(8, 46, 22, 12);
            g2.setColor(ZOMBIE_RUNNER_SKIN);
            g2.fillOval(26, 42, 16, 14);
            g2.dispose();
            return;
        }

        Color skin = isHurt ? Color.RED : ZOMBIE_RUNNER_SKIN;

        if (isTelegraphing) {
            // Pose Telegrafada de 0.3s: agachamento predatório, garras no solo, olhos vermelhos pulsantes
            g2.setColor(skin);
            g2.fillRect(4, 38, 28, 16); // Tronco rebaixado
            g2.setColor(ZOMBIE_RUNNER_RAGS);
            g2.fillRect(6, 40, 20, 12);
            // Cabeça rente ao solo
            g2.setColor(skin);
            g2.fillOval(22, 30, 18, 18);
            // Olho vermelho brilhante com alerta telegrafado
            g2.setColor(GameConfig.COLOR_DANGER_RED);
            g2.fillRect(32, 34, 6, 5);
            g2.setColor(Color.WHITE);
            g2.fillRect(34, 35, 2, 2);
            // Garras fincadas na terra
            g2.setColor(CLAW_COLOR);
            g2.fillRect(26, 54, 12, 8);
            g2.dispose();
            return;
        }

        if (isLeaping) {
            // Pose de Salto Aéreo: corpo projetado para a frente, garras esticadas
            g2.setColor(skin);
            g2.fillRect(2, 20, 32, 14); // Corpo horizontal
            g2.setColor(ZOMBIE_RUNNER_RAGS);
            g2.fillRect(6, 22, 22, 10);
            // Cabeça avançada
            g2.setColor(skin);
            g2.fillOval(26, 14, 18, 16);
            g2.setColor(GameConfig.COLOR_DANGER_RED);
            g2.fillRect(36, 18, 5, 4);
            // Garras prontas para o bote
            g2.setColor(CLAW_COLOR);
            g2.fillRect(36, 24, 14, 6);
            g2.dispose();
            return;
        }

        // Corrida ágil (passadas rápidas com corpo inclinado)
        int step = (animTick / 4) % 4;
        int stepOffset = (step == 0 || step == 1) ? -6 : 6;
        g2.setColor(skin);
        g2.fillRect(6 + stepOffset, 42, 9, 22);
        g2.fillRect(20 - stepOffset, 42, 9, 22);

        // Tronco inclinado para a frente
        g2.setColor(ZOMBIE_RUNNER_RAGS);
        g2.fillRect(6, 18, 22, 24);

        // Cabeça feroz
        g2.setColor(skin);
        g2.fillOval(14, 4, 18, 18);
        g2.setColor(GameConfig.COLOR_DANGER_RED);
        g2.fillRect(24, 10, 5, 4);

        // Braços com garras afiadas balançando em velocidade
        g2.setColor(skin);
        g2.fillRect(16, 20, 16, 6);
        g2.setColor(CLAW_COLOR);
        g2.fillRect(28, 19, 8, 8);

        g2.dispose();
    }

    // ==========================================
    // RENDERIZADOR DO BOSS 1: MR. X
    // ==========================================
    public static void renderMrX(
            Graphics2D g,
            int x, int y,
            int facing,
            int animTick,
            br.com.ultimoabrigo.entities.MrXBoss.BossState state,
            boolean trenchcoatTorn,
            boolean isHurt,
            boolean defeated,
            int defeatTimer
    ) {
        Graphics2D g2 = (Graphics2D) g.create();

        if (facing == -1) {
            g2.translate(x + 60, y);
            g2.scale(-1, 1);
        } else {
            g2.translate(x, y);
        }

        // Se derrotado, treme e desmorona
        if (defeated) {
            g2.setColor(new Color(25, 25, 25));
            g2.fillRect(6, 40, 50, 60);
            g2.dispose();
            return;
        }

        Color coatColor = isHurt ? Color.RED : new Color(34, 38, 44);
        Color skinColor = isHurt ? Color.WHITE : new Color(175, 170, 165);

        // 1. PERNAS E BOTAS PESADAS
        int legStep = (animTick / 8) % 4;
        int offL = (legStep == 0 || legStep == 1) ? -4 : 4;
        g2.setColor(new Color(20, 20, 24));
        g2.fillRect(14 + offL, 76, 15, 26);
        g2.fillRect(32 - offL, 76, 15, 26);
        // Botas de ferro
        g2.setColor(new Color(15, 15, 18));
        g2.fillRect(11 + offL, 92, 19, 10);
        g2.fillRect(29 - offL, 92, 19, 10);

        if (!trenchcoatTorn) {
            // FASE 1: SOBRETUDO LONGO E ELEGANTE
            g2.setColor(coatColor);
            // Aba longa do sobretudo cobrindo até as pernas
            g2.fillRect(10, 32, 42, 52);
            // Dobras e botões metálicos
            g2.setColor(new Color(20, 22, 26));
            g2.fillRect(29, 32, 4, 52);
            g2.setColor(new Color(190, 190, 200));
            for (int b = 0; b < 4; b++) {
                g2.fillRect(25, 38 + b * 11, 3, 3);
                g2.fillRect(34, 38 + b * 11, 3, 3);
            }
            // Gola alta erguida
            g2.setColor(new Color(25, 28, 32));
            g2.fillRect(16, 22, 30, 12);
        } else {
            // FASE 2: SOBRETUDO RASGADO, TRONCO MUSCULOSO E CICATRIZES
            g2.setColor(skinColor);
            g2.fillRect(12, 28, 38, 48); // Peito e abdômen colossal
            // Músculos e veias pulsantes
            g2.setColor(new Color(140, 40, 40));
            g2.fillRect(16, 38, 12, 14);
            g2.fillRect(32, 38, 12, 14);
            g2.fillRect(20, 56, 20, 16);
            // Tiras de tecido rasgado balançando na cintura
            g2.setColor(coatColor);
            g2.fillRect(8, 70, 44, 12);
            g2.fillRect(10, 80, 8, 12);
            g2.fillRect(36, 80, 10, 14);
        }

        // 2. CABEÇA E CHAPÉU
        g2.setColor(skinColor);
        g2.fillOval(18, 10, 24, 22);

        if (!trenchcoatTorn) {
            // Chapéu Fedora largo de aba escura
            g2.setColor(new Color(24, 26, 30));
            g2.fillRect(8, 14, 44, 5); // Aba larga
            g2.fillRect(16, 4, 28, 12); // Copa do chapéu
            g2.setColor(new Color(15, 15, 18));
            g2.fillRect(16, 12, 28, 3); // Fita do chapéu
            // Olho ameaçador sob a sombra da aba
            g2.setColor(Color.WHITE);
            g2.fillRect(32, 19, 4, 2);
        } else {
            // Cabeça descoberta com cicatriz profunda e olhos vermelhos em fúria
            g2.setColor(GameConfig.COLOR_DANGER_RED);
            g2.fillRect(30, 16, 6, 4); // Olho brilhando
            g2.setColor(Color.WHITE);
            g2.fillRect(32, 17, 2, 2);
            g2.setColor(new Color(110, 20, 20));
            g2.drawLine(22, 10, 28, 26); // Cicatriz
        }

        // 3. BRAÇOS E SOCO
        g2.setColor(trenchcoatTorn ? skinColor : coatColor);
        if (state == br.com.ultimoabrigo.entities.MrXBoss.BossState.TELEGRAPH_PUNCH) {
            // Punho recuado carregando energia
            g2.fillRect(4, 28, 18, 16);
            g2.setColor(Color.WHITE);
            g2.drawOval(0, 24, 24, 24); // Alerta visual de soco
        } else if (state == br.com.ultimoabrigo.entities.MrXBoss.BossState.PUNCHING) {
            // Soco colossal lançado para frente
            g2.fillRect(28, 32, 34, 16);
            g2.setColor(new Color(20, 20, 20));
            g2.fillRect(56, 30, 16, 20); // Luva preta maciça
            // Onda de choque do impacto
            g2.setColor(new Color(255, 255, 255, 180));
            g2.drawArc(50, 15, 35, 50, -45, 90);
        } else if (state == br.com.ultimoabrigo.entities.MrXBoss.BossState.CHARGING) {
            // Postura de carga (ombro projetado para a frente)
            g2.fillRect(32, 26, 24, 22);
            g2.setColor(new Color(20, 20, 20));
            g2.fillRect(50, 28, 12, 16);
        } else if (state == br.com.ultimoabrigo.entities.MrXBoss.BossState.STUNNED) {
            // Atordoado: cabeça tombada e estrelas
            g2.fillRect(10, 36, 16, 24);
            g2.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
            int starOff = (animTick * 6) % 360;
            g2.drawString("★", 20 + (int)(Math.cos(Math.toRadians(starOff))*14), 2);
            g2.drawString("★", 20 - (int)(Math.cos(Math.toRadians(starOff))*14), 2);
        } else {
            // Braço padrão balançando na marcha
            g2.fillRect(10, 32, 16, 28);
            g2.setColor(new Color(20, 20, 20));
            g2.fillRect(10, 56, 16, 12);
        }

        g2.dispose();
    }

    // ==========================================
    // RENDERIZADOR DO BOSS 2: TYRANT
    // ==========================================
    public static void renderTyrant(
            Graphics2D g,
            int x, int y,
            int facing,
            int animTick,
            br.com.ultimoabrigo.entities.TyrantBoss.TyrantState state,
            boolean heartExposed,
            boolean isHurt,
            boolean defeated,
            int defeatTimer
    ) {
        Graphics2D g2 = (Graphics2D) g.create();

        if (facing == -1) {
            g2.translate(x + 68, y);
            g2.scale(-1, 1);
        } else {
            g2.translate(x, y);
        }

        if (defeated) {
            g2.setColor(new Color(45, 20, 20));
            g2.fillRect(4, 52, 60, 52);
            g2.dispose();
            return;
        }

        Color skinColor = isHurt ? Color.WHITE : new Color(165, 172, 178);
        Color muscleColor = new Color(110, 45, 50);
        Color clawBoneColor = new Color(28, 24, 28);

        // 1. PERNAS PODEROSAS
        int step = (animTick / 7) % 4;
        int off = (step == 0 || step == 1) ? -5 : 5;
        g2.setColor(new Color(35, 38, 45)); // Calça rasgada
        g2.fillRect(16 + off, 74, 16, 28);
        g2.fillRect(36 - off, 74, 16, 28);
        g2.setColor(skinColor);
        g2.fillRect(14 + off, 94, 20, 12);
        g2.fillRect(34 - off, 94, 20, 12);

        // 2. TRONCO GIGANTESCO
        g2.setColor(skinColor);
        g2.fillRect(12, 24, 44, 52);
        g2.setColor(muscleColor); // Fibras musculares expostas
        g2.fillRect(16, 42, 16, 26);
        g2.fillRect(36, 42, 16, 26);

        // 3. CORAÇÃO EXPOSTO (PONTO FRACO)
        if (heartExposed) {
            // Pulsação vibrante em vermelho/laranja crítico
            float pulse = (float) (Math.sin(animTick * 0.4) * 0.3 + 0.7);
            g2.setColor(new Color(1.0f, 0.2f * pulse, 0.1f));
            g2.fillOval(20, 32, 22, 22);
            g2.setColor(Color.WHITE);
            g2.fillOval(25, 37, 12, 12);
            // Mira de Ponto Fraco
            g2.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
            g2.drawOval(16, 28, 30, 30);
            g2.drawLine(14, 43, 48, 43);
            g2.drawLine(31, 26, 31, 60);
        } else {
            // Coração sob a couraça
            g2.setColor(new Color(130, 25, 25));
            g2.fillOval(22, 34, 18, 18);
            g2.setColor(new Color(180, 50, 40));
            g2.fillOval(25, 37, 12, 12);
        }

        // 4. CABEÇA PÁLIDA E AMEAÇADORA
        g2.setColor(skinColor);
        g2.fillOval(22, 4, 24, 22);
        g2.setColor(GameConfig.COLOR_DANGER_RED);
        g2.fillRect(36, 12, 5, 4); // Olho vermelho brilhante
        g2.setColor(Color.BLACK);
        g2.fillRect(28, 18, 14, 4); // Mandíbula

        // 5. BRAÇO NORMAL (DIREITO)
        g2.setColor(skinColor);
        g2.fillRect(6, 28, 12, 32);
        g2.setColor(new Color(25, 25, 30));
        g2.fillRect(4, 52, 14, 14);

        // 6. GARRA COLOSSAL (BRAÇO ESQUERDO)
        if (state == br.com.ultimoabrigo.entities.TyrantBoss.TyrantState.TELEGRAPH_CLAW) {
            // Garra puxada para trás preparando o golpe
            g2.setColor(muscleColor);
            g2.fillRect(36, 18, 22, 20);
            g2.setColor(clawBoneColor);
            g2.fillRect(52, 6, 20, 14);
            g2.setColor(GameConfig.COLOR_DANGER_RED);
            g2.drawArc(45, 0, 35, 40, 0, 180);
        } else if (state == br.com.ultimoabrigo.entities.TyrantBoss.TyrantState.SWEEPING_CLAW) {
            // Golpe em arco varrendo tudo à frente
            g2.setColor(muscleColor);
            g2.fillRect(40, 32, 28, 22);
            g2.setColor(clawBoneColor);
            // 3 Lâminas curvas colossais
            g2.fillRect(64, 22, 28, 8);
            g2.fillRect(68, 36, 32, 9);
            g2.fillRect(64, 50, 26, 8);
            // Rastro vermelho de corte
            g2.setColor(new Color(255, 30, 30, 200));
            g2.drawArc(50, 10, 55, 60, -60, 120);
        } else if (state == br.com.ultimoabrigo.entities.TyrantBoss.TyrantState.CHARGING) {
            // Garra apontada para frente como aríete
            g2.setColor(muscleColor);
            g2.fillRect(44, 30, 26, 24);
            g2.setColor(clawBoneColor);
            g2.fillRect(66, 28, 30, 24);
        } else if (state == br.com.ultimoabrigo.entities.TyrantBoss.TyrantState.STUNNED_WEAK_POINT) {
            // Atordoado de joelhos com garra caída
            g2.setColor(muscleColor);
            g2.fillRect(38, 48, 22, 20);
        } else {
            // Padrão: garra imensa pendendo até o chão
            g2.setColor(muscleColor);
            g2.fillRect(40, 28, 22, 34);
            g2.setColor(clawBoneColor);
            g2.fillRect(42, 58, 24, 36);
            // Pontas das garras tocando o chão
            g2.fillRect(42, 88, 6, 16);
            g2.fillRect(52, 88, 7, 20);
            g2.fillRect(62, 88, 6, 16);
        }

        g2.dispose();
    }

    public static void renderNemesis(
            Graphics2D g,
            int x, int y,
            int facing,
            int animTick,
            String state,
            int phase,
            boolean hurt,
            boolean defeated,
            int defeatTimer
    ) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(x, y);

        if (facing == -1) {
            g2.scale(-1, 1);
            g2.translate(-50, 0); // width=50
        }

        // Treme se for ferido
        if (hurt) {
            g2.translate(Math.random() * 4 - 2, Math.random() * 4 - 2);
        }

        // Se derrotado
        if (defeated) {
            g2.translate(Math.random() * (defeatTimer / 20.0), 0);
            g2.scale(1.0, Math.max(0.1, 1.0 - (defeatTimer / 180.0)));
        }

        // Cores base da mutação
        Color skinColor = new Color(70, 75, 80);
        Color mutateColor = new Color(110, 40, 130); // Roxo mutante
        Color eyeColor = Color.RED;

        if (phase == 2) skinColor = new Color(85, 55, 95);
        if (phase == 3) {
            skinColor = new Color(110, 30, 40); // Vermelho carne
            mutateColor = new Color(200, 30, 30);
        }

        if (hurt) skinColor = Color.WHITE;

        // Corpo Base (Tamanho aprox 50x95)
        g2.setColor(skinColor);
        g2.fillRect(10, 20, 30, 75); // tronco e pernas
        
        // Detalhes musculares ou armadura
        g2.setColor(new Color(40, 45, 50));
        g2.fillRect(15, 40, 20, 20); 

        // Cabeça
        g2.setColor(skinColor);
        g2.fillRect(12, 0, 26, 20);
        // Olho brilhante
        g2.setColor(eyeColor);
        g2.fillRect(28, 5, 6, 4);

        // Braço/Arma (Fase 1: Lança Foguetes, Fase 2/3: Tentáculos)
        if (phase == 1) {
            // Lança foguetes no braço direito (frente)
            g2.setColor(new Color(60, 60, 65));
            g2.fillRect(25, 25, 35, 12);
            g2.setColor(Color.BLACK);
            g2.fillRect(55, 27, 8, 8); // bocal
        } else {
            // Tentáculos (Fase 2 e 3)
            g2.setColor(mutateColor);
            if (state.equals("WHIPPING")) {
                // Tentáculo esticado
                g2.fillRect(30, 30, 80, 8);
                g2.fillRect(100, 25, 15, 15); // ponta grossa
            } else {
                // Tentáculo recolhido ondulando
                int waveY = (int)(Math.sin(animTick * 0.2) * 5);
                g2.fillRect(25, 30 + waveY, 15, 10);
                g2.fillRect(40, 35 - waveY, 15, 10);
                g2.fillRect(55, 30 + waveY, 15, 10);
            }
        }
        
        // Mutação extra nas costas na Fase 3
        if (phase == 3) {
            g2.setColor(mutateColor);
            g2.fillRect(2, 10, 12, 30);
            g2.fillRect(-5, 15, 10, 15);
        }

        g2.dispose();
    }
}
