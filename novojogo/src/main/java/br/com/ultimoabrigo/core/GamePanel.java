package br.com.ultimoabrigo.core;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

/**
 * GamePanel - Painel de renderização customizado usando Canvas nativo.
 * Utiliza BufferStrategy para aceleração de hardware real, eliminando
 * totalmente o tearing, blocos pretos e lag de software do Swing.
 */
public class GamePanel extends Canvas {

    private final BufferedImage logicalBuffer;
    private final Graphics2D logicalGraphics;
    private final StateManager stateManager;
    private boolean showDebugInfo = false;
    private double currentFps = 0.0;
    private double currentUps = 0.0;

    public GamePanel(StateManager stateManager, InputHandler inputHandler) {
        this.stateManager = stateManager;
        setPreferredSize(new Dimension(GameConfig.DEFAULT_WINDOW_WIDTH, GameConfig.DEFAULT_WINDOW_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(inputHandler);

        // Buffer lógico interno (onde o jogo desenha)
        this.logicalBuffer = new BufferedImage(
                GameConfig.LOGICAL_WIDTH,
                GameConfig.LOGICAL_HEIGHT,
                BufferedImage.TYPE_INT_RGB
        );
        // Desativa o cache VRAM assíncrono bugado do Java2D no Windows.
        // Garante que a cópia para a tela seja síncrona e 100% perfeita.
        this.logicalBuffer.setAccelerationPriority(0.0f);

        this.logicalGraphics = logicalBuffer.createGraphics();
        logicalGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Impede que o SO e o Java pintem a tela de fundo sozinhos
        setIgnoreRepaint(true);
    }

    /**
     * Deve ser chamado DEPOIS que a janela for visível.
     */
    public void initBufferStrategy() {
        if (getBufferStrategy() == null) {
            createBufferStrategy(2); // Double Buffering nativo em Hardware
        }
    }

    public void updateDebugStats(double fps, double ups) {
        this.currentFps = fps;
        this.currentUps = ups;
    }

    public void toggleDebug() {
        this.showDebugInfo = !this.showDebugInfo;
    }

    /**
     * Renderiza o estado atual com aceleração gráfica e BufferStrategy.
     */
    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            return;
        }

        // 1. Limpa o buffer lógico
        logicalGraphics.setColor(Color.BLACK);
        logicalGraphics.fillRect(0, 0, GameConfig.LOGICAL_WIDTH, GameConfig.LOGICAL_HEIGHT);

        // 2. Renderiza o estado ativo
        stateManager.render(logicalGraphics);
        if (showDebugInfo) {
            renderDebugOverlay(logicalGraphics);
        }

        // 3. Obtém o Graphics da placa de vídeo e projeta o buffer lógico
        do {
            do {
                Graphics2D g2d = (Graphics2D) bs.getDrawGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

                int panelWidth = getWidth();
                int panelHeight = getHeight();

                double scaleX = (double) panelWidth / GameConfig.LOGICAL_WIDTH;
                double scaleY = (double) panelHeight / GameConfig.LOGICAL_HEIGHT;
                double scale = Math.min(scaleX, scaleY);

                int drawWidth = (int) Math.round(GameConfig.LOGICAL_WIDTH * scale);
                int drawHeight = (int) Math.round(GameConfig.LOGICAL_HEIGHT * scale);
                int offsetX = (panelWidth - drawWidth) / 2;
                int offsetY = (panelHeight - drawHeight) / 2;

                // Fundo preto apenas nas bordas (letterbox/pillarbox)
                g2d.setColor(Color.BLACK);
                if (offsetX > 0) {
                    g2d.fillRect(0, 0, offsetX, panelHeight);
                    g2d.fillRect(panelWidth - offsetX, 0, offsetX, panelHeight);
                }
                if (offsetY > 0) {
                    g2d.fillRect(0, 0, panelWidth, offsetY);
                    g2d.fillRect(0, panelHeight - offsetY, panelWidth, offsetY);
                }

                g2d.drawImage(logicalBuffer, offsetX, offsetY, drawWidth, drawHeight, null);
                
                g2d.dispose();
            } while (bs.contentsRestored());

            bs.show();
        } while (bs.contentsLost());

        java.awt.Toolkit.getDefaultToolkit().sync();
    }

    private void renderDebugOverlay(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(10, 10, 240, 75);
        g.setColor(Color.GREEN);
        g.setFont(GameConfig.FONT_UI);
        g.drawString(String.format("FPS: %.1f | UPS: %.1f", currentFps, currentUps), 20, 30);
        g.drawString("ESTADO: " + stateManager.getCurrentState(), 20, 50);
        g.drawString("RESOLUÇÃO: 960x540 (Escalado Nativo)", 20, 70);
    }

    @Override
    public void paint(java.awt.Graphics g) {
        // IGNORA: Bloqueia o AWT de intrometer e causar blocos pretos
    }

    @Override
    public void update(java.awt.Graphics g) {
        // IGNORA: Bloqueia o AWT de dar clearRect na tela (flicker)
    }

    public BufferedImage getLogicalBuffer() {
        return logicalBuffer;
    }
}
