package br.com.ultimoabrigo.core;

import br.com.ultimoabrigo.ui.BossFightState;
import br.com.ultimoabrigo.ui.EndingState;
import br.com.ultimoabrigo.ui.GameOverState;
import br.com.ultimoabrigo.ui.IntroCutsceneState;
import br.com.ultimoabrigo.ui.MenuState;
import br.com.ultimoabrigo.ui.PauseState;
import br.com.ultimoabrigo.ui.PhaseClearState;
import br.com.ultimoabrigo.ui.PlayingState;

import javax.swing.SwingUtilities;

/**
 * Game - Ponto de entrada (Main) do jogo "METAL ZOMBIE".
 * Inicializa a máquina de estados, subsistemas, janela gráfica e dispara o Game Loop a 60 UPS.
 */
public class Game {

    private final StateManager stateManager;
    private final InputHandler inputHandler;
    private final GamePanel gamePanel;
    private final GameWindow gameWindow;
    private final GameEngine gameEngine;

    public Game() {
        // Inicializa o gerenciador de estados
        this.stateManager = new StateManager();

        // Registra todos os 8 estados da máquina de estados
        stateManager.registerState(GameState.MENU, new MenuState(stateManager));
        stateManager.registerState(GameState.INTRO_CUTSCENE, new IntroCutsceneState(stateManager));
        stateManager.registerState(GameState.PLAYING, new PlayingState(stateManager));
        stateManager.registerState(GameState.BOSS_FIGHT, new BossFightState(stateManager));
        stateManager.registerState(GameState.PAUSED, new PauseState(stateManager));
        stateManager.registerState(GameState.PHASE_CLEAR, new PhaseClearState(stateManager));
        stateManager.registerState(GameState.GAME_OVER, new GameOverState(stateManager));
        stateManager.registerState(GameState.ENDING, new EndingState(stateManager));

        // Define o estado inicial como MENU
        stateManager.setState(GameState.MENU);

        // Inicializa entrada do teclado
        this.inputHandler = new InputHandler();

        // Inicializa painel com buffer lógico 960x540 e nearest-neighbor
        this.gamePanel = new GamePanel(stateManager, inputHandler);

        // Inicializa janela JFrame
        this.gameWindow = new GameWindow(gamePanel);

        // Inicializa motor com timestep fixo a 60 UPS
        this.gameEngine = new GameEngine(stateManager, inputHandler, gamePanel, gameWindow);
    }

    public void start() {
        gameWindow.show();
        gameEngine.start();
    }

    public static void main(String[] args) {
        // Configurações críticas para evitar "Buffer Tearing" e retângulos pretos no Windows.
        // Desativa o Direct3D (bugado no Java2D) e usa OpenGL puro ou software se necessário.
        System.setProperty("sun.java2d.d3d", "false");
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("sun.java2d.noddraw", "true");

        SwingUtilities.invokeLater(() -> {
            Game game = new Game();
            game.start();
        });
    }
}
