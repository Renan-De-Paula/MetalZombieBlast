package br.com.ultimoabrigo.assets;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class VideoPlayer {

    private final List<BufferedImage> frames = new ArrayList<>();
    private volatile boolean fullyLoaded = false;
    private int currentFrame = 0;
    private int frameDelayCounter = 0;
    private final int updatesPerFrame; // Controla a velocidade de reprodução baseada no GameLoop (60 UPS)

    public VideoPlayer(String resourceFolder, int targetFps) {
        this.updatesPerFrame = 60 / targetFps; 
        
        // Carrega assincronamente os JPEGs super-rápidos
        new Thread(() -> loadVideo(resourceFolder), "Video-Decoder-Thread").start();
    }

    private void loadVideo(String resourceFolder) {
        System.out.println("Carregando frames otimizados para a memória: " + resourceFolder);
        try {
            int frameIndex = 1;
            while (true) {
                String frameName = String.format("%s/frame_%03d.jpg", resourceFolder, frameIndex);
                InputStream is = getClass().getResourceAsStream(frameName);
                if (is == null) {
                    break; // Não há mais frames
                }
                
                BufferedImage bimg = ImageIO.read(is);
                is.close();
                
                if (bimg != null) {
                    // Converter explicitamente para INT_RGB para garantir arraycopy super rápido 
                    // sem gastar CPU com conversão de ColorModel do JPG
                    BufferedImage optimized = new BufferedImage(bimg.getWidth(), bimg.getHeight(), BufferedImage.TYPE_INT_RGB);
                    optimized.getGraphics().drawImage(bimg, 0, 0, null);
                    optimized.getGraphics().dispose();
                    frames.add(optimized);
                }
                frameIndex++;
            }

            if (!frames.isEmpty()) {
                System.out.println("Vídeo carregado com sucesso. Total de frames otimizados: " + frames.size());
                fullyLoaded = true;
            } else {
                System.err.println("Nenhum frame encontrado em: " + resourceFolder);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar frames do vídeo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void update() {
        if (!fullyLoaded || frames.isEmpty()) return;

        frameDelayCounter++;
        if (frameDelayCounter >= updatesPerFrame) {
            frameDelayCounter = 0;
            currentFrame++;
            if (currentFrame >= frames.size()) {
                currentFrame = 0; // Loop infinito
            }
        }
    }

    public BufferedImage getCurrentFrame() {
        if (!fullyLoaded || frames.isEmpty()) return null;
        return frames.get(currentFrame);
    }

    public boolean isLoaded() {
        return fullyLoaded;
    }
}
