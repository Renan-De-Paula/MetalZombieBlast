package br.com.ultimoabrigo.entities;

/**
 * Tipos de armas disponíveis para Elias Rocha.
 */
public enum WeaponType {
    PISTOL("PISTOLA", "Munição Infinita", -1, 14, 1),
    SHOTGUN("ESCOPETA", "Disparo em Leque", 25, 32, 2),
    RIFLE("FUZIL AUTO", "Alta Cadência", 90, 7, 2),
    FLAMETHROWER("LANÇA-CHAMAS", "Dano Contínuo", 150, 3, 1);

    private final String displayName;
    private final String description;
    private final int defaultAmmo;
    private final int fireRateFrames;
    private final int damage;

    WeaponType(String displayName, String description, int defaultAmmo, int fireRateFrames, int damage) {
        this.displayName = displayName;
        this.description = description;
        this.defaultAmmo = defaultAmmo;
        this.fireRateFrames = fireRateFrames;
        this.damage = damage;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public int getDefaultAmmo() { return defaultAmmo; }
    public int getFireRateFrames() { return fireRateFrames; }
    public int getDamage() { return damage; }
}
