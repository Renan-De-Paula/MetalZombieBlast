package br.com.ultimoabrigo.core;

/**
 * Estados da Máquina de Estados principal do jogo.
 */
public enum GameState {
    MENU,            // Menu principal com opções
    INTRO_CUTSCENE,  // Cena inicial em quadros estáticos e texto datilografado
    PLAYING,         // Ação lateral Run & Gun nos 1000 metros da fase
    BOSS_FIGHT,      // Arena travada com combate contra o chefe da fase
    PAUSED,          // Jogo pausado com overlay
    PHASE_CLEAR,     // Tela de vitória da fase com estatísticas
    GAME_OVER,       // Morte do jogador com opção de retorno ao checkpoint
    ENDING           // Cutscene final com o reencontro no laboratório
}
