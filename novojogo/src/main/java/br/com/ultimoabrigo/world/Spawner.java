package br.com.ultimoabrigo.world;

import br.com.ultimoabrigo.core.GameConfig;
import br.com.ultimoabrigo.entities.Pickup;
import br.com.ultimoabrigo.entities.RunnerZombie;
import br.com.ultimoabrigo.entities.WalkerZombie;
import br.com.ultimoabrigo.entities.Zombie;
import br.com.ultimoabrigo.entities.TankZombie;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Spawner - Gerencia a geração procedural e balanceada de hordas de zumbis
 * e obstáculos recorrentes acoplado à curva do DifficultyManager.
 */
public class Spawner {

    private final int stageNumber;
    private final DifficultyManager difficultyManager;
    private final Set<Integer> spawnedScreens = new HashSet<>();
    private final Random random;

    public Spawner(int stageNumber) {
        this.stageNumber = stageNumber;
        this.difficultyManager = new DifficultyManager(stageNumber);
        this.random = new Random(stageNumber * 77777L + 12345L);
    }

    public void update(float playerX, List<Zombie> zombies, List<Obstacle> obstacles, List<Pickup> pickups) {
        int currentScreen = (int) (playerX / GameConfig.LOGICAL_WIDTH);

        // Gera a tela atual e a próxima que está prestes a entrar no campo de visão
        for (int s = currentScreen; s <= currentScreen + 1 && s < 33; s++) {
            if (!spawnedScreens.contains(s)) {
                spawnScreen(s, zombies, obstacles, pickups);
                spawnedScreens.add(s);
            }
        }
    }

    private void spawnScreen(int screenIndex, List<Zombie> zombies, List<Obstacle> obstacles, List<Pickup> pickups) {
        float startX = screenIndex * GameConfig.LOGICAL_WIDTH;

        // Tela 0 (Início da fase): área de tutorial
        if (screenIndex == 0) {
            obstacles.add(new Obstacle(startX + 500, GameConfig.GROUND_Y - 40, Obstacle.ObstacleType.CHEST, stageNumber));
            obstacles.add(new Obstacle(startX + 650, GameConfig.GROUND_Y - 42, Obstacle.ObstacleType.CRATE_DEBRIS, stageNumber));
            WalkerZombie w = new WalkerZombie(startX + 820, GameConfig.GROUND_Y - GameConfig.WALKER_HEIGHT);
            w.setDropRate(0.5f);
            zombies.add(w);
            return;
        }

        // Não gera armadilhas mortais diretamente em cima dos checkpoints (250m, 500m, 750m)
        int meterStart = (int) (startX / GameConfig.PIXELS_PER_METER);
        boolean isNearCheckpoint = (meterStart >= 235 && meterStart <= 265) ||
                                   (meterStart >= 485 && meterStart <= 515) ||
                                   (meterStart >= 735 && meterStart <= 765);

        if (isNearCheckpoint) {
            pickups.add(new Pickup(startX + 400, GameConfig.GROUND_Y - 40, Pickup.PickupType.MEDKIT));
            // Coloca um baú de arma/munição bem no checkpoint para o jogador destruir e pegar
            obstacles.add(new Obstacle(startX + 550, GameConfig.GROUND_Y - 40, Obstacle.ObstacleType.CHEST, stageNumber));
            return;
        }

        // Dificuldade calculada para a tela
        int zombieCount = difficultyManager.getZombieCount(screenIndex);
        float runnerRatio = difficultyManager.getRunnerRatio(screenIndex);
        float speedMultiplier = difficultyManager.getSpeedMultiplier(screenIndex);
        float dropRate = difficultyManager.getSupplyDropRate(screenIndex);

        // 1. SPAWN DE OBSTÁCULOS RECORRENTES
        int pattern = (screenIndex + stageNumber) % 6;
        switch (pattern) {
            case 0:
                obstacles.add(new Obstacle(startX + 280, GameConfig.GROUND_Y - 48, Obstacle.ObstacleType.EXPLOSIVE_BARREL, stageNumber));
                obstacles.add(new Obstacle(startX + 350, GameConfig.GROUND_Y - 60, Obstacle.ObstacleType.WOODEN_BARRICADE, stageNumber));
                break;
            case 1:
                obstacles.add(new Obstacle(startX + 320, GameConfig.GROUND_Y - 48, Obstacle.ObstacleType.ABANDONED_CAR, stageNumber));
                obstacles.add(new Obstacle(startX + 550, GameConfig.GROUND_Y - 42, Obstacle.ObstacleType.CRATE_DEBRIS, stageNumber));
                break;
            case 2:
                obstacles.add(new Obstacle(startX + 260, GameConfig.GROUND_Y - 36, Obstacle.ObstacleType.BARBED_WIRE, stageNumber));
                obstacles.add(new Obstacle(startX + 480, GameConfig.GROUND_Y - 12, Obstacle.ObstacleType.LANDMINE, stageNumber));
                break;
            case 3:
                obstacles.add(new Obstacle(startX + 340, GameConfig.GROUND_Y - 26, Obstacle.ObstacleType.FALLING_HAZARD, stageNumber));
                obstacles.add(new Obstacle(startX + 580, GameConfig.GROUND_Y - 48, Obstacle.ObstacleType.EXPLOSIVE_BARREL, stageNumber));
                break;
            case 4:
                obstacles.add(new Obstacle(startX + 240, GameConfig.GROUND_Y - 12, Obstacle.ObstacleType.LANDMINE, stageNumber));
                obstacles.add(new Obstacle(startX + 450, GameConfig.GROUND_Y - 60, Obstacle.ObstacleType.WOODEN_BARRICADE, stageNumber));
                break;
            case 5:
                obstacles.add(new Obstacle(startX + 220, GameConfig.GROUND_Y - 48, Obstacle.ObstacleType.ABANDONED_CAR, stageNumber));
                obstacles.add(new Obstacle(startX + 460, GameConfig.GROUND_Y - 48, Obstacle.ObstacleType.EXPLOSIVE_BARREL, stageNumber));
                obstacles.add(new Obstacle(startX + 640, GameConfig.GROUND_Y - 36, Obstacle.ObstacleType.BARBED_WIRE, stageNumber));
                break;
        }

        // 2. SPAWN DE ZUMBIS COM VELOCIDADE E PROPORÇÃO DA DIFICULDADE
        for (int i = 0; i < zombieCount; i++) {
            float zx;
            // 20% de chance de spawn pela retaguarda para flanquear Elias
            if (i > 0 && random.nextFloat() < 0.20f) {
                zx = startX - (80 + i * 50);
            } else {
                zx = startX + 380 + (i * 95);
            }

            Zombie z;
            float r = random.nextFloat();
            if (r < runnerRatio) {
                z = new RunnerZombie(zx, GameConfig.GROUND_Y - GameConfig.RUNNER_HEIGHT);
            } else if (r < runnerRatio + 0.15f) { // 15% de chance de ser Tank
                z = new TankZombie(zx, GameConfig.GROUND_Y - br.com.ultimoabrigo.core.GameConfig.TANK_HEIGHT);
            } else {
                z = new WalkerZombie(zx, GameConfig.GROUND_Y - GameConfig.WALKER_HEIGHT);
            }

            z.applySpeedMultiplier(speedMultiplier);
            z.setDropRate(dropRate);
            zombies.add(z);
        }

        // Drop ocasional de peças no cenário
        if (random.nextFloat() < dropRate) {
            pickups.add(new Pickup(startX + 520, GameConfig.GROUND_Y - 40, Pickup.PickupType.SCRAP_METAL));
        }
    }

    public DifficultyManager getDifficultyManager() {
        return difficultyManager;
    }

    public void reset() {
        spawnedScreens.clear();
    }
}
