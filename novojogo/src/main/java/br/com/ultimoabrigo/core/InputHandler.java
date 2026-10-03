package br.com.ultimoabrigo.core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

/**
 * InputHandler - Gerenciador completo de teclado com suporte a teclas contínuas
 * e detecção de borda de subida (just pressed) por tick.
 */
public class InputHandler implements KeyListener {

    private static final int KEY_COUNT = 512;
    private final boolean[] keys = new boolean[KEY_COUNT];
    private final boolean[] justPressed = new boolean[KEY_COUNT];
    private final boolean[] justReleased = new boolean[KEY_COUNT];
    private final boolean[] keyStateCache = new boolean[KEY_COUNT];

    public InputHandler() {
        reset();
    }

    /**
     * Atualiza o estado de borda no início de cada tick do game loop.
     */
    public synchronized void update() {
        for (int i = 0; i < KEY_COUNT; i++) {
            justPressed[i] = keys[i] && !keyStateCache[i];
            justReleased[i] = !keys[i] && keyStateCache[i];
            keyStateCache[i] = keys[i];
        }
    }

    public synchronized void reset() {
        Arrays.fill(keys, false);
        Arrays.fill(justPressed, false);
        Arrays.fill(justReleased, false);
        Arrays.fill(keyStateCache, false);
    }

    public synchronized boolean isKeyDown(int keyCode) {
        if (keyCode >= 0 && keyCode < KEY_COUNT) {
            return keys[keyCode];
        }
        return false;
    }

    public synchronized boolean isKeyJustPressed(int keyCode) {
        if (keyCode >= 0 && keyCode < KEY_COUNT) {
            return justPressed[keyCode];
        }
        return false;
    }

    public synchronized boolean isKeyJustReleased(int keyCode) {
        if (keyCode >= 0 && keyCode < KEY_COUNT) {
            return justReleased[keyCode];
        }
        return false;
    }

    // --- ATALHOS DE CONTROLE DO JOGO ---
    public boolean isLeft() {
        return isKeyDown(KeyEvent.VK_A) || isKeyDown(KeyEvent.VK_LEFT);
    }

    public boolean isRight() {
        return isKeyDown(KeyEvent.VK_D) || isKeyDown(KeyEvent.VK_RIGHT);
    }

    public boolean isUp() {
        return isKeyDown(KeyEvent.VK_W) || isKeyDown(KeyEvent.VK_UP);
    }

    public boolean isCrouch() {
        return isKeyDown(KeyEvent.VK_S) || isKeyDown(KeyEvent.VK_DOWN);
    }

    public boolean isJump() {
        return isKeyDown(KeyEvent.VK_SPACE) || isKeyDown(KeyEvent.VK_W);
    }

    public boolean isJumpJustPressed() {
        return isKeyJustPressed(KeyEvent.VK_SPACE) || isKeyJustPressed(KeyEvent.VK_W);
    }

    public boolean isShoot() {
        return isKeyDown(KeyEvent.VK_J);
    }

    public boolean isShootJustPressed() {
        return isKeyJustPressed(KeyEvent.VK_J);
    }

    public boolean isMeleeJustPressed() {
        return isKeyJustPressed(KeyEvent.VK_K);
    }

    public boolean isGrenadeJustPressed() {
        return isKeyJustPressed(KeyEvent.VK_L);
    }

    public boolean isPauseJustPressed() {
        return isKeyJustPressed(KeyEvent.VK_P) || isKeyJustPressed(KeyEvent.VK_ESCAPE);
    }

    public boolean isConfirmJustPressed() {
        return isKeyJustPressed(KeyEvent.VK_ENTER) || isKeyJustPressed(KeyEvent.VK_SPACE) || isKeyJustPressed(KeyEvent.VK_J);
    }

    public boolean isFullscreenJustPressed() {
        return isKeyJustPressed(KeyEvent.VK_F11);
    }

    public boolean isDebugJustPressed() {
        return isKeyJustPressed(KeyEvent.VK_F3);
    }

    public boolean isWeaponSlot1() { return isKeyJustPressed(KeyEvent.VK_1); }
    public boolean isWeaponSlot2() { return isKeyJustPressed(KeyEvent.VK_2); }
    public boolean isWeaponSlot3() { return isKeyJustPressed(KeyEvent.VK_3); }
    public boolean isWeaponSlot4() { return isKeyJustPressed(KeyEvent.VK_4); }

    // --- IMPLEMENTAÇÃO KEYLISTENER ---
    @Override
    public synchronized void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code >= 0 && code < KEY_COUNT) {
            keys[code] = true;
        }
    }

    @Override
    public synchronized void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code >= 0 && code < KEY_COUNT) {
            keys[code] = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Não utilizado para controles contínuos
    }
}
