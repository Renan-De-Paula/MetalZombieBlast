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
        this.coinReward = 10;
        
        if (type === 'basic') {
            this.health = 30;
            this.speed = 60;
            this.setScale(0.1);
        } else if (type === 'runner') {
            this.health = 15;
            this.speed = 150;
            this.setScale(0.1);
            this.setTint(0xffcccc);
        } else if (type === 'brute') {
            this.health = 100;
            this.speed = 30;
            this.setScale(0.12);
            this.setTint(0xcccccc);
        } else if (type === 'miniboss') {
            this.health = 300;
            this.speed = 40;
            this.setScale(0.15);
            this.setTint(0xffaa00); // Dourado/Laranja
            this.coinReward = 25;
        } else if (type === 'boss') {
            this.health = 1500;
            this.speed = 45;
            this.setScale(0.25);
            this.setTint(0xff0000); // Vermelho
            this.coinReward = 100;
        }
        
        this.body.setSize(400, 800);
        this.body.setOffset(312, 224);
        
        this.body.setBounce(0.1);
        this.body.setCollideWorldBounds(false); 
        
        this.direction = 1;
    }

    update() {
        if (this.y > 800) {
            this.destroy();
            return;
        }

        if (this.health <= 0) {
            const emitter = this.scene.add.particles(this.x, this.y, 'bullet', {
                speed: { min: -200, max: 200 },
                angle: { min: 0, max: 360 },
                scale: { start: 1, end: 0 },
                lifespan: 300,
                gravityY: 200,
                tint: 0xff0000
            });
            emitter.explode(10);
            
            // Adicionar moedas
            this.scene.coinsScore += this.coinReward;
            if (this.scene.coinsText) {
                this.scene.coinsText.setText('Moedas de Ouro: ' + this.scene.coinsScore);
                this.scene.cameras.main.flash(50, 255, 215, 0);
            }
            
            this.destroy();
            return;
        }

        // IA: Andar na direção do jogador
        const player = this.scene.player;
        if (player && player.active) {
            if (player.x < this.x) {
                this.direction = -1;
                this.flipX = true;
            } else {
                this.direction = 1;
                this.flipX = false;
            }
        }

        this.body.setVelocityX(this.speed * this.direction);
    }

    takeDamage(amount) {
        this.health -= amount;
        
        // Flash de dano
        this.setTintFill(0xffffff);
        this.scene.time.delayedCall(50, () => {
            if (this.active) this.clearTint();
        });
    }
}
