package br.com.ultimoabrigo.ui;

import br.com.ultimoabrigo.assets.AssetLoader;
import br.com.ultimoabrigo.assets.VideoPlayer;
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
 * MenuState - Menu principal estilo Metal Slug / Arcade com opções de jogo,
 * controles, história e saída.
 */
public class MenuState implements GameStateHandler {

    private final StateManager stateManager;
    private final String[] options = {
            "NOVO JOGO",
            "CONTINUAR",
            "CONTROLES",
            "CREDITOS",
            "VER HISTÓRIA",
            "SAIR"
    };
    private int selectedIndex = 0;
    private boolean showingControls = false;
    private boolean showingCredits = false;
    private int animTick = 0;
    private VideoPlayer videoPlayer;

    public MenuState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void enter() {
        selectedIndex = 0;
        showingControls = false;
        animTick = 0;
        
        // Inicializa e carrega o vídeo já extraído em JPEGs (24 FPS)
        if (videoPlayer == null) {
            videoPlayer = new VideoPlayer("/assets/cutscenes/capaMenu_frames", 24);
        }
    }

    @Override
    public void update(InputHandler input) {
        animTick++;

        if (videoPlayer != null) {
            videoPlayer.update();
        }

        if (showingControls) {
            if (input.isConfirmJustPressed() || input.isPauseJustPressed()) {
                showingControls = false;
            }
            return;
        }

        if (input.isKeyJustPressed(java.awt.event.KeyEvent.VK_W) || input.isKeyJustPressed(java.awt.event.KeyEvent.VK_UP)) {
            selectedIndex--;
            if (selectedIndex < 0) {
                selectedIndex = options.length - 1;
            }
        }
        if (input.isKeyJustPressed(java.awt.event.KeyEvent.VK_S) || input.isKeyJustPressed(java.awt.event.KeyEvent.VK_DOWN)) {
            selectedIndex++;
            if (selectedIndex >= options.length) {
                selectedIndex = 0;
            }
        }

        if (input.isConfirmJustPressed()) {
            selectOption();
        }
    }

    private void selectOption() {
        switch (selectedIndex) {
            case 0: // NOVO JOGO
                stateManager.setState(GameState.INTRO_CUTSCENE);
                break;
            case 1: // CONTINUAR
                stateManager.setState(GameState.PLAYING);
                break;
            case 2: // CONTROLES
                showingControls = true;
                break;
            case 3: // VER HISTÓRIA
                stateManager.setState(GameState.INTRO_CUTSCENE);
                break;
            case 4: // SAIR
                System.exit(0);
                break;
        }
    }

    @Override
    public void render(Graphics2D g) {
        // Fundo de Vídeo Animado (Capa)
        if (videoPlayer != null && videoPlayer.isLoaded()) {
            BufferedImage frame = videoPlayer.getCurrentFrame();
            if (frame != null) {
                g.drawImage(frame, 0, 0, GameConfig.LOGICAL_WIDTH, GameConfig.LOGICAL_HEIGHT, null);
            }
            // Overlay de gradiente escuro para contraste do menu em cima do vídeo
            g.setColor(new Color(10, 10, 15, 140));
            g.fillRect(0, 0, GameConfig.LOGICAL_WIDTH, GameConfig.LOGICAL_HEIGHT);
        } else {
            // Fallback tela preta se o vídeo não carregou
            g.setColor(new Color(15, 15, 20));
            g.fillRect(0, 0, GameConfig.LOGICAL_WIDTH, GameConfig.LOGICAL_HEIGHT);
            
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.drawString("CARREGANDO VÍDEO...", 10, 20);
        }

        if (showingControls) {
            renderControlsModal(g);
            return;
        }

        // TÍTULO DO JOGO ESTILO METAL SLUG
        int titleY = 120;
        g.setFont(new Font("Impact", Font.BOLD, 64));
        String title = "METAL ZOMBIE";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        int titleX = (GameConfig.LOGICAL_WIDTH - titleWidth) / 2;

        // Sombra do título
        g.setColor(new Color(0, 0, 0, 240));
        g.drawString(title, titleX + 4, titleY + 4);

        // Contorno vermelho sangue
        g.setColor(GameConfig.COLOR_DANGER_RED);
        g.drawString(title, titleX + 2, titleY + 2);

        // Preenchimento amarelo arcade
        g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
        g.drawString(title, titleX, titleY);

        // Subtítulo
        g.setFont(new Font("Arial", Font.BOLD, 16));
        String subtitle = "A SOBREVIVÊNCIA DE ELIAS ROCHA — RUN & GUN 2D";
        int subWidth = g.getFontMetrics().stringWidth(subtitle);
        g.setColor(new Color(220, 220, 220));
        g.drawString(subtitle, (GameConfig.LOGICAL_WIDTH - subWidth) / 2, titleY + 36);

        // Frase de efeito do personagem
        g.setFont(new Font("SansSerif", Font.ITALIC, 14));
        String quote = "\"Enquanto essa casa estiver de pé, ainda existe alguém para salvar.\"";
        int quoteWidth = g.getFontMetrics().stringWidth(quote);
        g.setColor(new Color(180, 180, 180));
        g.drawString(quote, (GameConfig.LOGICAL_WIDTH - quoteWidth) / 2, titleY + 62);

        // OPÇÕES DO MENU
        int menuStartY = 270;
        int spacing = 45;
        g.setFont(new Font("Arial", Font.BOLD, 22));

        for (int i = 0; i < options.length; i++) {
            boolean selected = (i == selectedIndex);
            int optY = menuStartY + (i * spacing);
            String text = options[i];

            if (selected) {
                // Efeito pulsante de seleção
                float pulse = (float) (Math.sin(animTick * 0.15) * 0.2 + 0.8);
                g.setColor(new Color(1.0f, 0.8f * pulse, 0.0f));

                // Ícones de cursor estilo mira
                g.drawString("▶  " + text + "  ◀", (GameConfig.LOGICAL_WIDTH - g.getFontMetrics().stringWidth("▶  " + text + "  ◀")) / 2, optY);
            } else {
                g.setColor(new Color(170, 170, 170));
                g.drawString(text, (GameConfig.LOGICAL_WIDTH - g.getFontMetrics().stringWidth(text)) / 2, optY);
            }
        }

        // RODAPÉ COM DICAS
        g.setFont(GameConfig.FONT_UI);
        g.setColor(new Color(130, 130, 130));
        String footer = "[W/S] Navegar   [ENTER / ESPAÇO / J] Selecionar   [F11] Tela Cheia   [F3] Debug";
        g.drawString(footer, (GameConfig.LOGICAL_WIDTH - g.getFontMetrics().stringWidth(footer)) / 2, 510);
    }

    private void renderControlsModal(Graphics2D g) {
        int modalW = 820;
        int modalH = 420;
        int modalX = (GameConfig.LOGICAL_WIDTH - modalW) / 2;
        int modalY = (GameConfig.LOGICAL_HEIGHT - modalH) / 2;

        // Fundo da janela modal
        g.setColor(new Color(25, 25, 30, 245));
        g.fillRoundRect(modalX, modalY, modalW, modalH, 16, 16);
        g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
        g.drawRoundRect(modalX, modalY, modalW, modalH, 16, 16);

        // Cabeçalho
        g.setFont(new Font("Impact", Font.BOLD, 30));
        g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
        g.drawString("CONTROLES DE JOGO", modalX + 30, modalY + 45);

        // Lista de controles
        g.setFont(new Font("Monospaced", Font.BOLD, 16));
        g.setColor(Color.WHITE);

        String[][] controls = {
                {"A / D  ou  SETAS", "Mover Elias Rocha para a esquerda / direita"},
                {"W  ou  ESPAÇO", "Pular (altura variável)"},
                {"S  ou  SETA BAIXO", "Agachar (reduz hitbox pela metade e atira agachado)"},
                {"J", "Atirar com arma equipada (segurar = tiro automático)"},
                {"W + J  ou  CIMA + J", "Atirar para cima (alvos aéreos e obstáculos)"},
                {"K", "Ataque corpo a corpo com Chave Inglesa (dano alto)"},
                {"L", "Arremessar Granada / Molotov em arco parabólico"},
                {"1, 2, 3, 4", "Trocar arma: Pistola, Escopeta, Fuzil, Lança-chamas"},
                {"P  ou  ESC", "Pausar o jogo"}
        };

        int lineY = modalY + 85;
        for (String[] entry : controls) {
            g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
            g.drawString(String.format("%-20s", entry[0]), modalX + 35, lineY);
            g.setColor(new Color(230, 230, 230));
            g.drawString(entry[1], modalX + 230, lineY);
            lineY += 32;
        }

        // Rodapé da modal
        g.setFont(GameConfig.FONT_UI);
        g.setColor(GameConfig.COLOR_DANGER_RED);
        String closeHint = "Pressione [ENTER / J / ESC] para fechar";
        g.drawString(closeHint, (GameConfig.LOGICAL_WIDTH - g.getFontMetrics().stringWidth(closeHint)) / 2, modalY + modalH - 20);
    }

    @Override
    public void exit() {
    }

    private void renderCreditsModal(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, GameConfig.LOGICAL_WIDTH, GameConfig.LOGICAL_HEIGHT);

        try {
            BufferedImage cred = br.com.ultimoabrigo.assets.AssetLoader.loadImage("/assets/cutscenes/creditos.png");
            if (cred != null) {
                int drawX = (GameConfig.LOGICAL_WIDTH - cred.getWidth()) / 2;
                int drawY = (GameConfig.LOGICAL_HEIGHT - cred.getHeight()) / 2;
                g.drawImage(cred, drawX, drawY, null);
            }
        } catch (Exception e) {}

        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(Color.WHITE);
        String text = "[ESPAÇO] VOLTAR";
        g.drawString(text, (GameConfig.LOGICAL_WIDTH - g.getFontMetrics().stringWidth(text)) / 2, GameConfig.LOGICAL_HEIGHT - 30);
    }
}