package br.com.ultimoabrigo.world;

import br.com.ultimoabrigo.assets.SoundSystem;
import br.com.ultimoabrigo.core.GameConfig;
import br.com.ultimoabrigo.entities.Player;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Level - Representa uma fase completa com exatamente 1000 metros (32.000 pixels).
 * Gerencia a câmera com scroll lateral, 3 camadas de parallax, solo texturizado por tema,
 * checkpoints a cada 250m e acionamento da arena de chefe aos 1000m.
 */
public class Level {

    private final int stageNumber;
    private final Camera camera;
    private final ParallaxBackground parallax;
    private final List<Checkpoint> checkpoints = new ArrayList<>();

    // Notificação temporária de Checkpoint
    private String bannerMessage = null;
    private int bannerTimer = 0;

    // Controle de Chefe aos 1000m
    private boolean bossTriggered = false;

    public Level(int stageNumber) {
        this.stageNumber = stageNumber;
        this.camera = new Camera();
        this.parallax = new ParallaxBackground(stageNumber);

        // Inicializa os 3 checkpoints canônicos da fase (250m, 500m, 750m)
        checkpoints.add(new Checkpoint(250));
        checkpoints.add(new Checkpoint(500));
        checkpoints.add(new Checkpoint(750));
    }

    public void update(Player player) {
        parallax.update();
        camera.update(player.getX());

        // Atualiza checkpoints e verifica ativação por Elias
        for (Checkpoint cp : checkpoints) {
            cp.update();
            if (cp.checkPlayerActivation(player)) {
                showBanner("CHECKPOINT " + cp.getMeterDistance() + "m ATIVADO!");
            }
        }

        // Temporizador do banner
        if (bannerTimer > 0) {
            bannerTimer--;
            if (bannerTimer == 0) {
                bannerMessage = null;
            }
        }

        // Chegada aos 1000 metros (32.000 px): Trava a câmera e inicia arena do chefe
        int distanceMeters = (int) (player.getX() / GameConfig.PIXELS_PER_METER);
        if (distanceMeters >= GameConfig.METERS_PER_PHASE && !bossTriggered) {
            triggerBossArena();
        }
    }

    public void triggerBossArena() {
        bossTriggered = true;
        float arenaCamX = GameConfig.STAGE_WIDTH_PIXELS - GameConfig.LOGICAL_WIDTH;
        camera.lockAt(arenaCamX);
        camera.shake(8.0f, 60);
        SoundSystem.playBossAlarm();
    }

    public void showBanner(String message) {
        this.bannerMessage = message;
        this.bannerTimer = 160; // ~2.6 segundos
    }

    public void renderBackground(Graphics2D g) {
        parallax.render(g, camera.getX());
        renderGround(g);
        renderCheckpoints(g);
    }

    private void renderGround(Graphics2D g) {
        float camX = camera.getX();

        switch (stageNumber) {
            case 2: // Cidade Velha: Asfalto quebrado e calçada
                g.setColor(new Color(38, 38, 42));
                g.fillRect(0, GameConfig.GROUND_Y, GameConfig.LOGICAL_WIDTH, GameConfig.LOGICAL_HEIGHT - GameConfig.GROUND_Y);
                // Meio-fio de concreto
                g.setColor(new Color(60, 60, 65));
                g.fillRect(0, GameConfig.GROUND_Y - 4, GameConfig.LOGICAL_WIDTH, 4);
                // Faixas amarelas desgastadas de trânsito
                g.setColor(new Color(200, 175, 40, 180));
                for (int i = 0; i < 30; i++) {
                    int fx = (int) (i * 90 - (camX % 90));
                    g.fillRect(fx, GameConfig.GROUND_Y + 16, 45, 5);
                }
                break;

            case 3: // Bunker: Piso metálico industrial com grades e faixas de perigo
                g.setColor(new Color(30, 34, 40));
                g.fillRect(0, GameConfig.GROUND_Y, GameConfig.LOGICAL_WIDTH, GameConfig.LOGICAL_HEIGHT - GameConfig.GROUND_Y);
                // Faixa de perigo zebrada (amarelo e preto)
                for (int i = 0; i < 40; i++) {
                    int zx = (int) (i * 40 - (camX % 40));
                    g.setColor(i % 2 == 0 ? GameConfig.COLOR_METAL_SLUG_YELLOW : Color.BLACK);
                    g.fillRect(zx, GameConfig.GROUND_Y - 4, 20, 6);
                }
                break;

            default: // Fase 1: Estrada de terra, cascalho e poças da floresta
                g.setColor(new Color(44, 34, 26));
                g.fillRect(0, GameConfig.GROUND_Y, GameConfig.LOGICAL_WIDTH, GameConfig.LOGICAL_HEIGHT - GameConfig.GROUND_Y);
                g.setColor(new Color(28, 22, 18));
                g.fillRect(0, GameConfig.GROUND_Y + 14, GameConfig.LOGICAL_WIDTH, GameConfig.LOGICAL_HEIGHT - GameConfig.GROUND_Y - 14);
                // Borda de mato e lama
                g.setColor(new Color(34, 52, 28));
                g.fillRect(0, GameConfig.GROUND_Y - 3, GameConfig.LOGICAL_WIDTH, 4);
                break;
        }

        // Marcações métricas no solo a cada 25m e números a cada 50m
        g.setFont(new Font("Monospaced", Font.BOLD, 11));
        g.setColor(new Color(140, 135, 120));
        for (int m = 0; m <= GameConfig.METERS_PER_PHASE; m += 25) {
            float worldX = m * GameConfig.PIXELS_PER_METER;
            int screenX = (int) (worldX - camX);
            if (screenX >= -40 && screenX <= GameConfig.LOGICAL_WIDTH + 40) {
                g.fillRect(screenX, GameConfig.GROUND_Y, 2, 8);
                if (m % 50 == 0) {
                    g.drawString(m + "m", screenX - 8, GameConfig.GROUND_Y + 22);
                }
            }
        }
    }

    private void renderCheckpoints(Graphics2D g) {
        for (Checkpoint cp : checkpoints) {
            cp.render(g, camera.getX(), 0);
        }
    }

    public void renderForegroundOverlay(Graphics2D g) {
        // Exibe banner animado de Checkpoint atingido
        if (bannerMessage != null && bannerTimer > 0) {
            int bw = 460;
            int bh = 42;
            int bx = (GameConfig.LOGICAL_WIDTH - bw) / 2;
            int by = 60;

            g.setColor(new Color(15, 20, 28, 230));
            g.fillRoundRect(bx, by, bw, bh, 8, 8);
            g.setColor(new Color(40, 220, 40));
            g.drawRoundRect(bx, by, bw, bh, 8, 8);

            g.setFont(new Font("Impact", Font.PLAIN, 20));
            g.setColor(Color.WHITE);
            int tw = g.getFontMetrics().stringWidth(bannerMessage);
            g.drawString(bannerMessage, (GameConfig.LOGICAL_WIDTH - tw) / 2, by + 28);
        }

        // Se estiver na arena final de 1000m, desenha a estrutura do Posto Abandonado e contenção
        if (bossTriggered || camera.isLockedInArena()) {
            float camX = camera.getX();

            // Cenário da Arena da Fase 1: Posto Abandonado do Vale do Cedro
            if (stageNumber == 1) {
                int stationX = (int) (31300 - camX);
                // Cobertura do posto de gasolina
                g.setColor(new Color(45, 50, 58));
                g.fillRect(stationX, GameConfig.GROUND_Y - 160, 360, 24);
                // Pilastras de sustentação
                g.setColor(new Color(60, 65, 75));
                g.fillRect(stationX + 40, GameConfig.GROUND_Y - 136, 16, 136);
                g.fillRect(stationX + 300, GameConfig.GROUND_Y - 136, 16, 136);
                // Letreiro quebrado "POSTO VALE DO CEDRO"
                g.setColor(new Color(25, 25, 30));
                g.fillRect(stationX + 70, GameConfig.GROUND_Y - 190, 220, 26);
                g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
                g.drawRect(stationX + 70, GameConfig.GROUND_Y - 190, 220, 26);
                g.setFont(new Font("Impact", Font.PLAIN, 15));
                g.drawString("POSTO CEDRO - COMBUSTÍVEL", stationX + 80, GameConfig.GROUND_Y - 172);

                // Bombas de combustível enferrujadas
                g.setColor(new Color(130, 40, 35));
                g.fillRect(stationX + 90, GameConfig.GROUND_Y - 55, 30, 55);
                g.fillRect(stationX + 230, GameConfig.GROUND_Y - 55, 30, 55);
                g.setColor(Color.BLACK);
                g.fillRect(stationX + 95, GameConfig.GROUND_Y - 45, 20, 16);
                g.fillRect(stationX + 235, GameConfig.GROUND_Y - 45, 20, 16);
            } else if (stageNumber == 2) {
                // Cenário da Arena da Fase 2: Pátio da Delegacia e Clínica Rural
                int clinicX = (int) (31300 - camX);
                // Fachada da Delegacia / Clínica
                g.setColor(new Color(55, 50, 52));
                g.fillRect(clinicX, GameConfig.GROUND_Y - 180, 380, 180);
                g.setColor(new Color(30, 30, 35));
                g.fillRect(clinicX + 20, GameConfig.GROUND_Y - 160, 340, 40);
                // Placa luminosa
                g.setColor(GameConfig.COLOR_DANGER_RED);
                g.drawRect(clinicX + 20, GameConfig.GROUND_Y - 160, 340, 40);
                g.setFont(new Font("Impact", Font.PLAIN, 16));
                g.setColor(Color.WHITE);
                g.drawString("DELEGACIA MUNICIPAL & CLÍNICA RURAL", clinicX + 45, GameConfig.GROUND_Y - 134);

                // Portas de vidro arrombadas e manchas de sangue
                g.setColor(new Color(20, 25, 30));
                g.fillRect(clinicX + 130, GameConfig.GROUND_Y - 90, 120, 90);
                g.setColor(GameConfig.COLOR_BLOOD_DARK);
                g.fillRect(clinicX + 160, GameConfig.GROUND_Y - 60, 60, 30);

                // Viatura de polícia destruída no pátio
                g.setColor(new Color(35, 45, 65));
                g.fillRect(clinicX + 50, GameConfig.GROUND_Y - 40, 80, 40);
                g.setColor(Color.WHITE);
                g.drawString("POLÍCIA", clinicX + 65, GameConfig.GROUND_Y - 18);
            } else if (stageNumber == 3) {
                // Cenário da Arena da Fase 3: Laboratório Subterrâneo no Bunker
                int labX = (int) (31300 - camX);
                
                // Portão do Laboratório
                g.setColor(new Color(45, 45, 50));
                g.fillRect(labX, GameConfig.GROUND_Y - 200, 400, 200);
                
                // Tubulações de gás tóxico e fios soltos
                g.setColor(new Color(60, 70, 60));
                g.fillRect(labX + 20, GameConfig.GROUND_Y - 190, 360, 20);
                g.setColor(new Color(90, 140, 80)); // Gás residual (verde)
                g.fillRect(labX + 50, GameConfig.GROUND_Y - 185, 30, 30);
                
                // Letreiro
                g.setColor(new Color(20, 20, 25));
                g.fillRect(labX + 40, GameConfig.GROUND_Y - 150, 320, 50);
                g.setColor(GameConfig.COLOR_DANGER_RED);
                g.drawRect(labX + 40, GameConfig.GROUND_Y - 150, 320, 50);
                g.setFont(new Font("Impact", Font.PLAIN, 20));
                g.drawString("NÚCLEO DE CRIOGENIA - SETOR ZERO", labX + 60, GameConfig.GROUND_Y - 120);

                // Portão principal quebrado
                g.setColor(new Color(25, 25, 30));
                g.fillRect(labX + 130, GameConfig.GROUND_Y - 90, 140, 90);
                g.setColor(new Color(40, 40, 45));
                g.fillRect(labX + 120, GameConfig.GROUND_Y - 95, 10, 95);
                g.fillRect(labX + 270, GameConfig.GROUND_Y - 95, 10, 95);
            }

            // Parede / Barricada de contenção no fim dos 1000m
            int wallX = (int) (GameConfig.STAGE_WIDTH_PIXELS - camX);
            if (wallX <= GameConfig.LOGICAL_WIDTH) {
                g.setColor(new Color(80, 25, 25));
                g.fillRect(wallX - 30, 0, 30, GameConfig.GROUND_Y);
                g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
                g.drawRect(wallX - 30, 0, 30, GameConfig.GROUND_Y);
            }
        }
    }

    public Camera getCamera() { return camera; }
    public boolean isBossTriggered() { return bossTriggered; }
    public int getStageNumber() { return stageNumber; }
}
