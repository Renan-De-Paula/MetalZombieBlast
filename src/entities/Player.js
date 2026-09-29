import Phaser from 'phaser';

export default class Player extends Phaser.GameObjects.Sprite {
    constructor(scene, x, y) {
        super(scene, x, y, 'player'); 
        scene.add.existing(this);
        scene.physics.add.existing(this);
        
        this.setScale(0.15); // Tamanho pequeno para o estilo Vampire Survivors
        
        // Hitbox menor
        this.body.setSize(150, 150);
        this.body.setOffset(420, 420);
        this.body.setCollideWorldBounds(true); 

        // Atributos base do estilo Survivor
        this.stats = {
            speed: 200,
            maxHealth: 100,
            health: 100,
            magnetRadius: 100,
            damage: 15,
            attackCooldown: 1000, // ms
            projectiles: 1, // quantos tiros por vez
            projectileSpeed: 400
        };
        
        this.isInvulnerable = false;
        this.lastAttackTime = 0;
    }

    update(cursors, time) {
        let velocityX = 0;
        let velocityY = 0;

        // WASD ou Setas
        if (cursors.left.isDown || (cursors.scene && cursors.scene.input.keyboard.addKey('A').isDown)) {
            velocityX = -this.stats.speed;
            this.flipX = true;
        } else if (cursors.right.isDown || (cursors.scene && cursors.scene.input.keyboard.addKey('D').isDown)) {
            velocityX = this.stats.speed;
            this.flipX = false;
        }

        if (cursors.up.isDown || (cursors.scene && cursors.scene.input.keyboard.addKey('W').isDown)) {
            velocityY = -this.stats.speed;
        } else if (cursors.down.isDown || (cursors.scene && cursors.scene.input.keyboard.addKey('S').isDown)) {
            velocityY = this.stats.speed;
        }

        if (velocityX !== 0 && velocityY !== 0) {
            velocityX *= 0.7071;
            velocityY *= 0.7071;
        }

        this.body.setVelocity(velocityX, velocityY);
        this.setDepth(this.y);
    }
    
    takeDamage(amount) {
        if (this.isInvulnerable) return;
        
        this.stats.health -= amount;
        this.isInvulnerable = true;
        
        this.scene.updateHealthBar();
        
        this.scene.tweens.add({
            targets: this,
            alpha: 0.3,
            duration: 100,
            yoyo: true,
            repeat: 3,
            onComplete: () => {
                this.alpha = 1;
                this.isInvulnerable = false;
            }
        });
        
        if (this.stats.health <= 0) {
            this.scene.gameOver();
        }
    }
}
