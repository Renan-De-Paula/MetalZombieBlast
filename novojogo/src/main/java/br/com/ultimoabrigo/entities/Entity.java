package br.com.ultimoabrigo.entities;

import br.com.ultimoabrigo.util.AABB;

import java.awt.Graphics2D;

/**
 * Entity - Classe base para todas as entidades do mundo (jogador, inimigos, projéteis, pickups).
 */
public abstract class Entity {

    protected float x;
    protected float y;
    protected float vx;
    protected float vy;
    protected int width;
    protected int height;
    protected int facing = 1; // 1 = direita, -1 = esquerda
    protected boolean active = true;
    protected final AABB bounds;

    public Entity(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bounds = new AABB(x, y, width, height);
    }

    public abstract void update();

    public abstract void render(Graphics2D g, float cameraX, float cameraY);

    protected void updateBounds() {
        bounds.set(x, y, width, height);
    }

    public AABB getBounds() {
        return bounds;
    }

    public float getX() { return x; }
    public void setX(float x) { this.x = x; updateBounds(); }
    public float getY() { return y; }
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        updateBounds();
    }

    public float getVx() { return vx; }
    public float getVy() { return vy; }
    public void setVelocity(float vx, float vy) {
        this.vx = vx;
        this.vy = vy;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getFacing() { return facing; }
    public void setFacing(int facing) { this.facing = facing; }

    public boolean isActive() { return active; }
    public void destroy() { this.active = false; }
}
