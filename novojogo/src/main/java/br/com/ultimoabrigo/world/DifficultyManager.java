package br.com.ultimoabrigo.world;

/**
 * DifficultyManager - Calcula a curva de dificuldade progressiva tela a tela (screenIndex)
 * e fase a fase:
 * - Nível = (fase - 1) * pesoFase + screenIndex * incrementoTela.
 * - Modula: quantidade de zumbis simultâneos, proporção de Corredores sobre Caminhantes,
 *   velocidade dos zumbis (até +40% no fim da fase), densidade de armadilhas e escassez de suprimentos.
 */
public class DifficultyManager {

    private final int stageNumber;
    private static final float STAGE_WEIGHT = 4.5f;
    private static final float SCREEN_INCREMENT = 0.45f;

    public DifficultyManager(int stageNumber) {
        this.stageNumber = stageNumber;
    }

    /**
     * Nível numérico absoluto de dificuldade.
     */
    public float getDifficultyLevel(int screenIndex) {
        return (stageNumber - 1) * STAGE_WEIGHT + screenIndex * SCREEN_INCREMENT;
    }

    /**
     * Quantidade de zumbis a gerar na tela (1 a 3 no início da Fase 1, até 7-9 na Fase 3).
     */
    public int getZombieCount(int screenIndex) {
        float diff = getDifficultyLevel(screenIndex);
        int baseCount = 1 + (int) (diff * 0.28f);

        // Variação por fase
        if (stageNumber == 1) {
            return Math.min(6, Math.max(1, baseCount));
        } else if (stageNumber == 2) {
            return Math.min(8, Math.max(3, baseCount + 1));
        } else {
            return Math.min(10, Math.max(4, baseCount + 2));
        }
    }

    /**
     * Proporção de Zumbis Corredores (Runner) sobre Caminhantes (0.0 = só walkers, 0.8 = 80% runners).
     */
    public float getRunnerRatio(int screenIndex) {
        if (stageNumber == 1 && screenIndex < 3) {
            return 0.0f; // Telas iniciais da Fase 1: apenas Caminhantes para tutorial
        }

        float progress = Math.min(1.0f, screenIndex / 32.0f);
        float baseRatio = progress * 0.40f; // Até 40% no fim da fase 1

        if (stageNumber == 2) {
            baseRatio += 0.25f; // Fase 2: 25% a 65%
        } else if (stageNumber == 3) {
            baseRatio += 0.45f; // Fase 3: 45% a 85%
        }

        return Math.min(0.85f, baseRatio);
    }

    /**
     * Multiplicador de velocidade dos zumbis (até +40% no fim da fase).
     */
    public float getSpeedMultiplier(int screenIndex) {
        float progress = Math.min(1.0f, screenIndex / 32.0f);
        // Aumenta de 1.0f até 1.40f (+40%) ao longo dos 1000m
        float bonus = progress * 0.40f;
        // Bônus adicional leve para fases mais avançadas
        float stageBonus = (stageNumber - 1) * 0.05f;
        return 1.0f + bonus + stageBonus;
    }

    /**
     * Probabilidade de drop de suprimentos ao matar zumbis (reduz à medida que a fase avança).
     */
    public float getSupplyDropRate(int screenIndex) {
        float progress = Math.min(1.0f, screenIndex / 32.0f);
        // Começa em 35% e cai para 15% próximo ao chefe
        float drop = 0.35f - (progress * 0.20f);
        // Fase 3 é ainda mais escassa
        if (stageNumber == 3) {
            drop *= 0.75f;
        }
        return Math.max(0.12f, drop);
    }

    /**
     * Percentual de intensidade da horda (0% a 100%) para exibição discreta no HUD.
     */
    public int getIntensityPercentage(int screenIndex) {
        float maxExpected = 2 * STAGE_WEIGHT + 32 * SCREEN_INCREMENT;
        float current = getDifficultyLevel(screenIndex);
        return Math.min(100, Math.max(5, (int) ((current / maxExpected) * 100)));
    }

    /**
     * Descrição textual do nível de ameaça.
     */
    public String getThreatLevelText(int screenIndex) {
        int intensity = getIntensityPercentage(screenIndex);
        if (intensity < 25) return "BAIXA";
        if (intensity < 50) return "MODERADA";
        if (intensity < 75) return "ALTA";
        return "EXTREMA";
    }

    public int getStageNumber() {
        return stageNumber;
    }
}
