import Phaser from 'phaser';

export default class Player extends Phaser.GameObjects.Sprite {
    constructor(scene, x, y) {
        super(scene, x, y, 'player'); 
        scene.add.existing(this);
        scene.physics.add.existing(this);
        
        this.setScale(0.1); 
        // A imagem original tem cerca de 1024x1024. Com scale 0.1, ela fica com ~100x100.
        // Vamos ajustar a hitbox da física para se alinhar ao centro da imagem (largura: 400, altura: 800) e aplicar offset.
        this.body.setSize(400, 800);
        this.body.setOffset(312, 224);

        this.body.setBounce(0.0);
        this.body.setCollideWorldBounds(false); 
        this.body.setDragX(1000);
        
        this.speed = 250;
        this.jumpForce = -550;
        
        this.maxHealth = 5;
        this.health = 5;
        this.isInvulnerable = false;
        this.isCrouching = false;

        this.weapon = {
            type: 'pistol',
            fireRate: 200,
            bulletSpeed: 800,
            damage: 15
        };
    }

    update(cursors) {
        // Lógica de agachar (crouch)
        if (cursors.down.isDown && this.body.touching.down && !this.isCrouching) {
            this.isCrouching = true;
            this.body.setSize(32, 24);
            this.body.setOffset(0, 24);
            this.setDisplaySize(32, 24);
        } else if (!cursors.down.isDown && this.isCrouching) {
            this.isCrouching = false;
            this.body.setSize(32, 48);
            this.body.setOffset(0, 0);
            this.setDisplaySize(32, 48);
            this.y -= 12; 
        }

        const currentSpeed = this.isCrouching ? 100 : this.speed;

        if (cursors.left.isDown) {
            this.body.setAccelerationX(-1500);
            this.flipX = true;
        } else if (cursors.right.isDown) {
            this.body.setAccelerationX(1500);
            this.flipX = false;
        } else {
            this.body.setAccelerationX(0);
        }
        
        if (this.body.velocity.x > currentSpeed) {
            this.body.setVelocityX(currentSpeed);
        } else if (this.body.velocity.x < -currentSpeed) {
            this.body.setVelocityX(-currentSpeed);
        }

        if (cursors.up.isDown && this.body.touching.down && !this.isCrouching) {
            this.body.setVelocityY(this.jumpForce);
        }
    }
    
    takeDamage() {
        if (this.isInvulnerable) return;
        
        this.health -= 1;
        this.isInvulnerable = true;
        
        if (this.scene.updateHearts) {
            this.scene.updateHearts(this.health, this.maxHealth);
        }
        
        this.scene.tweens.add({
            targets: this,
            alpha: 0.2,
            duration: 100,
            yoyo: true,
            repeat: 5,
            onComplete: () => {
                this.alpha = 1;
                this.isInvulnerable = false;
            }
        });
        
        // Morte (quando os corações chegam a 0)
        if (this.health <= 0) {
            this.scene.cameras.main.flash(500, 255, 0, 0);
            this.setPosition(this.scene.cameras.main.scrollX + 100, 300);
            
            // Restaura os corações
            this.health = this.maxHealth;
            if (this.scene.updateHearts) {
                this.scene.updateHearts(this.health, this.maxHealth);
            }
            
            // Perde a arma especial
            this.weapon = { type: 'pistol', fireRate: 200, bulletSpeed: 800, damage: 15 };
        }
    }
}
