package br.com.ultimoabrigo.ui;

import br.com.ultimoabrigo.assets.AssetLoader;
import br.com.ultimoabrigo.core.GameConfig;
import br.com.ultimoabrigo.core.GameState;
import br.com.ultimoabrigo.core.GameStateHandler;
import br.com.ultimoabrigo.core.InputHandler;
import br.com.ultimoabrigo.core.StateManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * IntroCutsceneState - Exibe os 6 quadros da história original com efeito
 * datilografado (typewriter) e narração da jornada de Elias Rocha.
 */
public class IntroCutsceneState implements GameStateHandler {

    private final StateManager stateManager;

    private static class FrameData {
        final int frameIndex;
        final String title;
        final String text;

        FrameData(int frameIndex, String title, String text) {
            this.frameIndex = frameIndex;
            this.title = title;
            this.text = text;
        }
    }

    private final FrameData[] frames = new FrameData[]{
            new FrameData(1, "O COLAPSO DA CIDADE",
                    "A Síndrome de Degeneração Neurológica Aguda (SDNA) destruiu a civilização em poucos dias. Elias Rocha, mecânico e eletricista, viu pela janela da oficina os infectados atacando a população e as defesas militares ruírem."),
            new FrameData(2, "A FUGA PARA VALE DO CEDRO",
                    "Elias pegou sua velha caminhonete, ferramentas, alimentos e partiu na noite chuvosa com sua esposa Marina e sua filha Lívia. Na estrada, soldados queimavam pilhas de corpos... mas alguns cadáveres ainda se moviam."),
            new FrameData(3, "A CHEGADA AO ABRIGO",
                    "Eles chegaram à antiga propriedade de seu pai no Vale do Cedro. Cercada por florestas densas, parecia o refúgio ideal. Elias começou imediatamente a reforçar janelas e portas com tábuas enquanto a família descarregava."),
            new FrameData(4, "A SEPARAÇÃO TRÁGICA",
                    "A horda atacou na madrugada. As cercas cederam e Marina foi ferida no depósito. A infecção foi implacável. Antes de partir, ela entregou o rádio a Elias: '...Lívia... laboratório ao norte... não confiem nos soldados...'"),
            new FrameData(5, "O DESAPARECIMENTO DE LÍVIA",
                    "Após enterrar Marina, Elias correu para proteger Lívia, mas encontrou o quarto vazio: janelas estilhaçadas, pegadas na mata e o pequeno casaco da filha com manchas de sangue recente. Lívia havia sido levada."),
            new FrameData(6, "METAL ZOMBIE",
                    "Elias jurou transformar aquela casa em sua fortaleza e ponto de partida. Ele caçará os responsáveis por cada centímetro do mundo devastado até encontrar Lívia, custe o que custar. 'Eles levaram a única coisa que me restava... e eu vou até o inferno para buscá-la.'")
    };

    private int currentFrame = 0;
    private int visibleChars = 0;
    private int tickCount = 0;
    private BufferedImage currentImage;

    public IntroCutsceneState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void enter() {
        currentFrame = 0;
        loadFrame(0);
    }

    private void loadFrame(int index) {
        currentFrame = index;
        visibleChars = 0;
        tickCount = 0;
        currentImage = AssetLoader.getCutsceneFrame(frames[index].frameIndex);
    }

    @Override
    public void update(InputHandler input) {
        tickCount++;
        FrameData data = frames[currentFrame];

        // Efeito de máquina de escrever: adiciona 1 caractere a cada 2 ticks
        if (tickCount % 2 == 0 && visibleChars < data.text.length()) {
            visibleChars++;
        }

        // Avançar quadro ou completar texto
        if (input.isConfirmJustPressed()) {
            if (visibleChars < data.text.length()) {
                // Se ainda está datilografando, exibe o texto inteiro imediatamente
                visibleChars = data.text.length();
            } else {
                // Avança para o próximo quadro
                if (currentFrame < frames.length - 1) {
                    loadFrame(currentFrame + 1);
                } else {
                    // Fim da intro: inicia o jogo
                    stateManager.setState(GameState.PLAYING);
                }
            }
        }

        // Pular intro completa com ESC ou P
        if (input.isPauseJustPressed()) {
            stateManager.setState(GameState.PLAYING);
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(new Color(12, 12, 16));
        g.fillRect(0, 0, GameConfig.LOGICAL_WIDTH, GameConfig.LOGICAL_HEIGHT);

        FrameData data = frames[currentFrame];

        // Desenha imagem do quadro centralizada no topo
        int imgW = 580;
        int imgH = 340;
        int imgX = (GameConfig.LOGICAL_WIDTH - imgW) / 2;
        int imgY = 25;

        if (currentImage != null) {
            g.drawImage(currentImage, imgX, imgY, imgW, imgH, null);
        }

        // Moldura metálica e estilizada ao redor da cena
        g.setColor(new Color(40, 45, 55));
        g.drawRect(imgX - 2, imgY - 2, imgW + 4, imgH + 4);
        g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
        g.drawRect(imgX - 4, imgY - 4, imgW + 8, imgH + 8);

        // Painel inferior de texto
        int textPanelY = 380;
        int textPanelH = 125;
        g.setColor(new Color(20, 20, 26, 235));
        g.fillRect(imgX - 4, textPanelY, imgW + 8, textPanelH);
        g.setColor(new Color(60, 60, 75));
        g.drawRect(imgX - 4, textPanelY, imgW + 8, textPanelH);

        // Título do quadro
        g.setFont(new Font("Impact", Font.PLAIN, 18));
        g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
        g.drawString(String.format("[%d/%d]  %s", currentFrame + 1, frames.length, data.title), imgX + 15, textPanelY + 28);

        // Texto com quebra automática de linha
        g.setFont(new Font("SansSerif", Font.PLAIN, 15));
        g.setColor(new Color(230, 230, 235));
        String currentText = data.text.substring(0, Math.min(visibleChars, data.text.length()));
        drawWrappedText(g, currentText, imgX + 15, textPanelY + 52, imgW - 30, 22);

        // Rodapé de navegação
        g.setFont(GameConfig.FONT_UI);
        g.setColor(new Color(140, 140, 140));
        String hint = "[ENTER / ESPAÇO / J] Continuar   |   [ESC] Pular História";
        g.drawString(hint, (GameConfig.LOGICAL_WIDTH - g.getFontMetrics().stringWidth(hint)) / 2, 526);
    }

    private void drawWrappedText(Graphics2D g, String text, int x, int y, int maxWidth, int lineHeight) {
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        int curY = y;

        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            if (g.getFontMetrics().stringWidth(testLine) > maxWidth) {
                g.drawString(currentLine.toString(), x, curY);
                currentLine = new StringBuilder(word);
                curY += lineHeight;
            } else {
                currentLine = new StringBuilder(testLine);
            }
        }
        if (currentLine.length() > 0) {
            g.drawString(currentLine.toString(), x, curY);
        }
    }

    @Override
    public void exit() {
    }
}
