import Phaser from 'phaser';

export default class Enemy extends Phaser.GameObjects.Sprite {
    constructor(scene, x, y, type = 'basic') {
        let textureKey = 'zombie_' + type;
        if (type === 'miniboss' || type === 'boss') {
            textureKey = 'zombie_brute';
        }
        
        super(scene, x, y, textureKey);
        
        scene.add.existing(this);
        scene.physics.add.existing(this);
        
        this.type = type;
        
        // Atributos base
        if (type === 'basic') {
            this.health = 20;
            this.speed = 90;
            this.setScale(0.12);
            this.expValue = 1; // XP drop
        } else if (type === 'runner') {
            this.health = 15;
            this.speed = 150;
            this.setScale(0.12);
            this.setTint(0xffcccc);
            this.expValue = 2;
        } else if (type === 'brute') {
            this.health = 80;
            this.speed = 50;
            this.setScale(0.15);
            this.setTint(0xcccccc);
            this.expValue = 5;
        } else {
            this.health = 200;
            this.speed = 60;
            this.setScale(0.2);
            this.setTint(0xffaa00);
            this.expValue = 20;
        }
        
        this.body.setSize(150, 150);
        this.body.setOffset(420, 420);
        this.body.setCollideWorldBounds(true); 
    }

    update() {
        if (this.health <= 0) {
            this.die();
            return;
        }

        const player = this.scene.player;
        if (player && player.active) {
            // IA simples: ir na direção do jogador
            this.scene.physics.moveToObject(this, player, this.speed);
            this.flipX = this.body.velocity.x < 0;
        } else {
            this.body.setVelocity(0, 0);
        }

        this.setDepth(this.y);
    }

    takeDamage(amount) {
        this.health -= amount;
        
        // Mostrar número de dano voador (Damage Text)
        this.scene.showDamageText(this.x, this.y - 20, amount);
        
        this.setTintFill(0xffffff);
        this.scene.time.delayedCall(50, () => {
            if (this.active) {
                if (this.type === 'runner') this.setTint(0xffcccc);
                else if (this.type === 'brute') this.setTint(0xcccccc);
                else if (this.type === 'miniboss' || this.type === 'boss') this.setTint(0xffaa00);
                else this.clearTint();
            }
        });
    }

    die() {
        // Efeito de morte
        const emitter = this.scene.add.particles(this.x, this.y, 'bullet', {
            speed: { min: -100, max: 100 },
            angle: { min: 0, max: 360 },
            scale: { start: 0.5, end: 0 },
            lifespan: 300,
            tint: 0x550000
        });
        emitter.explode(5);
        
        // Spawnar Gem (Experiência)
        this.scene.spawnGem(this.x, this.y, this.expValue);
        
        this.scene.killCount++;
        this.scene.killsText.setText(`Kills: ${this.scene.killCount}`);
        
        this.destroy();
    }
}
