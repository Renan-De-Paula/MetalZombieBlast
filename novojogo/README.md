# METAL ZOMBIE

**METAL ZOMBIE** é um jogo *Run & Gun 2D* de sobrevivência extrema em Java 21 Puro (estilo Metal Slug), desenvolvido com renderização *pixel-perfect* customizada.

Você joga como Elias Rocha, um mecânico lutando contra zumbis, aberrações e militares durante um surto viral em busca de sua filha sequestrada. O jogo se passa no Vale do Cedro.

## 🚀 Como Jogar em Qualquer Computador

Para rodar o jogo, a única dependência que você ou seus amigos precisam ter no computador é o **Java 21** ou superior.

### Passo 1: Instale o Java 21
Se você não tem o Java, baixe e instale a versão oficial gratuita:
- [Eclipse Adoptium Temurin JDK 21](https://adoptium.net/temurin/releases/?version=21) (Baixe o arquivo `.msi` ou instalador padrão para Windows e marque a opção para adicionar ao `PATH`).

### Passo 2: Rodar o jogo
Basta dar dois cliques no arquivo:
👉 `jogar.bat`

Este script fará uma verificação do seu sistema e abrirá o jogo automaticamente em tela cheia usando a compilação mais recente disponível na pasta `target`.

---

## 🎮 Controles do Jogo

- **A / D**: Andar para Esquerda / Direita
- **W**: Olhar/Mirar para cima
- **S**: Agachar
- **Espaço / J**: Pular (Pulo variável - segure para pular mais alto)
- **K**: Tiro principal / Tiro automático
- **L**: Arremessar Granada (Explosão em área, excelente contra hordas)
- **P**: Alternar Armas (Pistola, Escopeta, Fuzil, Lança-Chamas)
- **O**: Golpe Corpo-a-Corpo (Bater com a Chave Inglesa)
- **ESC**: Pausar Jogo / Sair / Pular Cutscenes

---

## 🏗️ Para Desenvolvedores (Compilando o Código)

Se você deseja alterar o código fonte e gerar uma nova versão:
1. Certifique-se de ter o **Maven (mvn)** instalado e configurado no `PATH` do seu Windows.
2. Certifique-se de ter o **JDK 21** no `JAVA_HOME`.
3. Abra o prompt de comando na pasta do jogo e execute:
   `mvn clean package`
4. O Maven compilará todas as imagens, áudios e classes no arquivo *Fat JAR* executável: `target/ultimo-abrigo-1.0.0.jar`.

---
*Enquanto essa casa estiver de pé, ainda existe alguém para salvar.*
