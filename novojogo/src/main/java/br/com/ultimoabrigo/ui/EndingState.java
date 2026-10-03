package br.com.ultimoabrigo.ui;

import br.com.ultimoabrigo.core.GameConfig;
import br.com.ultimoabrigo.core.GameState;
import br.com.ultimoabrigo.core.GameStateHandler;
import br.com.ultimoabrigo.core.InputHandler;
import br.com.ultimoabrigo.core.StateManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * EndingState - Cutscene e tela de encerramento emocional.
 * Revela que Marina não faleceu, mas foi mantida em criogenia militar
 * tratada com o soro de imunidade de Lívia. Elias encontra ambas vivas.
 */
public class EndingState implements GameStateHandler {

    private final StateManager stateManager;
    private int tickCount = 0;
    private int visibleChars = 0;

    private final String narrative =
            "Elias adentra a câmara criogênica central do laboratório subterrâneo.\n" +
            "Diante dele, a verdade é revelada por documentos da equipe científica militar:\n\n" +
            "Marina não havia falecido! Ela fora resgatada pelos médicos militares em estado crítico\n" +
            "e mantida em estase criogênica sob tratamento intensivo.\n" +
            "Lívia fora trazida ao complexo devido à sua imunidade genética rara contra o vírus SDNA.\n" +
            "O soro derivado do sangue de sua filha estabilizou a infecção de Marina!\n\n" +
            "As portas da câmara se abrem. Lívia corre com lágrimas nos olhos e abraça Elias com força.\n" +
            "Marina desperta da cápsula criogênica, recuperada e lúcida.\n" +
            "Juntos, a família Rocha caminha para fora do bunker em direção ao nascer do sol no Vale do Cedro.\n\n" +
            "\"Enquanto essa casa estiver de pé, ainda existe alguém para salvar.\"\n\n" +
            "— FIM DA JORNADA —";

    public EndingState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void enter() {
        tickCount = 0;
        visibleChars = 0;
    }

    @Override
    public void update(InputHandler input) {
        tickCount++;
        if (visibleChars < narrative.length()) {
            visibleChars++;
        }

        if (input.isConfirmJustPressed()) {
            if (visibleChars < narrative.length()) {
                visibleChars = narrative.length();
            } else {
                stateManager.setState(GameState.MENU);
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(new Color(15, 15, 25));
        g.fillRect(0, 0, GameConfig.LOGICAL_WIDTH, GameConfig.LOGICAL_HEIGHT);

        g.setFont(new Font("Impact", Font.BOLD, 38));
        g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
        String header = "REENCONTRO NO LABORATÓRIO CENTRAL";
        int headerW = g.getFontMetrics().stringWidth(header);
        g.drawString(header, (GameConfig.LOGICAL_WIDTH - headerW) / 2, 60);

        g.setFont(new Font("SansSerif", Font.PLAIN, 15));
        g.setColor(new Color(230, 235, 240));

        String displayed = narrative.substring(0, Math.min(visibleChars, narrative.length()));
        String[] lines = displayed.split("\n");
        int y = 110;
        for (String line : lines) {
            if (line.contains("FIM DA JORNADA")) {
                g.setFont(new Font("Impact", Font.BOLD, 28));
                g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
                int lw = g.getFontMetrics().stringWidth(line);
                g.drawString(line, (GameConfig.LOGICAL_WIDTH - lw) / 2, y + 10);
            } else if (line.contains("Enquanto essa casa estiver de pé")) {
                g.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 16));
                g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
                int lw = g.getFontMetrics().stringWidth(line);
                g.drawString(line, (GameConfig.LOGICAL_WIDTH - lw) / 2, y);
            } else {
                g.setFont(new Font("SansSerif", Font.PLAIN, 15));
                g.setColor(new Color(225, 230, 240));
                g.drawString(line, 60, y);
            }
            y += 24;
        }

        g.setFont(GameConfig.FONT_UI);
        g.setColor(new Color(150, 150, 150));
        String hint = "[ENTER / ESPAÇO / J] Retornar ao Menu Principal";
        g.drawString(hint, (GameConfig.LOGICAL_WIDTH - g.getFontMetrics().stringWidth(hint)) / 2, 515);
    }

    @Override
    public void exit() {
    }
}
