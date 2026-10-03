package br.com.ultimoabrigo.assets;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * SoundSystem - Gerador e sintetizador de áudio procedural arcade (Java Sound puro).
 * Garante efeitos sonoros retro para todas as armas, impactos, explosões, pickups e zumbis
 * sem depender de bibliotecas externas ou arquivos de terceiros.
 */
public class SoundSystem {

    private static final int SAMPLE_RATE = 22050;
    private static final ExecutorService soundPool = Executors.newFixedThreadPool(4);
    private static boolean muted = false;
    private static final Random random = new Random();

    public static void toggleMute() {
        muted = !muted;
    }

    public static boolean isMuted() {
        return muted;
    }

    private static void playToneAsync(byte[] buffer) {
        if (muted) return;
        soundPool.submit(() -> {
            try {
                AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, false, false);
                SourceDataLine line = AudioSystem.getSourceDataLine(format);
                line.open(format, buffer.length);
                line.start();
                line.write(buffer, 0, buffer.length);
                line.drain();
                line.close();
            } catch (Exception ignored) {
            }
        });
    }

    /**
     * Tiro de pistola: estalo seco e rápido.
     */
    public static void playPistolShot() {
        int length = SAMPLE_RATE / 12; // ~80ms
        byte[] buf = new byte[length];
        for (int i = 0; i < length; i++) {
            float progress = (float) i / length;
            float env = 1.0f - progress;
            float noise = (random.nextFloat() * 2.0f - 1.0f) * 0.7f;
            float freq = 600.0f - progress * 400.0f;
            float tone = (float) Math.sin(2.0 * Math.PI * freq * i / SAMPLE_RATE);
            buf[i] = (byte) (128 + ((noise * 0.6f + tone * 0.4f) * env * 110));
        }
        playToneAsync(buf);
    }

    /**
     * Tiro de escopeta: explosão pesada com grave encorpado.
     */
    public static void playShotgunShot() {
        int length = SAMPLE_RATE / 5; // ~200ms
        byte[] buf = new byte[length];
        for (int i = 0; i < length; i++) {
            float progress = (float) i / length;
            float env = (float) Math.pow(1.0f - progress, 1.8);
            float noise = (random.nextFloat() * 2.0f - 1.0f);
            float bass = (float) Math.sin(2.0 * Math.PI * (160.0f - progress * 110.0f) * i / SAMPLE_RATE);
            buf[i] = (byte) (128 + ((noise * 0.7f + bass * 0.8f) * env * 120));
        }
        playToneAsync(buf);
    }

    /**
     * Tiro de fuzil automático: estampido metálico e rápido.
     */
    public static void playRifleShot() {
        int length = SAMPLE_RATE / 16; // ~60ms
        byte[] buf = new byte[length];
        for (int i = 0; i < length; i++) {
            float progress = (float) i / length;
            float env = 1.0f - progress;
            float noise = (random.nextFloat() * 2.0f - 1.0f) * 0.8f;
            float tone = (float) Math.sin(2.0 * Math.PI * 450.0f * i / SAMPLE_RATE);
            buf[i] = (byte) (128 + ((noise * 0.5f + tone * 0.5f) * env * 115));
        }
        playToneAsync(buf);
    }

    /**
     * Lança-chamas: rugido contínuo e abafado.
     */
    public static void playFlameHiss() {
        int length = SAMPLE_RATE / 10;
        byte[] buf = new byte[length];
        float prev = 0;
        for (int i = 0; i < length; i++) {
            float raw = random.nextFloat() * 2.0f - 1.0f;
            prev = prev * 0.8f + raw * 0.2f;
            buf[i] = (byte) (128 + (prev * 85));
        }
        playToneAsync(buf);
    }

    /**
     * Golpe corpo a corpo com Chave Inglesa: pancada contundente e metálica.
     */
    public static void playWrenchHit() {
        int length = SAMPLE_RATE / 7;
        byte[] buf = new byte[length];
        for (int i = 0; i < length; i++) {
            float progress = (float) i / length;
            float env = (float) Math.pow(1.0f - progress, 2.0);
            float clang = (float) (Math.sin(2.0 * Math.PI * 340.0f * i / SAMPLE_RATE)
                    + 0.5f * Math.sin(2.0 * Math.PI * 680.0f * i / SAMPLE_RATE));
            float thud = (float) Math.sin(2.0 * Math.PI * (120.0f - progress * 60.0f) * i / SAMPLE_RATE);
            buf[i] = (byte) (128 + ((clang * 0.6f + thud * 0.8f) * env * 120));
        }
        playToneAsync(buf);
    }

    /**
     * Explosão de granada ou barril: estrondo colossal com reverberação grave.
     */
    public static void playExplosion() {
        int length = (int) (SAMPLE_RATE * 0.6); // ~600ms
        byte[] buf = new byte[length];
        for (int i = 0; i < length; i++) {
            float progress = (float) i / length;
            float env = (float) Math.pow(1.0f - progress, 1.5);
            float noise = (random.nextFloat() * 2.0f - 1.0f);
            float rumble = (float) Math.sin(2.0 * Math.PI * (80.0f - progress * 50.0f) * i / SAMPLE_RATE);
            buf[i] = (byte) (128 + ((noise * 0.6f + rumble * 0.8f) * env * 125));
        }
        playToneAsync(buf);
    }

    /**
     * Pulo do jogador: efeito rápido ascendente.
     */
    public static void playJump() {
        int length = SAMPLE_RATE / 9;
        byte[] buf = new byte[length];
        for (int i = 0; i < length; i++) {
            float progress = (float) i / length;
            float env = 1.0f - progress;
            float freq = 200.0f + progress * 400.0f;
            float tone = (float) Math.sin(2.0 * Math.PI * freq * i / SAMPLE_RATE);
            buf[i] = (byte) (128 + (tone * env * 85));
        }
        playToneAsync(buf);
    }

    /**
     * Dano sofrido por Elias: gemido de impacto com recuo.
     */
    public static void playPlayerHurt() {
        int length = SAMPLE_RATE / 5;
        byte[] buf = new byte[length];
        for (int i = 0; i < length; i++) {
            float progress = (float) i / length;
            float env = 1.0f - progress;
            float freq = 220.0f - progress * 90.0f;
            float tone = (float) Math.sin(2.0 * Math.PI * freq * i / SAMPLE_RATE);
            float grit = (random.nextFloat() * 2.0f - 1.0f) * 0.3f;
            buf[i] = (byte) (128 + ((tone + grit) * env * 105));
        }
        playToneAsync(buf);
    }

    /**
     * Coleta de item (munição, vida, granadas): sinos ascendentes agradáveis.
     */
    public static void playPickup() {
        int length = SAMPLE_RATE / 6;
        byte[] buf = new byte[length];
        for (int i = 0; i < length; i++) {
            float progress = (float) i / length;
            float env = 1.0f - progress;
            float freq = progress < 0.5f ? 523.25f : 659.25f;
            float tone = (float) Math.sin(2.0 * Math.PI * freq * i / SAMPLE_RATE);
            buf[i] = (byte) (128 + (tone * env * 90));
        }
        playToneAsync(buf);
    }

    /**
     * Alarme / Sirene de perigo ao encontrar Boss.
     */
    public static void playBossAlarm() {
        int length = SAMPLE_RATE / 3;
        byte[] buf = new byte[length];
        for (int i = 0; i < length; i++) {
            float progress = (float) i / length;
            float freq = (progress < 0.5f) ? 880.0f : 660.0f;
            float tone = (float) Math.sin(2.0 * Math.PI * freq * i / SAMPLE_RATE);
            buf[i] = (byte) (128 + (tone * 100));
        }
        playToneAsync(buf);
    }

    /**
     * Rosnado gutural do Zumbi Caminhante (Walker).
     */
    public static void playZombieGroan() {
        int length = SAMPLE_RATE / 4;
        byte[] buf = new byte[length];
        for (int i = 0; i < length; i++) {
            float progress = (float) i / length;
            float env = (float) Math.sin(progress * Math.PI);
            float freq = 85.0f + (float) Math.sin(i * 0.05) * 15.0f;
            float tone = (float) Math.sin(2.0 * Math.PI * freq * i / SAMPLE_RATE);
            float grit = (random.nextFloat() * 2.0f - 1.0f) * 0.4f;
            buf[i] = (byte) (128 + ((tone + grit) * env * 95));
        }
        playToneAsync(buf);
    }

    /**
     * Grito feroz e sibilante do Zumbi Corredor (Runner) telegrafando o salto.
     */
    public static void playRunnerScreech() {
        int length = SAMPLE_RATE / 6;
        byte[] buf = new byte[length];
        for (int i = 0; i < length; i++) {
            float progress = (float) i / length;
            float env = 1.0f - progress;
            float freq = 650.0f + progress * 500.0f;
            float tone = (float) Math.sin(2.0 * Math.PI * freq * i / SAMPLE_RATE);
            float hiss = (random.nextFloat() * 2.0f - 1.0f) * 0.5f;
            buf[i] = (byte) (128 + ((tone * 0.6f + hiss * 0.4f) * env * 110));
        }
        playToneAsync(buf);
    }

    /**
     * Dano e impacto de tiro no zumbi (sangue e carne).
     */
    public static void playZombieHit() {
        int length = SAMPLE_RATE / 14;
        byte[] buf = new byte[length];
        for (int i = 0; i < length; i++) {
            float progress = (float) i / length;
            float env = 1.0f - progress;
            float squish = (random.nextFloat() * 2.0f - 1.0f) * 0.8f;
            float thud = (float) Math.sin(2.0 * Math.PI * 110.0f * i / SAMPLE_RATE);
            buf[i] = (byte) (128 + ((squish * 0.7f + thud * 0.3f) * env * 115));
        }
        playToneAsync(buf);
    }

    /**
     * Morte do zumbi: desmoronamento visceral.
     */
    public static void playZombieDeath() {
        int length = SAMPLE_RATE / 5;
        byte[] buf = new byte[length];
        for (int i = 0; i < length; i++) {
            float progress = (float) i / length;
            float env = (float) Math.pow(1.0f - progress, 1.4);
            float freq = 130.0f - progress * 70.0f;
            float tone = (float) Math.sin(2.0 * Math.PI * freq * i / SAMPLE_RATE);
            float gore = (random.nextFloat() * 2.0f - 1.0f) * 0.5f;
            buf[i] = (byte) (128 + ((tone + gore) * env * 120));
        }
        playToneAsync(buf);
    }
}
