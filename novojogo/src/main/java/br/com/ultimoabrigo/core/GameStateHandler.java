package br.com.ultimoabrigo.core;

import java.awt.Graphics2D;

/**
 * Interface comum para todos os estados de tela do jogo.
 */
public interface GameStateHandler {
    void enter();
    void update(InputHandler input);
    void render(Graphics2D g);
    void exit();
}
