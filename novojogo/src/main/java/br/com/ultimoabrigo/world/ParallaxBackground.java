package br.com.ultimoabrigo.world;

import br.com.ultimoabrigo.core.GameConfig;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * ParallaxBackground - Renderiza 3 camadas de profundidade com velocidades de scroll
 * desacopladas e efeitos climáticos (chuva torrencial na floresta da Fase 1).
 */
public class ParallaxBackground {

    private final int stageNumber;
    private int animTick = 0;

    // Partículas de chuva para a Fase 1
    private final int RAIN_COUNT = 90;
    private final float[] rainX = new float[RAIN_COUNT];
    private final float[] rainY = new float[RAIN_COUNT];
    private final float[] rainSpeed = new float[RAIN_COUNT];

    public ParallaxBackground(int stageNumber) {
        this.stageNumber = stageNumber;
        for (int i = 0; i < RAIN_COUNT; i++) {
            rainX[i] = (float) (Math.random() * GameConfig.LOGICAL_WIDTH);
            rainY[i] = (float) (Math.random() * GameConfig.LOGICAL_HEIGHT);
            rainSpeed[i] = (float) (Math.random() * 8.0 + 12.0);
        }
    }

    public void update() {
        animTick++;
        // Atualiza chuva
        if (stageNumber == 1) {
            for (int i = 0; i < RAIN_COUNT; i++) {
                rainY[i] += rainSpeed[i];
                rainX[i] -= 2.5f; // Chuva em diagonal com vento
                if (rainY[i] > GameConfig.LOGICAL_HEIGHT) {
                    rainY[i] = -10;
                    rainX[i] = (float) (Math.random() * (GameConfig.LOGICAL_WIDTH + 150));
                }
            }
        }
    }

    public void render(Graphics2D g, float cameraX) {
        switch (stageNumber) {
            case 2:
                renderStage2City(g, cameraX);
                break;
            case 3:
                renderStage3Bunker(g, cameraX);
                break;
            default:
                renderStage1Forest(g, cameraX);
                break;
        }
    }

    private void renderStage1Forest(Graphics2D g, float cameraX) {
        // CAMADA 1: Céu tempestuoso noturno e montanhas distantes (fator 0.06)
        g.setColor(new Color(14, 18, 24));
        g.fillRect(0, 0, GameConfig.LOGICAL_WIDTH, GameConfig.LOGICAL_HEIGHT);

        // Montanhas escuras no horizonte
        g.setColor(new Color(22, 28, 36));
        float offset1 = (cameraX * 0.06f) % 400;
        for (int i = -1; i < 4; i++) {
            int bx = (int) (i * 400 - offset1);
            int[] px = {bx, bx + 200, bx + 400};
            int[] py = {GameConfig.GROUND_Y, GameConfig.GROUND_Y - 180, GameConfig.GROUND_Y};
            g.fillPolygon(px, py, 3);
        }

        // CAMADA 2: Silhueta intermediária de pinheiros densos (fator 0.28)
        g.setColor(new Color(28, 38, 32));
        float offset2 = (cameraX * 0.28f) % 180;
        for (int i = -1; i < 8; i++) {
            int tx = (int) (i * 180 - offset2);
            // Copas triangulares de pinheiro
            int[] ptx = {tx, tx + 90, tx + 180};
            int[] pty = {GameConfig.GROUND_Y, GameConfig.GROUND_Y - 130, GameConfig.GROUND_Y};
            g.fillPolygon(ptx, pty, 3);
        }

        // CAMADA 3: Vegetação lateral e postes de telégrafo abandonados (fator 0.65)
        g.setColor(new Color(36, 48, 38));
        float offset3 = (cameraX * 0.65f) % 320;
        for (int i = -1; i < 5; i++) {
            int px = (int) (i * 320 - offset3);
            // Poste de madeira inclinado com fiação arrebentada
            g.setColor(new Color(55, 42, 30));
            g.fillRect(px + 40, GameConfig.GROUND_Y - 150, 8, 150);
            g.fillRect(px + 28, GameConfig.GROUND_Y - 145, 32, 6);
            g.setColor(new Color(35, 45, 35));
            g.fillOval(px + 120, GameConfig.GROUND_Y - 60, 80, 65);
        }

        // Efeito atmosférico de chuva torrencial
        g.setColor(new Color(160, 190, 230, 95));
        for (int i = 0; i < RAIN_COUNT; i++) {
            int rx = (int) rainX[i];
            int ry = (int) rainY[i];
            g.drawLine(rx, ry, rx - 4, ry + 12);
        }
    }

    private void renderStage2City(Graphics2D g, float cameraX) {
        // CAMADA 1: Céu alaranjado de incêndios urbanos e fuligem
        g.setColor(new Color(30, 20, 22));
        g.fillRect(0, 0, GameConfig.LOGICAL_WIDTH, GameConfig.LOGICAL_HEIGHT);

        // Silhueta distante de arranha-céus em chamas
        g.setColor(new Color(45, 32, 35));
        float offset1 = (cameraX * 0.08f) % 240;
        for (int i = -1; i < 6; i++) {
            int bx = (int) (i * 240 - offset1);
            g.fillRect(bx, GameConfig.GROUND_Y - 220, 90, 220);
            g.fillRect(bx + 110, GameConfig.GROUND_Y - 170, 70, 170);
        }

        // CAMADA 2: Prédios médios comerciais e viadutos destruídos
        g.setColor(new Color(35, 36, 42));
        float offset2 = (cameraX * 0.32f) % 200;
        for (int i = -1; i < 7; i++) {
            int px = (int) (i * 200 - offset2);
            g.fillRect(px, GameConfig.GROUND_Y - 130, 80, 130);
            // Janelas escuras quebradas
            g.setColor(new Color(20, 20, 25));
            for (int r = 0; r < 3; r++) {
                g.fillRect(px + 12, GameConfig.GROUND_Y - 115 + r * 28, 14, 16);
                g.fillRect(px + 36, GameConfig.GROUND_Y - 115 + r * 28, 14, 16);
            }
            g.setColor(new Color(35, 36, 42));
        }

        // CAMADA 3: Postes de rua caídos e muros pichados
        g.setColor(new Color(48, 48, 55));
        float offset3 = (cameraX * 0.68f) % 280;
        for (int i = -1; i < 5; i++) {
            int mx = (int) (i * 280 - offset3);
            g.fillRect(mx + 20, GameConfig.GROUND_Y - 60, 120, 60);
        }
    }

    private void renderStage3Bunker(Graphics2D g, float cameraX) {
        // CAMADA 1: Paredes industriais profundas e dutos de ventilação
        g.setColor(new Color(15, 20, 25));
        g.fillRect(0, 0, GameConfig.LOGICAL_WIDTH, GameConfig.LOGICAL_HEIGHT);

        // Painéis e vigas estruturais ao fundo
        g.setColor(new Color(25, 32, 40));
        float offset1 = (cameraX * 0.1f) % 180;
        for (int i = -1; i < 7; i++) {
            int px = (int) (i * 180 - offset1);
            g.fillRect(px, 0, 16, GameConfig.GROUND_Y);
            g.fillRect(px, 140, 180, 12);
        }

        // CAMADA 2: Tubos de gás e fiação de alta tensão suspensa
        g.setColor(new Color(35, 45, 55));
        float offset2 = (cameraX * 0.35f) % 240;
        for (int i = -1; i < 6; i++) {
            int tx = (int) (i * 240 - offset2);
            g.fillRect(tx, 70, 240, 18);
            // Luzes estroboscópicas de emergência
            boolean strobe = (animTick / 16 + i) % 2 == 0;
            g.setColor(strobe ? new Color(240, 60, 40) : new Color(60, 15, 10));
            g.fillOval(tx + 110, 88, 12, 12);
            g.setColor(new Color(35, 45, 55));
        }

        // CAMADA 3: Portas de segurança seladas e monitores de vigilância
        g.setColor(new Color(45, 52, 60));
        float offset3 = (cameraX * 0.70f) % 360;
        for (int i = -1; i < 4; i++) {
            int dx = (int) (i * 360 - offset3);
            g.fillRect(dx + 50, GameConfig.GROUND_Y - 140, 90, 140);
            g.setColor(new Color(20, 25, 30));
            g.fillRect(dx + 60, GameConfig.GROUND_Y - 125, 70, 125);
            g.setColor(new Color(45, 52, 60));
        }
    }
}
