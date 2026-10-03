package br.com.ultimoabrigo.core;

/**
 * GameEngine - Controla a execução do Game Loop com timestep fixo a 60 UPS
 * e renderização desacoplada, garantindo física e lógica precisas e determinísticas.
 */
public class GameEngine implements Runnable {

    private final StateManager stateManager;
    private final InputHandler inputHandler;
    private final GamePanel gamePanel;
    private final GameWindow gameWindow;

    private Thread gameThread;
    private volatile boolean running = false;

    // Métricas de desempenho
    private double currentFps = 0.0;
    private double currentUps = 0.0;

    public GameEngine(StateManager stateManager, InputHandler inputHandler, GamePanel gamePanel, GameWindow gameWindow) {
        this.stateManager = stateManager;
        this.inputHandler = inputHandler;
        this.gamePanel = gamePanel;
        this.gameWindow = gameWindow;
    }

    public synchronized void start() {
        if (running) {
            return;
        }
        running = true;
        gameThread = new Thread(this, "UltimoAbrigo-GameLoop");
        gameThread.start();
    }

    public synchronized void stop() {
        if (!running) {
            return;
        }
        running = false;
        try {
            if (gameThread != null) {
                gameThread.join(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        long initialTime = System.nanoTime();
        final double nsPerUpdate = 1_000_000_000.0 / GameConfig.TARGET_UPS;
        double delta = 0.0;

        int updates = 0;
        int frames = 0;
        long timer = System.currentTimeMillis();

        while (running) {
            long currentTime = System.nanoTime();
            delta += (currentTime - initialTime) / nsPerUpdate;
            initialTime = currentTime;

            // Executa updates fixos para acompanhar o tempo
            while (delta >= 1.0) {
                update();
                updates++;
                delta--;
            }

            // Renderiza o quadro atual
            render();
            frames++;

            // Atualiza estatísticas a cada 1 segundo
            if (System.currentTimeMillis() - timer > 1000) {
                currentFps = frames;
                currentUps = updates;
                gamePanel.updateDebugStats(currentFps, currentUps);
                frames = 0;
                updates = 0;
                timer += 1000;
            }

            // Breve sleep para aliviar CPU mantendo alta taxa de quadros
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void update() {
        // Atualiza a borda das teclas (just pressed / released)
        inputHandler.update();

        // Atalhos globais do motor
        if (inputHandler.isFullscreenJustPressed()) {
            gameWindow.toggleFullscreen();
        }
        if (inputHandler.isDebugJustPressed()) {
            gamePanel.toggleDebug();
        }

        // Atualiza o estado ativo
        stateManager.update(inputHandler);
    }

    private void render() {
        gamePanel.render();
    }

    public double getCurrentFps() {
        return currentFps;
    }

    public double getCurrentUps() {
        return currentUps;
    }
}
