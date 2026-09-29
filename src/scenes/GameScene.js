import Phaser from 'phaser';
import Player from '../entities/Player.js';
import Enemy from '../entities/Enemy.js';

export default class GameScene extends Phaser.Scene {
    constructor() {
        super('GameScene');
    }

    preload() {
        this.load.image('player', '/assets/player.png');
        this.load.image('zombie_basic', '/assets/zombie_basic.png');
        this.load.image('zombie_runner', '/assets/zombie_runner.png');
        this.load.image('zombie_brute', '/assets/zombie_brute.png');
    }

    create() {
        // Criar textura de grama programaticamente
        const graphics = this.add.graphics();
        graphics.fillStyle(0x8bc34a, 1); // Verde claro (grama)
        graphics.fillRect(0, 0, 64, 64);
        graphics.fillStyle(0x7cb342, 1); // Detalhes da grama
        graphics.fillRect(10, 10, 5, 5);
        graphics.fillRect(40, 30, 8, 8);
        graphics.generateTexture('grass_bg', 64, 64);
        graphics.destroy();

        // Criar textura de Gem (EXP)
        const gemGraphics = this.add.graphics();
        gemGraphics.fillStyle(0x00ff00, 1); // Verde
        gemGraphics.lineStyle(2, 0x005500, 1);
        gemGraphics.strokeRect(0, 0, 12, 12);
        gemGraphics.fillRect(0, 0, 12, 12);
        gemGraphics.generateTexture('gem', 12, 12);
        gemGraphics.destroy();

        // Mundo gigante
        this.physics.world.setBounds(0, 0, 4000, 4000);
        this.bg = this.add.tileSprite(0, 0, 800, 600, 'grass_bg').setOrigin(0, 0).setScrollFactor(0);

        // Grupos
        this.enemies = this.physics.add.group();
        this.bullets = this.physics.add.group({ maxSize: 300 });
        this.gems = this.physics.add.group(); // Experiência
        this.damageTexts = this.add.group();

        this.killCount = 0;
        this.playerLevel = 1;
        this.playerExp = 0;
        this.expToNextLevel = 10;
        
        this.player = new Player(this, 2000, 2000);

        // Colisões (Sem colisão entre inimigos para permitir hordas gigantes)
        this.physics.add.overlap(this.bullets, this.enemies, this.bulletHitEnemy, null, this);
        this.physics.add.overlap(this.player, this.enemies, this.playerHitEnemy, null, this);
        // Coleta de gemas é feita na mão no update para simular magnetismo suave

        this.cameras.main.startFollow(this.player, true, 0.1, 0.1);
        this.cameras.main.setBounds(0, 0, 4000, 4000); 
        this.cursors = this.input.keyboard.createCursorKeys();
        
        this.spawnTimer = 0;
        this.spawnRate = 1000;
        
        this.createUI();

        this.isGamePaused = false;
        this.gameTimer = 0;
    }

    createUI() {
        // Barra de EXP
        this.expBarBg = this.add.rectangle(400, 15, 780, 20, 0x000000).setScrollFactor(0).setDepth(10000);
        this.expBarFill = this.add.rectangle(10, 15, 0, 20, 0x00aaff).setOrigin(0, 0.5).setScrollFactor(0).setDepth(10001);
        this.levelText = this.add.text(760, 15, 'Lv 1', { fontSize: '16px', fill: '#fff', fontStyle: 'bold' }).setOrigin(1, 0.5).setScrollFactor(0).setDepth(10002);

        // Barra de Vida
        this.healthBarBg = this.add.rectangle(400, 580, 300, 15, 0x000000).setScrollFactor(0).setDepth(10000);
        this.healthBarFill = this.add.rectangle(250, 580, 300, 15, 0x00ff00).setOrigin(0, 0.5).setScrollFactor(0).setDepth(10001);

        // Textos
        this.timeText = this.add.text(400, 45, '00:00', { fontSize: '24px', fill: '#fff', fontStyle: 'bold', stroke: '#000', strokeThickness: 4 }).setOrigin(0.5).setScrollFactor(0).setDepth(10000);
        this.killsText = this.add.text(780, 50, 'Kills: 0', { fontSize: '20px', fill: '#fff', stroke: '#000', strokeThickness: 3 }).setOrigin(1, 0.5).setScrollFactor(0).setDepth(10000);
        
        this.updateHealthBar();
    }

    updateHealthBar() {
        const pct = Math.max(0, this.player.stats.health / this.player.stats.maxHealth);
        this.healthBarFill.width = 300 * pct;
        if (pct < 0.3) this.healthBarFill.fillColor = 0xff0000;
        else if (pct < 0.6) this.healthBarFill.fillColor = 0xffff00;
        else this.healthBarFill.fillColor = 0x00ff00;
    }

    spawnGem(x, y, amount) {
        const gem = this.add.sprite(x, y, 'gem');
        this.physics.add.existing(gem);
        gem.expValue = amount;
        
        // Cores baseadas no XP (como Vampire Survivors)
        if (amount > 15) gem.setTint(0xff0000); // Vermelho
        else if (amount > 4) gem.setTint(0x0000ff); // Azul
        else gem.setTint(0x00ff00); // Verde

        gem.setDepth(y - 1); // Fica no chão
        this.gems.add(gem);
    }

    spawnEnemies() {
        const cam = this.cameras.main;
        const spawnAmount = Math.min(10 + Math.floor(this.gameTimer / 10000), 40); // Spawna cada vez mais
        
        for (let i = 0; i < spawnAmount; i++) {
            const edge = Phaser.Math.Between(0, 3);
            let spawnX, spawnY;
            const offset = 200; // Nasce bem fora da tela
            
            if (edge === 0) { // Top
                spawnX = cam.scrollX + Phaser.Math.Between(-offset, cam.width + offset);
                spawnY = cam.scrollY - offset;
            } else if (edge === 1) { // Bottom
                spawnX = cam.scrollX + Phaser.Math.Between(-offset, cam.width + offset);
                spawnY = cam.scrollY + cam.height + offset;
            } else if (edge === 2) { // Left
                spawnX = cam.scrollX - offset;
                spawnY = cam.scrollY + Phaser.Math.Between(-offset, cam.height + offset);
            } else { // Right
                spawnX = cam.scrollX + cam.width + offset;
                spawnY = cam.scrollY + Phaser.Math.Between(-offset, cam.height + offset);
            }

            let type = 'basic';
            const rnd = Phaser.Math.Between(1, 100);
            if (this.gameTimer > 60000 && rnd <= 20) type = 'runner';
            if (this.gameTimer > 120000 && rnd <= 10) type = 'brute';
            
            this.enemies.add(new Enemy(this, spawnX, spawnY, type));
        }
    }

    update(time, delta) {
        if (this.isGamePaused) return;

        this.gameTimer += delta;
        const mins = Math.floor(this.gameTimer / 60000).toString().padStart(2, '0');
        const secs = Math.floor((this.gameTimer % 60000) / 1000).toString().padStart(2, '0');
        this.timeText.setText(`${mins}:${secs}`);

        this.player.update(this.cursors, time);

        this.bg.tilePositionX = this.cameras.main.scrollX;
        this.bg.tilePositionY = this.cameras.main.scrollY;

        // Spawner de Hordas
        if (time > this.spawnTimer) {
            this.spawnEnemies();
            this.spawnRate = Math.max(300, 1000 - (this.gameTimer / 1000));
            this.spawnTimer = time + this.spawnRate;
        }

        // Auto Attack (Procura inimigo mais próximo)
        if (time > this.player.lastAttackTime) {
            this.autoAttack();
            this.player.lastAttackTime = time + this.player.stats.attackCooldown;
        }

        // Atualizar Inimigos
        this.enemies.getChildren().forEach(enemy => {
            if (enemy.active) enemy.update();
        });

        // Magnetismo de Gemas e Coleta
        const pX = this.player.x;
        const pY = this.player.y;
        this.gems.getChildren().forEach(gem => {
            if (!gem.active) return;
            const dist = Phaser.Math.Distance.Between(pX, pY, gem.x, gem.y);
            
            if (dist < 40) { // Coletou
                this.collectExp(gem.expValue);
                gem.destroy();
            } else if (dist < this.player.stats.magnetRadius) { // Magnetismo
                this.physics.moveToObject(gem, this.player, 400);
            } else {
                gem.body.setVelocity(0, 0); // Para de mover se sair do raio
            }
        });

        // Limpar balas
        this.bullets.getChildren().forEach(b => {
            if (b.active && Phaser.Math.Distance.Between(pX, pY, b.x, b.y) > 1000) {
                b.destroy();
            }
        });
    }

    autoAttack() {
        // Encontrar inimigo mais próximo
        let closest = null;
        let minD = Infinity;
        
        this.enemies.getChildren().forEach(enemy => {
            if (enemy.active) {
                const d = Phaser.Math.Distance.Between(this.player.x, this.player.y, enemy.x, enemy.y);
                if (d < minD && d < 600) { // Range de visão
                    minD = d;
                    closest = enemy;
                }
            }
        });

        if (closest) {
            const angle = Phaser.Math.Angle.Between(this.player.x, this.player.y, closest.x, closest.y);
            
            if (closest.x < this.player.x) this.player.flipX = true;
            else this.player.flipX = false;
            
            // Atira Qtd de Projéteis
            for (let i = 0; i < this.player.stats.projectiles; i++) {
                const spread = (i - (this.player.stats.projectiles - 1) / 2) * 0.2; // Espalhamento leve
                this.createBullet(angle + spread);
            }
        }
    }
    
    createBullet(angle) {
        const bullet = this.add.rectangle(this.player.x, this.player.y, 20, 4, 0x00ffff);
        this.physics.add.existing(bullet);
        this.bullets.add(bullet);
        
        bullet.body.velocity.x = Math.cos(angle) * this.player.stats.projectileSpeed;
        bullet.body.velocity.y = Math.sin(angle) * this.player.stats.projectileSpeed;
        bullet.rotation = angle;
        bullet.damage = this.player.stats.damage;
        bullet.setDepth(this.player.y);
    }

    bulletHitEnemy(bullet, enemy) {
        if (!bullet.active || !enemy.active) return;
        enemy.takeDamage(bullet.damage);
        bullet.destroy();
    }

    playerHitEnemy(player, enemy) {
        if (enemy.active && !player.isInvulnerable) {
            player.takeDamage(5); // Dano constante
            
            // Pequeno knockback no player e no zumbi
            const angle = Phaser.Math.Angle.Between(enemy.x, enemy.y, player.x, player.y);
            player.body.velocity.x = Math.cos(angle) * 300;
            player.body.velocity.y = Math.sin(angle) * 300;
        }
    }

    collectExp(amount) {
        this.playerExp += amount;
        
        if (this.playerExp >= this.expToNextLevel) {
            this.playerExp -= this.expToNextLevel;
            this.playerLevel++;
            this.expToNextLevel = Math.floor(this.expToNextLevel * 1.5);
            this.levelText.setText(`Lv ${this.playerLevel}`);
            this.showLevelUpMenu();
        }

        const pct = Math.min(1, this.playerExp / this.expToNextLevel);
        this.expBarFill.width = 780 * pct;
    }

    showDamageText(x, y, damage) {
        const text = this.add.text(x, y, damage.toString(), {
            fontSize: '18px', fill: '#fff', fontStyle: 'bold', stroke: '#000', strokeThickness: 3
        }).setOrigin(0.5).setDepth(20000);

        this.tweens.add({
            targets: text,
            y: y - 50,
            alpha: 0,
            duration: 800,
            onComplete: () => text.destroy()
        });
    }

    showLevelUpMenu() {
        this.isGamePaused = true;
        this.physics.pause();
        this.player.body.setVelocity(0,0);
        
        const cx = this.cameras.main.scrollX + 400; 
        const cy = this.cameras.main.scrollY + 300;
        
        const overlay = this.add.rectangle(cx, cy, 800, 600, 0x000000, 0.7).setDepth(30000);
        const title = this.add.text(cx, cy - 200, 'NOVA CARTA / UPGRADE', { 
            fontSize: '36px', fill: '#ffdf00', fontStyle: 'bold', stroke: '#000', strokeThickness: 6 
        }).setOrigin(0.5).setDepth(30001);
        
        this.upgradeMenuGroup = this.add.group();
        this.upgradeMenuGroup.addMultiple([overlay, title]);
        
        const possibleUpgrades = [
            { id: 'dmg', name: '+ Força', desc: 'Aumenta Dano', color: 0xff4444 },
            { id: 'spd', name: '+ Velocidade', desc: 'Move mais rápido', color: 0x44ff44 },
            { id: 'atkSpd', name: '+ Rapidez', desc: 'Ataca mais rápido', color: 0x4444ff },
            { id: 'proj', name: '+ Projéteis', desc: 'Atira mais raios', color: 0xff00ff },
            { id: 'mag', name: '+ Ímã', desc: 'Raio de coleta maior', color: 0x00ffff },
            { id: 'heal', name: 'Curar', desc: 'Recupera Vida', color: 0xffffff }
        ];
        
        Phaser.Utils.Array.Shuffle(possibleUpgrades);
        
        for (let i = 0; i < 3; i++) {
            const upg = possibleUpgrades[i];
            const btnX = cx - 220 + (i * 220);
            const btnY = cy;
            
            const btnBg = this.add.rectangle(btnX, btnY, 200, 300, upg.color).setInteractive().setDepth(30001);
            btnBg.setStrokeStyle(6, 0xffffff);

            const btnText = this.add.text(btnX, btnY - 50, upg.name, { fontSize: '24px', fill: '#000', fontStyle: 'bold' }).setOrigin(0.5).setDepth(30002);
            const btnDesc = this.add.text(btnX, btnY + 50, upg.desc, { fontSize: '18px', fill: '#000', align: 'center', wordWrap: { width: 180 } }).setOrigin(0.5).setDepth(30002);
            
            btnBg.on('pointerover', () => btnBg.setScale(1.05));
            btnBg.on('pointerout', () => btnBg.setScale(1));

            btnBg.on('pointerdown', () => {
                this.applyUpgrade(upg.id);
            });

            this.upgradeMenuGroup.addMultiple([btnBg, btnText, btnDesc]);
        }
    }
    
    applyUpgrade(id) {
        const stats = this.player.stats;
        
        if (id === 'dmg') stats.damage += 10;
        else if (id === 'spd') stats.speed += 30;
        else if (id === 'atkSpd') stats.attackCooldown = Math.max(200, stats.attackCooldown - 150);
        else if (id === 'proj') stats.projectiles += 1;
        else if (id === 'mag') stats.magnetRadius += 100;
        else if (id === 'heal') stats.health = Math.min(stats.maxHealth, stats.health + 50);
        
        this.updateHealthBar();
        
        this.upgradeMenuGroup.clear(true, true);
        this.physics.resume();
        this.isGamePaused = false;
    }

    gameOver() {
        this.isGamePaused = true;
        this.physics.pause();
        const cx = this.cameras.main.scrollX + 400; 
        const cy = this.cameras.main.scrollY + 300;
        
        this.add.rectangle(cx, cy, 800, 600, 0x550000, 0.8).setDepth(40000);
        this.add.text(cx, cy - 50, 'GAME OVER', { fontSize: '64px', fill: '#fff', fontStyle: 'bold' }).setOrigin(0.5).setDepth(40001);
        
        const restartBtn = this.add.text(cx, cy + 50, 'TENTAR NOVAMENTE', { fontSize: '32px', fill: '#ff0' }).setOrigin(0.5).setInteractive().setDepth(40001);
        restartBtn.on('pointerdown', () => {
            this.scene.restart();
        });
    }
}
