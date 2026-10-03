package br.com.ultimoabrigo.assets;

import br.com.ultimoabrigo.core.GameConfig;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * AssetLoader - Gerencia o carregamento e cache em memória de texturas,
 * quadros de cutscenes, sprites e áudio.
 */
public class AssetLoader {

    private static final Map<String, BufferedImage> imageCache = new HashMap<>();

    public static BufferedImage loadImage(String path) {
        if (imageCache.containsKey(path)) {
            return imageCache.get(path);
        }

        BufferedImage img = null;
        try {
            // Tenta carregar do classpath (JAR / resources)
            InputStream is = AssetLoader.class.getResourceAsStream(path.startsWith("/") ? path : "/" + path);
            if (is != null) {
                img = ImageIO.read(is);
            } else {
                // Tenta carregar do sistema de arquivos direto
                File file = new File(path);
                if (file.exists()) {
                    img = ImageIO.read(file);
                }
            }
        } catch (Exception e) {
            System.err.println("[AssetLoader] Aviso ao carregar imagem: " + path + " -> " + e.getMessage());
        }

        if (img == null) {
            img = createPlaceholder(path);
        }

        imageCache.put(path, img);
        return img;
    }

    /**
     * Retorna um quadro de cutscene (1 a 6) do prólogo oficial.
     */
    public static BufferedImage getCutsceneFrame(int frameNumber) {
        String path = "/assets/cutscenes/quadro_" + frameNumber + ".png";
        return loadImage(path);
    }

    /**
     * Retorna a arte de referência oficial de Elias Rocha.
     */
    public static BufferedImage getCharacterReference() {
        return loadImage("/assets/cutscenes/character_ref.png");
    }

    /**
     * Cria uma imagem placeholder estilizada caso o arquivo de asset não seja encontrado.
     */
    public static BufferedImage createPlaceholder(String label) {
        BufferedImage placeholder = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = placeholder.createGraphics();
        g.setColor(new Color(60, 20, 20));
        g.fillRect(0, 0, 128, 128);
        g.setColor(GameConfig.COLOR_METAL_SLUG_YELLOW);
        g.drawRect(2, 2, 124, 124);
        g.setFont(GameConfig.FONT_UI);
        g.drawString("ASSET", 10, 30);
        String shortName = label.length() > 14 ? label.substring(label.length() - 14) : label;
        g.drawString(shortName, 10, 60);
        g.dispose();
        return placeholder;
    }
}
