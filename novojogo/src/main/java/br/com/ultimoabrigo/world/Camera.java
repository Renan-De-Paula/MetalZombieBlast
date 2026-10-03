package br.com.ultimoabrigo.world;

import br.com.ultimoabrigo.core.GameConfig;

/**
 * Camera - Controla o scroll lateral suave, clamp nas bordas do mundo de 32.000 px,
 * travamento na arena de chefe aos 1000 metros e efeito de tremor de tela (screen shake).
 */
public class Camera {

    private float x = 0;
    private float y = 0;
    private float targetX = 0;

    // Efeito de Tremor de Tela (Screen Shake)
    private int shakeTimer = 0;
    private float shakeIntensity = 0;
    private float shakeOffsetX = 0;
    private float shakeOffsetY = 0;

    // Travamento de arena
    private boolean lockedInArena = false;
    private float lockX = 0;

    public Camera() {
        reset();
    }

    public void reset() {
        this.x = 0;
        this.y = 0;
        this.targetX = 0;
        this.shakeTimer = 0;
        this.shakeIntensity = 0;
        this.shakeOffsetX = 0;
        this.shakeOffsetY = 0;
        this.lockedInArena = false;
    }

    public void update(float playerX) {
        if (lockedInArena) {
            // Câmera travada na arena fechada
            x = lockX;
        } else {
            // Posiciona o jogador a cerca de 280px da borda esquerda para ver o caminho à frente
            targetX = playerX - 280;

            // Clamping para não mostrar além de 0 ou da arena de 1000m
            if (targetX < 0) {
                targetX = 0;
            }
            float maxCamX = GameConfig.STAGE_WIDTH_PIXELS - GameConfig.LOGICAL_WIDTH;
            if (targetX > maxCamX) {
                targetX = maxCamX;
            }

            // Interpolação suave (lerp)
            x += (targetX - x) * 0.14f;
        }

        // Processa tremor de tela
        if (shakeTimer > 0) {
            shakeTimer--;
            shakeOffsetX = (float) ((Math.random() * 2.0 - 1.0) * shakeIntensity);
            shakeOffsetY = (float) ((Math.random() * 2.0 - 1.0) * shakeIntensity);
            shakeIntensity *= 0.92f;
        } else {
            shakeOffsetX = 0;
            shakeOffsetY = 0;
        }
    }

    /**
     * Dispara tremor de tela com intensidade e duração em frames.
     */
    public void shake(float intensity, int durationFrames) {
        this.shakeIntensity = Math.max(this.shakeIntensity, intensity);
        this.shakeTimer = Math.max(this.shakeTimer, durationFrames);
    }

    /**
     * Trava a câmera na arena do chefe (aos 1000 m).
     */
    public void lockAt(float arenaCameraX) {
        this.lockedInArena = true;
        this.lockX = arenaCameraX;
        this.x = arenaCameraX;
    }

    public void unlock() {
        this.lockedInArena = false;
    }

    public float getX() {
        return x + shakeOffsetX;
    }

    public float getY() {
        return y + shakeOffsetY;
    }

    public int getScreenIndex() {
        return (int) (x / GameConfig.LOGICAL_WIDTH);
    }

    public boolean isLockedInArena() {
        return lockedInArena;
    }
}
