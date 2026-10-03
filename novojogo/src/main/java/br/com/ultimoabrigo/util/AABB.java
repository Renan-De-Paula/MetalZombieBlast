package br.com.ultimoabrigo.util;

/**
 * AABB - Caixa delimitadora alinhada aos eixos para detecção precisa de colisões e hitboxes.
 */
public class AABB {
    public float x;
    public float y;
    public float width;
    public float height;

    public AABB(float x, float y, float width, float height) {
        set(x, y, width, height);
    }

    public void set(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean intersects(AABB other) {
        if (other == null) return false;
        return this.x < other.x + other.width &&
               this.x + this.width > other.x &&
               this.y < other.y + other.height &&
               this.y + this.height > other.y;
    }

    public boolean contains(float px, float py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }

    public float getCenterX() {
        return x + width * 0.5f;
    }

    public float getCenterY() {
        return y + height * 0.5f;
    }
}
