import Phaser from 'phaser';
import GameScene from './scenes/GameScene.js';

const config = {
    type: Phaser.AUTO,
    width: 800,
    height: 600,
    parent: 'game-container',
    physics: {
        default: 'arcade',
        arcade: {
            gravity: { y: 0 }, // 0 gravity for top-down 2.5D
            debug: false
        }
    },
    scene: [GameScene],
    pixelArt: true,
};

export default new Phaser.Game(config);
