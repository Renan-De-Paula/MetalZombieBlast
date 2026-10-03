# PLANO DE EXECUÇÃO: "ÚLTIMO ABRIGO"
### Run & Gun 2D Estilo Metal Slug em Java 21 Puro (Java2D / Swing)

---

## 1. VISÃO GERAL DA ARQUITETURA
- **Linguagem & Runtime:** Java 21 LTS (OpenJDK Temurin 21)
- **Gerenciador de Dependências & Build:** Maven 3.9.9 (Plugin `maven-jar-plugin` & `maven-assembly-plugin` para JAR executável com um clique)
- **Renderização Gráfica:** Java2D puro com `BufferedImage` offscreen em resolução lógica **960x540**, renderizado para tela/janela redimensionável com `RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR` para manter o pixel art nítido sem borrões e com Letterboxing/Pillarboxing proporcional (16:9).
- **Game Loop:** Timestep fixo de 60 UPS (Updates Per Second) desacoplado de renderização, com cálculo de FPS/UPS real e double buffering.
- **Áudio:** Java Sound API nativo com sintetizador arcade procedural em tempo real (retro chiptune arcade multi-canal: melodia, baixo, ruído branco para percussão/tiros) e suporte a carregamento de WAV/PCM para efeitos sonoros crocantes (tiros, explosões, golpes de chave inglesa, alertas).

---

## 2. ESTRUTURA DE PACOTES
```
br.com.ultimoabrigo
├── core
│   ├── Game.java               // Ponto de entrada (Main) e inicializador
│   ├── GameEngine.java         // Game Loop fixo 60 UPS e gerenciamento de ticks
│   ├── GameWindow.java         // JFrame com suporte a tela cheia e modo janela
│   ├── GamePanel.java          // JPanel com renderizador nearest-neighbor e double buffering
│   ├── GameConfig.java         // Todas as constantes de balanceamento e configurações
│   ├── GameState.java          // Enum de estados (MENU, INTRO, PLAYING, BOSS, etc.)
│   ├── StateManager.java       // Transição suave entre estados do jogo
│   └── InputHandler.java       // Gerenciador de teclado (A/D, W, S, J, K, L, P, ESC)
├── entities
│   ├── Entity.java             // Classe base (posição, velocidade, AABB, vida, estado)
│   ├── Player.java             // Elias Rocha (movimento, pulo, agachar, tiro 3 direções, melee)
│   ├── Zombie.java             // Classe abstrata para zumbis com IA e animação
│   ├── WalkerZombie.java       // Zumbi Caminhante (HP 3, lento, resistente, emboscadas)
│   ├── RunnerZombie.java       // Zumbi Corredor (HP 2, rápido, salto telegrafado)
│   ├── Boss.java               // Classe base de Boss com barra de vida e fases
│   ├── MrXBoss.java            // Boss 1: Sobretudo, investida, socos, fase 2 furiosa
│   ├── TyrantBoss.java         // Boss 2: Garra mutante, onda de choque, coração exposto
│   ├── NemesisBoss.java        // Boss 3: Lança-foguetes, tentáculos, mutação final
│   ├── Projectile.java         // Projéteis (pistola, escopeta, fuzil, lança-chamas, granada, foguetes)
│   ├── Pickup.java             // Itens coletáveis (munição, kit médico, granadas, sucata)
│   └── Particle.java           // Efeitos de partículas (sangue escuro, chamas, faíscas, fumaça)
├── world
│   ├── Level.java              // Gerenciamento dos 1000 metros (32.000 px) da fase
│   ├── Stage1.java             // Floresta do Cedro e Estrada de terra
│   ├── Stage2.java             // Cidade Velha, Delegacia e Clínica Rural
│   ├── Stage3.java             // Bunker Militar e Laboratório Subterrâneo
│   ├── Camera.java             // Câmera com scroll lateral, clamp e screen shake
│   ├── ParallaxBackground.java // 3 camadas de fundo com profundidade
│   ├── Obstacle.java           // Barricadas, carros, arames, barris explosivos, poças
│   ├── Spawner.java            // Gerador de hordas por tela com seed reprodutível
│   ├── DifficultyManager.java  // Curva de dificuldade por tela (screenIndex) e fase
│   └── Checkpoint.java         // Marcos a cada 250 m com salvamento de progresso
├── ui
│   ├── HUD.java                // Vidas, corações, arma, munição, medidor 1000m, ícone do boss
│   ├── MainMenu.java           // Novo Jogo, Seleção de Fase, Controles, Créditos, Sair
│   ├── PauseOverlay.java       // Menu de pausa com retorno instantâneo
│   ├── GameOverOverlay.java    // Tela de morte com opção de reiniciar do checkpoint
│   ├── PhaseClearOverlay.java  // Resumo de fase: tempo, zumbis eliminados, precisão
│   ├── BossHealthBar.java      // Barra de vida imponente no topo com nome do chefe
│   └── CutsceneRenderer.java   // Exibição de quadros de história com efeito typewriter
├── assets
│   ├── AssetLoader.java        // Carregador de imagens, quadros e spritesheets
│   ├── SpriteSheet.java        // Fatiador de sprites e manipulação de frames
│   ├── Animation.java          // Controle de animação por estado e temporização
│   ├── ProceduralSprites.java  // Gerador vetorial de pixel art em alta definição Metal Slug
│   └── SoundSystem.java        // Sintetizador procedural de efeitos sonoros e trilhas arcade
├── story
│   ├── StoryManager.java       // Orquestrador das cutscenes e narrativa oficial
│   ├── StoryCutscene.java      // Definição das cenas (Intro, Pós-Fase 1, Pós-Fase 2, Final)
│   └── Dialogue.java           // Textos com falas oficiais de Elias e rádio
└── util
    ├── Vector2D.java           // Vetores 2D para física e aceleração
    ├── AABB.java               // Caixas colidentes com cálculo de interseção e colisão
    └── MathUtils.java          // Interpolações, clamps e utilitários matemáticos
```

---

## 3. ROTEIRO DE TRABALHO DAS 12 ETAPAS

| Etapa | Foco | Entregáveis |
|---|---|---|
| **1. Estrutura Maven & Game Loop** | Infraestrutura base | `pom.xml`, `Game`, `GameWindow`, `GamePanel`, Game loop a 60 UPS fixos, estados (`StateManager`), `InputHandler` com controles mapeados. |
| **2. Player Completo (Elias Rocha)** | Mecânicas do herói | Movimento horizontal, pulo variável, agachamento (hitbox reduzida), tiro em 3 direções (frente, cima, agachado), 4 armas (Pistola, Escopeta, Fuzil, Lança-chamas), golpe melee de chave inglesa (K), granada/molotov parabólica (L), 5 corações, 1,5s invencibilidade com piscar. |
| **3. Mundo, Câmera e Distância** | Espaço de 1000 metros | Câmera com scroll lateral suave e trava nos 1000m (32.000 px), contador no HUD ("X m / 1000 m"), barra de progresso com ícone do boss, checkpoints a cada 250m (250m, 500m, 750m), parallax em 3 camadas. |
| **4. Zumbis (Walker e Runner)** | Inimigos comuns | Walker (lento, HP 3, emboscadas de ambos os lados, 100 pts); Runner (rápido, HP 2, agachamento telegráfico 0.3s antes do salto/investida, 150 pts). Sangue escuro, animações de morte e IA de perseguição. |
| **5. Obstáculos Recorrentes** | Level Design dinâmico | Barricadas de madeira destrutíveis, carros abandonados, caixotes, arame farpado (dano ao toque), barris explosivos em cadeia, poças tóxicas, vigas/troncos caindo telegrafados com poeira. |
| **6. DifficultyManager & Spawner** | Balanceamento | Cálculo de dificuldade proporcional a `(fase * peso + screenIndex * incremento)`. Aumento progressivo de velocidade dos zumbis (+até 40%), taxa de Runners, densidade de armadilhas e escassez de munição/vida. Random com seed por fase. |
| **7. Fase 1: Floresta & Boss Mr. X** | Primeiro chefe | Cenário de chuva, estrada de terra e floresta sombria. Boss Mr. X: caminhada implacável, socos telegrafados, arremesso de entulho, fase 2 em fúria ao rasgar o sobretudo. Arena no posto abandonado com barris. |
| **8. Fase 2: Cidade & Boss Tyrant** | Segundo chefe | Ruas destruídas, viaturas e hordas densas. Boss Tyrant: garra colossal, pulo com onda de choque sísmica no chão, investida devastadora contra paredes que o atordoa e expõe seu coração vulnerável, invocação de zumbis em vida baixa. |
| **9. Fase 3: Bunker & Boss Nemesis** | Terceiro chefe | Corredores industriais, lasers e gás tóxico. Boss Nemesis em 3 fases: Fase 1 (lança-foguetes e mobilidade aérea), Fase 2 (tentáculos chicoteadores), Fase 3 (mutação apex com regeneração ativa). |
| **10. Cutscenes & História Oficial** | Narrativa completa | Intro com os 6 quadros da história e texto datilografado. Cenas intermediárias com descobertas de documentos e pistas no rádio. Cutscene final emocionante: Elias encontra Marina e Lívia vivas no laboratório de criogenia, abraço familiar e nascer do sol. Frase oficial: *"Enquanto essa casa estiver de pé, ainda existe alguém para salvar."* |
| **11. HUD, Menus, Áudio e Polimento** | UX, áudio e arte | HUD completo com munição, corações e medidor de distância. Menus interativos (Novo Jogo, Controles, Pausa, Game Over com respawn no checkpoint). Efeitos sonoros e músicas arcade sintetizadas em tempo real. Screen shake e partículas intensas. |
| **12. Empacotamento, Teste e README** | Finalização | Build Maven sem falhas, empacotamento em fat JAR executável `java -jar`, verificação de 60 FPS estáveis, documentação completa de execução e controles. |

---

## 4. DESIGN DAS IMAGENS E SPRITES
- Utilizaremos os 6 quadros originais encontrados em alta definição da história de Elias Rocha para a introdução épica.
- Geraremos artes temáticas dedicadas para as cenas intermediárias, bosses e cena final emocionante com o reencontro de Marina, Lívia e Elias.
- Os sprites do Elias seguirão rigorosamente as características visuais: homem careca, barba cheia estilo lenhador, robusto, roupas de trabalho com colete tático, joelheiras e botas.
