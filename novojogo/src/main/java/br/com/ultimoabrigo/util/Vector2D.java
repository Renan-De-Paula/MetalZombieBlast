package br.com.ultimoabrigo.util;

/**
 * Vector2D - Vetor bidimensional para posição, velocidade e aceleração.
 */
public class Vector2D {
    public float x;
    public float y;

    public Vector2D() {
        this(0, 0);
    }

    public Vector2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2D add(float dx, float dy) {
        this.x += dx;
        this.y += dy;
        return this;
    }

    public Vector2D add(Vector2D other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    public Vector2D multiply(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public Vector2D normalize() {
        float len = length();
        if (len > 0.0001f) {
            x /= len;
            y /= len;
        }
        return this;
    }
}
