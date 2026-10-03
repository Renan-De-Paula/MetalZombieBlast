package br.com.ultimoabrigo.entities;

import br.com.ultimoabrigo.assets.SoundSystem;
import br.com.ultimoabrigo.core.GameConfig;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * Pickup - Itens coletáveis deixados por zumbis ou encontrados pelo mapa:
 * caixa de munição, kit médico, granadas e peças de metal (pontos).
 */
public class Pickup extends Entity {

    public enum PickupType {
        AMMO_BOX("MUNIÇÃO"),
        MEDKIT("VIDA"),
        GRENADE_PACK("GRANADAS"),
        SCRAP_METAL("PEÇAS"),
        SHOTGUN_WEAPON("ESCOPETA"),
        RIFLE_WEAPON("FUZIL"),
        FLAMETHROWER_WEAPON("LANÇA-CHAMAS");

        private final String label;

        PickupType(String label) {
            this.label = label;
        }

        public String getLabel() { return label; }
    }

    private final PickupType type;
    private int animTick = 0;
    private float initialY;

    public Pickup(float x, float y, PickupType type) {
        super(x, y, 26, 26);
        this.type = type;
        this.initialY = y;
    }

    @Override
    public void update() {
        animTick++;
        // Efeito sutil de flutuação estilo arcade
        y = initialY + (float) Math.sin(animTick * 0.1) * 4.0f;
        updateBounds();
    }

    public void applyToPlayer(Player player) {
        switch (type) {
            case AMMO_BOX:
                player.refillAmmo();
                break;
            case MEDKIT:
                player.heal(1);
                break;
            case GRENADE_PACK:
                player.addGrenades(2);
                break;
            case SCRAP_METAL:
                player.addScore(250);
                break;
            case SHOTGUN_WEAPON:
            case RIFLE_WEAPON:
            case FLAMETHROWER_WEAPON:
                // Tratado no PlayingState
                return;
        }
        SoundSystem.playPickup();
        destroy();
    }

    @Override
    public void render(Graphics2D g, float cameraX, float cameraY) {
        int drawX = (int) (x - cameraX);
        int drawY = (int) (y - cameraY);

        // Brilho pulsante
        int glow = (int) (Math.sin(animTick * 0.15) * 40 + 200);

        switch (type) {
            case AMMO_BOX:
                java.awt.image.BufferedImage ammoImg = br.com.ultimoabrigo.assets.AssetLoader.loadImage("/assets/items/ammo.png");
                g.drawImage(ammoImg, drawX - 4, drawY - 4, width + 8, height + 8, null); // Render a bit bigger than hitbox
                break;

            case MEDKIT:
                java.awt.image.BufferedImage medkitImg = br.com.ultimoabrigo.assets.AssetLoader.loadImage("/assets/items/medkit.png");
                g.drawImage(medkitImg, drawX - 4, drawY - 4, width + 8, height + 8, null);
                break;

            case GRENADE_PACK:
                java.awt.image.BufferedImage grenadeImg = br.com.ultimoabrigo.assets.AssetLoader.loadImage("/assets/items/grenade.png");
                g.drawImage(grenadeImg, drawX - 4, drawY - 4, width + 8, height + 8, null);
                break;

            case SCRAP_METAL:
                java.awt.image.BufferedImage scrapImg = br.com.ultimoabrigo.assets.AssetLoader.loadImage("/assets/items/scrap.png");
                g.drawImage(scrapImg, drawX - 4, drawY - 4, width + 8, height + 8, null);
                break;
            case SHOTGUN_WEAPON:
            case RIFLE_WEAPON:
            case FLAMETHROWER_WEAPON:
                g.setColor(new java.awt.Color(40, 40, 50));
                g.fillRect(drawX, drawY, width, height);
                g.setColor(br.com.ultimoabrigo.core.GameConfig.COLOR_METAL_SLUG_YELLOW);
                g.drawRect(drawX, drawY, width, height);
                g.setFont(new java.awt.Font("Impact", java.awt.Font.PLAIN, 12));
                g.drawString("WEAPON", drawX + 2, drawY + 14);
                break;
        }
    }

    

    public PickupType getType() { return type; }
}
