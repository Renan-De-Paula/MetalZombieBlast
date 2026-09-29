import Phaser from 'phaser';
import Player from '../entities/Player.js';
import Enemy from '../entities/Enemy.js';

export default class GameScene extends Phaser.Scene {
    constructor() {
        super('GameScene');
    }

    preload() {
        this.load.image('war_bg', '/assets/war_bg.jpg');
        this.load.image('player', '/assets/player.png');
        this.load.image('zombie_basic', '/assets/zombie_basic.png');
        this.load.image('zombie_runner', '/assets/zombie_runner.png');
        this.load.image('zombie_brute', '/assets/zombie_brute.png');
    }

    create() {
        // Fundo Parallax de Guerra
        const bgImg = this.textures.get('war_bg').getSourceImage();
        const scale = 600 / bgImg.height;
        this.bg = this.add.tileSprite(0, 0, 800 / scale, 600 / scale, 'war_bg')
            .setOrigin(0, 0).setScrollFactor(0).setScale(scale);

        // Grupos de Física
        this.platforms = this.physics.add.staticGroup();
        this.obstacles = this.physics.add.staticGroup(); // Barricadas, vigas
        this.hazards = this.physics.add.staticGroup(); // Minas, Barris, Pneus
        this.enemies = this.physics.add.group();
        this.bullets = this.physics.add.group({ defaultKey: 'bullet', maxSize: 100 });
        this.coins = this.physics.add.group({ allowGravity: false });
        
        // Efeitos visuais estáticos (para serem limpos depois)
        this.sceneryVisuals = [];
        this.sceneryParticles = [];

        this.coinsScore = 0;
        this.nextChunkX = 0;
        this.chunkWidth = 800;
        
        for (let i = 0; i < 3; i++) {
            this.generateChunk();
        }

        this.player = new Player(this, 100, 400);

        // Colisões e Interações
        this.physics.add.collider(this.player, this.platforms);
        this.physics.add.collider(this.enemies, this.platforms);
        this.physics.add.collider(this.player, this.obstacles);
        this.physics.add.collider(this.enemies, this.obstacles);
        
        this.physics.add.collider(this.player, this.hazards, this.playerHitHazard, null, this);
        
        this.physics.add.collider(this.bullets, this.platforms, this.bulletHitWall, null, this);
        this.physics.add.collider(this.bullets, this.obstacles, this.bulletHitWall, null, this);
        this.physics.add.collider(this.bullets, this.hazards, this.bulletHitHazard, null, this);

        this.physics.add.overlap(this.bullets, this.enemies, this.bulletHitEnemy, null, this);
        this.physics.add.overlap(this.player, this.enemies, this.playerHitEnemy, null, this);
        this.physics.add.overlap(this.player, this.coins, this.collectCoin, null, this);

        this.cameras.main.startFollow(this.player, true, 0.1, 0.1);
        this.cameras.main.setBounds(0, 0, 10000000, 600); // Trava a câmera no eixo Y

        this.cursors = this.input.keyboard.createCursorKeys();
        this.shootKey = this.input.keyboard.addKey(Phaser.Input.Keyboard.KeyCodes.Z);
        
        this.lastFired = 0;
        this.distance = 0;
        this.lastKm = 0;
        
        // --- UI ---
        this.distanceText = this.add.text(16, 16, 'Distância: 0m', { 
            fontSize: '20px', fill: '#fff', stroke: '#000', strokeThickness: 3, fontFamily: 'Courier New'
        }).setScrollFactor(0).setDepth(100);

        this.coinsText = this.add.text(16, 40, 'Moedas de Ouro: 0', { 
            fontSize: '20px', fill: '#ffd700', stroke: '#000', strokeThickness: 3, fontFamily: 'Courier New'
        }).setScrollFactor(0).setDepth(100);

        this.heartsText = this.add.text(16, 68, '❤️❤️❤️❤️❤️', { 
            fontSize: '22px', fontFamily: 'Courier New'
        }).setScrollFactor(0).setDepth(100);

        // Botão Tiro
        this.fireButtonDown = false;
        const fireBtn = this.add.circle(700, 500, 50, 0xff0000, 0.8)
            .setScrollFactor(0).setInteractive().setDepth(100);
            
        this.add.text(700, 500, 'FOGO', { 
            fontSize: '22px', fill: '#fff', fontStyle: 'bold', fontFamily: 'Courier New'
        }).setOrigin(0.5).setScrollFactor(0).setDepth(101);

        fireBtn.on('pointerdown', () => this.fireButtonDown = true);
        fireBtn.on('pointerup', () => this.fireButtonDown = false);
        fireBtn.on('pointerout', () => this.fireButtonDown = false);
        
        this.upgradeMenu = this.add.group();
        this.isGamePaused = false;
        this.bossSpawned = false;
    }

    updateHearts(currentHealth, maxHealth) {
        let heartsStr = '';
        for (let i = 0; i < maxHealth; i++) {
            if (i < currentHealth) heartsStr += '❤️';
            else heartsStr += '🖤';
        }
        this.heartsText.setText(heartsStr);
    }

    generateChunk() {
        const ground = this.add.rectangle(this.nextChunkX + this.chunkWidth / 2, 580, this.chunkWidth, 40, 0x3a5f0b).setVisible(false);
        this.physics.add.existing(ground, true);
        this.platforms.add(ground);

        const groundTop = this.add.rectangle(this.nextChunkX + this.chunkWidth / 2, 555, this.chunkWidth, 10, 0x4caf50).setVisible(false);
        this.physics.add.existing(groundTop, true);
        this.platforms.add(groundTop);

        const chunkType = Phaser.Math.Between(0, 3);
        
        if (chunkType === 0) {
            // Plataforma flutuante normal
            const platX = this.nextChunkX + Phaser.Math.Between(150, this.chunkWidth - 150);
            const plat = this.add.rectangle(platX, 400, 200, 20, 0x444444);
            this.physics.add.existing(plat, true);
            this.obstacles.add(plat);
            this.createCoinsLine(platX - 50, 360, 3);
            
        } else if (chunkType === 1) {
            // Obstáculo ALTO (Deslizar por baixo)
            const obsX = this.nextChunkX + Phaser.Math.Between(200, 500);
            this.spawnHighObstacle(obsX);
            this.createCoinsLine(obsX - 30, 540, 3);
            
        } else if (chunkType === 2) {
            // Obstáculo BAIXO (Pular por cima)
            const obsX = this.nextChunkX + Phaser.Math.Between(200, 500);
            this.spawnLowObstacle(obsX);
            this.createCoinsArc(obsX, 535);
        }

        // Inimigos
        if (this.nextChunkX > 0) {
            const enemyCount = Phaser.Math.Between(0, 2);
            const types = ['basic', 'runner', 'brute'];
            for (let i = 0; i < enemyCount; i++) {
                const enemyX = this.nextChunkX + Phaser.Math.Between(200, this.chunkWidth - 100);
                
                let randomType = types[Phaser.Math.Between(0, 2)];
                // 10% de chance de ser um mini-boss
                if (Phaser.Math.Between(1, 100) <= 10) {
                    randomType = 'miniboss';
                }
                
                this.enemies.add(new Enemy(this, enemyX, 500, randomType));
            }
        }

        this.nextChunkX += this.chunkWidth;
    }

    spawnHighObstacle(x) {
        // Bloco físico invisível que obriga a deslizar
        const hitBox = this.add.rectangle(x, 480, 100, 100, 0xff0000, 0); // Invisível
        this.physics.add.existing(hitBox, true);
        this.obstacles.add(hitBox);
        
        const type = Phaser.Math.Between(0, 1);
        const container = this.add.container(0, 0);
        
        if (type === 0) {
            // ARAME FARPADO
            const post1 = this.add.rectangle(x - 45, 505, 10, 100, 0x5c4033); // Estaca
            const post2 = this.add.rectangle(x + 45, 505, 10, 100, 0x5c4033);
            const wire1 = this.add.rectangle(x, 470, 90, 2, 0xaaaaaa);
            const wire2 = this.add.rectangle(x, 490, 90, 2, 0xaaaaaa);
            const wire3 = this.add.rectangle(x, 510, 90, 2, 0xaaaaaa);
            container.add([post1, post2, wire1, wire2, wire3]);
        } else {
            // CARCAÇA DE VEÍCULO/TANQUE
            const body = this.add.rectangle(x, 490, 110, 50, 0x2e3b32);
            const turret = this.add.rectangle(x + 15, 450, 60, 30, 0x1f2621);
            const barrel = this.add.rectangle(x - 30, 460, 70, 8, 0x111111);
            container.add([body, turret, barrel]);
            
            // Fumaça saindo do tanque
            const smoke = this.add.particles(x, 470, 'bullet', {
                speed: { min: 20, max: 50 },
                angle: { min: -110, max: -70 },
                scale: { start: 1, end: 3 },
                alpha: { start: 0.5, end: 0 },
                lifespan: 2000,
                tint: 0x555555
            });
            this.sceneryParticles.push({ p: smoke, x: x });
        }
        
        this.sceneryVisuals.push({ obj: container, x: x });
    }

    spawnLowObstacle(x) {
        const type = Phaser.Math.Between(0, 3);
        const container = this.add.container(0, 0);

        if (type === 0) {
            // BARRICADA DE SACOS DE AREIA
            const hitBox = this.add.rectangle(x, 535, 60, 40, 0x000000, 0);
            this.physics.add.existing(hitBox, true);
            this.obstacles.add(hitBox);
            
            for(let i=0; i<3; i++) {
                for(let j=0; j<2; j++) {
                    const sack = this.add.rectangle(x - 20 + (i*20), 545 - (j*15), 18, 12, 0xd2b48c).setStrokeStyle(1, 0x8b4513);
                    container.add(sack);
                }
            }
        } else if (type === 1) {
            // BARRIL EXPLOSIVO
            const hitBox = this.add.rectangle(x, 535, 30, 40, 0x000000, 0);
            this.physics.add.existing(hitBox, true);
            hitBox.hazardType = 'barrel';
            this.hazards.add(hitBox);
            
            const visual = this.add.rectangle(x, 535, 30, 40, 0xcc0000);
            const stripe = this.add.rectangle(x, 535, 30, 10, 0x111111);
            container.add([visual, stripe]);
            hitBox.visualContainer = container;
        } else if (type === 2) {
            // MINA TERRESTRE
            const hitBox = this.add.rectangle(x, 550, 30, 10, 0x000000, 0);
            this.physics.add.existing(hitBox, true);
            hitBox.hazardType = 'mine';
            this.hazards.add(hitBox);
            
            const visual = this.add.rectangle(x, 550, 30, 10, 0x333333);
            const light = this.add.circle(x, 545, 3, 0xff0000);
            this.tweens.add({ targets: light, alpha: 0, duration: 300, yoyo: true, repeat: -1 });
            container.add([visual, light]);
            hitBox.visualContainer = container;
        } else if (type === 3) {
            // PNEU EM CHAMAS
            const hitBox = this.add.circle(x, 540, 15, 0x000000, 0);
            this.physics.add.existing(hitBox, true);
            hitBox.hazardType = 'tire';
            this.hazards.add(hitBox);
            
            const visual = this.add.circle(x, 540, 15, 0x222222).setStrokeStyle(5, 0x111111);
            const fire = this.add.particles(x, 535, 'bullet', {
                speed: { min: 10, max: 40 },
                angle: { min: -110, max: -70 },
                scale: { start: 1, end: 0 },
                lifespan: 800,
                tint: 0xffaa00,
                blendMode: 'ADD'
            });
            container.add(visual);
            hitBox.visualContainer = container;
            hitBox.particle = fire;
            this.sceneryParticles.push({ p: fire, x: x });
        }
        
        this.sceneryVisuals.push({ obj: container, x: x });
    }

    explodeHazard(hazard) {
        if (hazard.exploded) return;
        hazard.exploded = true;
        
        // Explosão visual
        const emitter = this.add.particles(hazard.x, hazard.y, 'bullet', {
            speed: { min: 50, max: 250 },
            angle: { min: 0, max: 360 },
            scale: { start: 3, end: 0 },
            lifespan: 400,
            tint: 0xff5500,
            blendMode: 'ADD'
        });
        emitter.explode(30);
        this.cameras.main.shake(150, 0.01);
        
        // Dano em área
        const radius = 150;
        if (Phaser.Math.Distance.Between(this.player.x, this.player.y, hazard.x, hazard.y) < radius) {
            this.player.takeDamage();
            this.player.body.setVelocityY(-400);
            const dir = this.player.x < hazard.x ? -1 : 1;
            this.player.body.setVelocityX(300 * dir);
        }
        
        this.enemies.getChildren().forEach(enemy => {
            if (enemy.active && Phaser.Math.Distance.Between(enemy.x, enemy.y, hazard.x, hazard.y) < radius) {
                enemy.takeDamage(50); // Morte instantânea
            }
        });
        
        if(hazard.visualContainer) hazard.visualContainer.destroy();
        if(hazard.particle) hazard.particle.destroy();
        hazard.destroy();
    }

    playerHitHazard(player, hazard) {
        if (hazard.hazardType === 'tire') {
            player.takeDamage();
            player.body.setVelocityY(-300);
            const dir = player.x < hazard.x ? -1 : 1;
            player.body.setVelocityX(300 * dir);
        } else if (hazard.hazardType === 'mine' && player.body.touching.down && hazard.body.touching.up) {
            this.explodeHazard(hazard);
        }
        // O Barril funciona como uma parede normal até ser atirado
    }

    bulletHitHazard(bullet, hazard) {
        bullet.destroy();
        if (hazard.hazardType === 'barrel' || hazard.hazardType === 'mine') {
            this.explodeHazard(hazard);
        }
    }

    createCoinsLine(startX, y, count) {
        for(let i=0; i<count; i++){
            const coin = this.add.circle(startX + (i * 40), y, 10, 0xffd700);
            this.physics.add.existing(coin);
            this.tweens.add({ targets: coin, alpha: 0.6, duration: 500, yoyo: true, repeat: -1 });
            this.coins.add(coin);
        }
    }
    
    createCoinsArc(centerX, bottomY) {
        const c1 = this.add.circle(centerX - 50, bottomY - 60, 10, 0xffd700);
        const c2 = this.add.circle(centerX, bottomY - 100, 10, 0xffd700);
        const c3 = this.add.circle(centerX + 50, bottomY - 60, 10, 0xffd700);
        [c1, c2, c3].forEach(c => {
            this.physics.add.existing(c);
            this.tweens.add({ targets: c, alpha: 0.6, duration: 500, yoyo: true, repeat: -1 });
            this.coins.add(c);
        });
    }

    collectCoin(player, coin) {
        coin.destroy();
        this.coinsScore += 1;
        this.coinsText.setText(`Moedas de Ouro: ${this.coinsScore}`);
        this.cameras.main.flash(100, 255, 215, 0);
    }

    update(time, delta) {
        if (this.isGamePaused) return;

        this.player.update(this.cursors);

        const leftBound = this.cameras.main.scrollX;
        if (this.player.x < leftBound + 16) {
            this.player.x = leftBound + 16;
            this.player.body.setVelocityX(Math.max(0, this.player.body.velocity.x));
        }

        this.bg.tilePositionX = this.cameras.main.scrollX * 0.3;

        if (this.player.x > this.nextChunkX - (this.chunkWidth * 2)) {
            this.generateChunk();
        }

        this.cleanupOldObjects();

        const meters = Math.max(0, Math.floor(this.player.x / 10));
        this.distance = meters;
        this.distanceText.setText(`Distância: ${this.distance}m`);

        const currentKm = Math.floor(this.distance / 1000);
        if (currentKm > this.lastKm) {
            this.lastKm = currentKm;
            this.showUpgradeMenu();
        }

        if (this.distance >= 1000 && !this.bossSpawned) {
            this.bossSpawned = true;
            const bossX = this.cameras.main.scrollX + 900;
            this.enemies.add(new Enemy(this, bossX, 500, 'boss'));
            
            // Texto anunciando boss
            const bossText = this.add.text(400, 300, 'ALERTA DE BOSS!', { fontSize: '40px', fill: '#ff0000', fontStyle: 'bold' }).setOrigin(0.5).setScrollFactor(0);
            this.tweens.add({ targets: bossText, alpha: 0, duration: 2000, onComplete: () => bossText.destroy() });
        }

        if ((this.shootKey.isDown || this.fireButtonDown) && time > this.lastFired) {
            this.shoot();
            this.lastFired = time + this.player.weapon.fireRate;
        }

        this.enemies.getChildren().forEach(enemy => {
            if(enemy.active) enemy.update();
        });
    }
    
    cleanupOldObjects() {
        const cleanupX = this.cameras.main.scrollX - 400;
        
        this.platforms.getChildren().forEach(p => { if (p.x + p.width < cleanupX) p.destroy(); });
        this.obstacles.getChildren().forEach(o => { if (o.x + o.width < cleanupX) o.destroy(); });
        this.hazards.getChildren().forEach(h => { 
            if (h.x + h.width < cleanupX) {
                if(h.visualContainer) h.visualContainer.destroy();
                if(h.particle) h.particle.destroy();
                h.destroy(); 
            }
        });
        this.enemies.getChildren().forEach(e => { if (e.x < cleanupX) e.destroy(); });
        this.coins.getChildren().forEach(c => { if (c.x < cleanupX) c.destroy(); });
        this.bullets.getChildren().forEach(b => {
            if (b.x > this.cameras.main.scrollX + 850 || b.x < cleanupX) b.destroy();
        });

        // Limpa visuais estáticos
        this.sceneryVisuals = this.sceneryVisuals.filter(item => {
            if (item.x < cleanupX) { item.obj.destroy(); return false; }
            return true;
        });
        this.sceneryParticles = this.sceneryParticles.filter(item => {
            if (item.x < cleanupX) { item.p.destroy(); return false; }
            return true;
        });
    }

    shoot() {
        const weapon = this.player.weapon;
        const direction = this.player.flipX ? -1 : 1;
        const spawnY = this.player.isCrouching ? this.player.y + 6 : this.player.y;
        
        if (weapon.type === 'shotgun') {
            for (let i = -1; i <= 1; i++) {
                this.createBullet(direction, i * 150, spawnY);
            }
        } else {
            this.createBullet(direction, 0, spawnY);
        }
    }
    
    createBullet(direction, velY, spawnY) {
        if (this.player.body.touching.down && !this.player.isCrouching) {
            this.player.body.setVelocityX(-50 * direction);
        }

        const color = this.player.weapon.type === 'sniper' ? 0xff00aa : (this.player.weapon.type === 'machinegun' ? 0x00aaff : 0xffff00);
        const bullet = this.add.rectangle(this.player.x, spawnY, 16, 6, color);
        
        this.physics.add.existing(bullet);
        this.bullets.add(bullet);
        
        bullet.body.setVelocityX(this.player.weapon.bulletSpeed * direction);
        bullet.body.setVelocityY(velY);
        bullet.body.setAllowGravity(false);
        bullet.damage = this.player.weapon.damage;
    }

    bulletHitWall(bullet, wall) { bullet.destroy(); }

    bulletHitEnemy(bullet, enemy) {
        const damage = bullet.damage || 10;
        bullet.destroy();
        enemy.takeDamage(damage);
    }

    playerHitEnemy(player, enemy) {
        if (player.isInvulnerable) return;
        player.takeDamage();
        const dir = player.x < enemy.x ? -1 : 1;
        player.body.setVelocityX(400 * dir);
        player.body.setVelocityY(-300);
    }
    
    showUpgradeMenu() {
        this.isGamePaused = true;
        this.physics.pause();
        this.player.body.setVelocity(0,0);
        
        const cx = 400; // Posicionamento absoluto na tela
        const cy = 300;
        
        const overlay = this.add.rectangle(cx, cy, 800, 600, 0x000000, 0.8).setScrollFactor(0).setDepth(200);
        const title = this.add.text(cx, cy - 150, '🎉 1 QUILÔMETRO ALCANÇADO! 🎉\nATUALIZE SUA ARMA', { 
            fontSize: '32px', fill: '#fff', align: 'center', fontStyle: 'bold', fontFamily: 'Courier New'
        }).setOrigin(0.5).setScrollFactor(0).setDepth(201);
        
        this.upgradeMenu.addMultiple([overlay, title]);
        
        const weapons = [
            { id: 'machinegun', name: 'Machine Gun', color: 0x00aaff, desc: 'Tiro Rápido\n50 Moedas' },
            { id: 'shotgun', name: 'Shotgun', color: 0xffaa00, desc: 'Espalhado\n100 Moedas' },
            { id: 'sniper', name: 'Rifle Pesado', color: 0xff00aa, desc: 'Muito Dano\n150 Moedas' }
        ];
        
        weapons.forEach((w, i) => {
            const btnX = cx - 220 + (i * 220);
            const btnY = cy + 50;
            const cost = w.id === 'machinegun' ? 50 : (w.id === 'shotgun' ? 100 : 150);
            const canAfford = this.coinsScore >= cost;
            
            const bgColor = canAfford ? w.color : 0x444444;
            const btnBg = this.add.rectangle(btnX, btnY, 200, 120, bgColor).setInteractive().setScrollFactor(0).setDepth(201);
            btnBg.setStrokeStyle(4, 0xffffff);

            const btnText = this.add.text(btnX, btnY - 20, w.name, { fontSize: '20px', fill: '#fff', fontStyle: 'bold' }).setOrigin(0.5).setScrollFactor(0).setDepth(202);
            const btnDesc = this.add.text(btnX, btnY + 20, canAfford ? w.desc : 'Sem Moedas', { fontSize: '14px', fill: '#fff', align: 'center' }).setOrigin(0.5).setScrollFactor(0).setDepth(202);
            
            if(canAfford) {
                btnBg.on('pointerover', () => btnBg.setScale(1.1));
                btnBg.on('pointerout', () => btnBg.setScale(1));

                btnBg.on('pointerdown', () => {
                    this.coinsScore -= cost;
                    this.coinsText.setText(`Moedas de Ouro: ${this.coinsScore}`);
                    this.selectWeapon(w.id);
                });
            }

            this.upgradeMenu.addMultiple([btnBg, btnText, btnDesc]);
        });
        
        const skipBtn = this.add.rectangle(cx, cy + 200, 200, 50, 0x555555).setInteractive().setScrollFactor(0).setDepth(201);
        const skipText = this.add.text(cx, cy + 200, 'Continuar (Grátis)', { fontSize: '16px', fill: '#fff' }).setOrigin(0.5).setScrollFactor(0).setDepth(202);
        
        skipBtn.on('pointerdown', () => this.selectWeapon('skip'));
        this.upgradeMenu.addMultiple([skipBtn, skipText]);
    }
    
    selectWeapon(weaponId) {
        if (weaponId === 'machinegun') {
            this.player.weapon = { type: 'machinegun', fireRate: 80, bulletSpeed: 1000, damage: 12 };
        } else if (weaponId === 'shotgun') {
            this.player.weapon = { type: 'shotgun', fireRate: 400, bulletSpeed: 800, damage: 25 };
        } else if (weaponId === 'sniper') {
            this.player.weapon = { type: 'sniper', fireRate: 700, bulletSpeed: 1500, damage: 60 };
        }
        
        this.upgradeMenu.clear(true, true);
        this.physics.resume();
        this.isGamePaused = false;
    }
}
