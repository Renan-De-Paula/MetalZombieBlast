package br.com.ultimoabrigo.core;

import java.awt.Color;
import java.awt.Font;

/**
 * GameConfig - Centraliza todas as constantes de balanceamento,
 * dimensões lógicas, física, áudio e controles do jogo "METAL ZOMBIE".
 */
public final class GameConfig {

    private GameConfig() {
        // Construtor privado para classe utilitária
    }

    // --- IDENTIFICAÇÃO E JANELA ---
    public static final String GAME_TITLE = "METAL ZOMBIE — Run & Gun 2D";
    public static final String GAME_VERSION = "1.0.0";

    // Resolução lógica interna (16:9) estilo arcade
    public static final int LOGICAL_WIDTH = 960;
    public static final int LOGICAL_HEIGHT = 540;

    // Resolução inicial da janela no desktop
    public static final int DEFAULT_WINDOW_WIDTH = 1280;
    public static final int DEFAULT_WINDOW_HEIGHT = 720;

    // Game loop fixo
    public static final int TARGET_UPS = 60;
    public static final double TIME_STEP = 1.0 / TARGET_UPS;
    public static final long TIME_STEP_NANOS = 1_000_000_000L / TARGET_UPS;

    // --- MUNDO E MÉTRICAS ---
    public static final int TILE_SIZE = 32;
    public static final int PIXELS_PER_METER = 32; // 1 metro = 32 pixels
    public static final int METERS_PER_PHASE = 1000; // Cada fase tem exatamente 1000 metros
    public static final int STAGE_WIDTH_PIXELS = METERS_PER_PHASE * PIXELS_PER_METER; // 32.000 pixels
    public static final int CHECKPOINT_INTERVAL_METERS = 250; // Checkpoints a cada 250 m
    public static final int GROUND_Y = 460; // Nível do chão padrão

    // --- JOGADOR (ELIAS ROCHA) ---
    public static final int PLAYER_MAX_HEARTS = 5;
    public static final int PLAYER_INITIAL_LIVES = 3;
    public static final int PLAYER_INVINCIBILITY_FRAMES = 90; // 1.5 segundos a 60 UPS
    public static final float PLAYER_WALK_SPEED = 3.6f;
    public static final float PLAYER_JUMP_FORCE = -11.5f;
    public static final float GRAVITY = 0.52f;
    public static final float MAX_FALL_SPEED = 12.0f;

    // Dimensões do jogador
    public static final int PLAYER_WIDTH = 48;
    public static final int PLAYER_HEIGHT = 72;
    public static final int PLAYER_CROUCH_HEIGHT = 44;

    // Chave Inglesa (Ataque Melee - K)
    public static final int MELEE_DAMAGE = 5;
    public static final int MELEE_RANGE = 54;
    public static final int MELEE_COOLDOWN_FRAMES = 24;

    // Granada / Molotov (L)
    public static final int GRENADE_INITIAL_AMMO = 3;
    public static final int GRENADE_MAX_AMMO = 10;
    public static final int GRENADE_EXPLOSION_RADIUS = 110;
    public static final int GRENADE_DAMAGE = 8;

    // Armas de fogo
    public static final int PISTOL_DAMAGE = 1;
    public static final int PISTOL_FIRE_RATE_FRAMES = 14;

    public static final int SHOTGUN_DAMAGE_PER_PELLET = 2;
    public static final int SHOTGUN_PELLETS = 5;
    public static final int SHOTGUN_FIRE_RATE_FRAMES = 32;
    public static final int SHOTGUN_INITIAL_AMMO = 25;

    public static final int RIFLE_DAMAGE = 2;
    public static final int RIFLE_FIRE_RATE_FRAMES = 7;
    public static final int RIFLE_INITIAL_AMMO = 90;

    public static final int FLAMETHROWER_DAMAGE = 1;
    public static final int FLAMETHROWER_FIRE_RATE_FRAMES = 3;
    public static final int FLAMETHROWER_INITIAL_AMMO = 150;

    // --- INIMIGOS COMUNS ---
    // Zumbi Caminhante (Walker)
    public static final int WALKER_HP = 6;
    public static final float WALKER_BASE_SPEED = 1.3f;
    public static final int WALKER_SCORE = 100;
    public static final int WALKER_WIDTH = 42;
    public static final int WALKER_HEIGHT = 68;

    // Zumbi Corredor (Runner)
    public static final int RUNNER_HP = 4;
    public static final float RUNNER_BASE_SPEED = 3.8f;
    public static final int RUNNER_SCORE = 150;
    public static final int RUNNER_WIDTH = 40;
    public static final int RUNNER_HEIGHT = 64;
        public static final int RUNNER_TELEGRAPH_FRAMES = 18; // 0.3 segundos telegrafando o salto

    // Zumbi Fortão (Tank)
    public static final int TANK_HP = 12;
    public static final float TANK_BASE_SPEED = 0.8f;
    public static final int TANK_SCORE = 300;
    public static final int TANK_WIDTH = 60;
    public static final int TANK_HEIGHT = 76;

    // --- CORES DE TEMA ARCADE ---
    public static final Color COLOR_HUD_TEXT = new Color(245, 245, 235);
    public static final Color COLOR_HUD_BG = new Color(20, 20, 25, 210);
    public static final Color COLOR_BLOOD_DARK = new Color(130, 15, 20);
    public static final Color COLOR_BLOOD_BRIGHT = new Color(195, 30, 35);
    public static final Color COLOR_MUZZLE_FLASH = new Color(255, 220, 80);
    public static final Color COLOR_METAL_SLUG_YELLOW = new Color(255, 204, 0);
    public static final Color COLOR_DANGER_RED = new Color(230, 40, 40);

    // Fontes
    public static final Font FONT_TITLE = new Font("Impact", Font.BOLD, 46);
    public static final Font FONT_HEADER = new Font("Arial", Font.BOLD, 22);
    public static final Font FONT_UI = new Font("Monospaced", Font.BOLD, 15);
    public static final Font FONT_DIALOGUE = new Font("SansSerif", Font.PLAIN, 18);
}
