package br.com.ultimoabrigo.core;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

/**
 * GameWindow - Janela principal JFrame com suporte a tela cheia exclusiva
 * e modo janela redimensionável.
 */
public class GameWindow {

    private final JFrame frame;
    private final GamePanel panel;
    private boolean isFullscreen = false;

    public GameWindow(GamePanel panel) {
        this.panel = panel;
        this.frame = new JFrame(GameConfig.GAME_TITLE);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setIgnoreRepaint(true); // Impede interferência do painel cinza do JFrame
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);

        // Ícone customizado da janela
        try {
            BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g = icon.createGraphics();
            g.setColor(GameConfig.COLOR_BLOOD_DARK);
            g.fillRoundRect(2, 2, 28, 28, 6, 6);
            g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
            g.drawString("ÚA", 6, 22);
            g.dispose();
            frame.setIconImage(icon);
        } catch (Exception ignored) {
        }
    }

    public void show() {
        frame.setVisible(true);
        panel.initBufferStrategy(); // Inicializa aceleração de hardware
        panel.requestFocusInWindow();
    }

    public void toggleFullscreen() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        frame.dispose(); // Necessário para trocar undecorated
        if (!isFullscreen) {
            frame.setUndecorated(true);
            if (gd.isFullScreenSupported()) {
                gd.setFullScreenWindow(frame);
            } else {
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
            isFullscreen = true;
        } else {
            if (gd.isFullScreenSupported() && gd.getFullScreenWindow() == frame) {
                gd.setFullScreenWindow(null);
            }
            frame.setUndecorated(false);
            frame.setSize(new Dimension(GameConfig.DEFAULT_WINDOW_WIDTH, GameConfig.DEFAULT_WINDOW_HEIGHT));
            frame.setLocationRelativeTo(null);
            isFullscreen = false;
        }
        frame.setVisible(true);
        panel.initBufferStrategy();
        panel.requestFocusInWindow();
    }

    public JFrame getFrame() {
        return frame;
    }
}
