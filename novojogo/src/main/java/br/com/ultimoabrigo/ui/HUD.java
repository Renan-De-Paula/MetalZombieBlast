package br.com.ultimoabrigo.ui;

import br.com.ultimoabrigo.core.GameConfig;
import br.com.ultimoabrigo.entities.Player;
import br.com.ultimoabrigo.entities.WeaponType;
import br.com.ultimoabrigo.world.DifficultyManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * HUD - Renderiza as informações vitais do jogador estilo arcade Metal Slug:
 * corações de vida, vidas restantes, arma atual e munição, granadas,
 * pontuação, o medidor de distância percorrida até o chefe (1000 m)
 * e o indicador discreto de intensidade da horda (DifficultyManager).
 */
public class HUD {

    private static final Font FONT_IMPACT_18 = new Font("Impact", Font.PLAIN, 18);
    private static final Font FONT_IMPACT_16 = new Font("Impact", Font.PLAIN, 16);
    private static final Font FONT_ARIAL_12 = new Font("Arial", Font.BOLD, 12);
    private static final Font FONT_ARIAL_13 = new Font("Arial", Font.BOLD, 13);
    private static final Font FONT_ARIAL_11 = new Font("Arial", Font.BOLD, 11);
    private static final Font FONT_MONO_13 = new Font("Monospaced", Font.BOLD, 13);
    private static final Font FONT_MONO_11 = new Font("Monospaced", Font.BOLD, 11);

    public static void render(Graphics2D g, Player player, int distanceMeters, int stageNumber, DifficultyManager diffMgr) {
        // Barra superior translúcida
        g.setColor(new Color(15, 18, 24, 220));
        g.fillRect(0, 0, GameConfig.LOGICAL_WIDTH, 48);

        // Borda inferior dourada arcade
        g.setColor(new Color(60, 50, 30));
        g.fillRect(0, 47, GameConfig.LOGICAL_WIDTH, 2);

        // 1. VIDAS E CORAÇÕES DE ELIAS (CANTO SUPERIOR ESQUERDO)
        g.setFont(FONT_IMPACT_18);
        g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
        g.drawString("ELIAS", 16, 22);

        g.setFont(FONT_ARIAL_12);
        g.setColor(Color.WHITE);
        g.drawString("x" + player.getLives(), 62, 22);

        // 5 Corações de vida
        int startHeartX = 92;
        int heartY = 10;
        int playerHearts = player.getHearts();

        for (int i = 0; i < GameConfig.PLAYER_MAX_HEARTS; i++) {
            int hx = startHeartX + (i * 20);
            if (i < playerHearts) {
                g.setColor(GameConfig.COLOR_DANGER_RED);
                drawHeart(g, hx, heartY, 14, 14, true);
            } else {
                g.setColor(new Color(60, 60, 65));
                drawHeart(g, hx, heartY, 14, 14, false);
            }
        }

        // 2. ARMA SELECIONADA E MUNIÇÃO
        WeaponType weapon = player.getCurrentWeapon();
        int ammo = player.getCurrentAmmo();
        String ammoStr = (ammo < 0) ? "∞" : String.valueOf(ammo);

        int weaponX = 215;
        g.setFont(FONT_IMPACT_16);
        g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
        g.drawString(weapon.getDisplayName(), weaponX, 22);

        g.setFont(GameConfig.FONT_UI);
        g.setColor(Color.WHITE);
        g.drawString("[" + ammoStr + "]", weaponX + g.getFontMetrics().stringWidth(weapon.getDisplayName()) + 6, 22);

        // Granadas / Molotovs (L)
        int grenadeX = 370;
        g.setColor(new Color(100, 180, 80));
        g.fillOval(grenadeX, 10, 14, 14);
        g.setColor(Color.WHITE);
        g.setFont(FONT_ARIAL_13);
        g.drawString("💣 x" + player.getGrenades(), grenadeX + 18, 22);

        // 3. MEDIDOR DE DISTÂNCIA E PROGRESSO ATÉ O BOSS (1000 METROS)
        int distPanelX = 475;
        g.setFont(FONT_MONO_13);
        g.setColor(Color.WHITE);
        String distText = String.format("%4dm/1000m", Math.min(1000, distanceMeters));
        g.drawString(distText, distPanelX, 22);

        int barX = distPanelX + 110;
        int barY = 12;
        int barW = 110;
        int barH = 12;

        g.setColor(Color.BLACK);
        g.fillRect(barX, barY, barW, barH);

        float progress = Math.min(1.0f, (float) distanceMeters / GameConfig.METERS_PER_PHASE);
        g.setColor(GameConfig.COLOR_DANGER_RED);
        g.fillRect(barX, barY, (int) (barW * progress), barH);

        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barW, barH);

        g.setFont(FONT_ARIAL_11);
        g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
        g.drawString("☠", barX + barW + 5, 22);

        // 4. INDICADOR DISCRETO DE INTENSIDADE / DIFICULDADE (HORDA)
        if (diffMgr != null) {
            int curScreen = Math.min(32, distanceMeters / 30);
            int intensity = diffMgr.getIntensityPercentage(curScreen);
            String threat = diffMgr.getThreatLevelText(curScreen);

            int threatX = barX + barW + 28;
            g.setFont(FONT_MONO_11);
            Color threatColor = (intensity < 30) ? new Color(120, 220, 120) :
                                (intensity < 60) ? new Color(240, 210, 60) :
                                (intensity < 85) ? new Color(240, 130, 40) : GameConfig.COLOR_DANGER_RED;

            g.setColor(threatColor);
            g.drawString("HORDA: " + intensity + "%", threatX, 22);
        }

        // 5. PONTUAÇÃO (CANTO SUPERIOR DIREITO)
        g.setFont(FONT_IMPACT_18);
        g.setColor(Color.WHITE);
        String scoreText = String.format("SCORE: %06d", player.getScore());
        g.drawString(scoreText, GameConfig.LOGICAL_WIDTH - 140, 22);

        // Legenda de controles
        g.setFont(FONT_MONO_11);
        g.setColor(new Color(150, 150, 150));
        g.drawString("[A/D] Andar  [W/Espaço] Pular  [S] Agachar  [J] Atirar  [K] Chave  [L] Granada  [1-4] Armas", 16, 40);
    }

    private static void drawHeart(Graphics2D g, int x, int y, int w, int h, boolean filled) {
        if (filled) {
            g.fillOval(x, y, w / 2, h / 2);
            g.fillOval(x + w / 2, y, w / 2, h / 2);
            int[] xPoints = {x, x + w, x + w / 2};
            int[] yPoints = {y + h / 4, y + h / 4, y + h};
            g.fillPolygon(xPoints, yPoints, 3);
        } else {
            g.drawOval(x, y, w / 2, h / 2);
            g.drawOval(x + w / 2, y, w / 2, h / 2);
            g.drawLine(x, y + h / 4, x + w / 2, y + h);
            g.drawLine(x + w, y + h / 4, x + w / 2, y + h);
        }
    }
}
