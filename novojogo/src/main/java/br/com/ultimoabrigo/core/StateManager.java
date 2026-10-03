package br.com.ultimoabrigo.core;

import java.awt.Graphics2D;
import java.util.EnumMap;
import java.util.Map;

/**
 * StateManager - Gerencia a máquina de estados global do jogo.
 */
public class StateManager {

    private final Map<GameState, GameStateHandler> states = new EnumMap<>(GameState.class);
    private GameState currentState = null;
    private GameState previousState = null;

    public StateManager() {
    }

    public void registerState(GameState state, GameStateHandler handler) {
        states.put(state, handler);
    }

    public void setState(GameState newState) {
        if (newState == currentState) {
            return;
        }

        if (currentState != null) {
            GameStateHandler currentHandler = states.get(currentState);
            if (currentHandler != null) {
                currentHandler.exit();
            }
        }

        previousState = currentState;
        currentState = newState;

        GameStateHandler newHandler = states.get(currentState);
        if (newHandler != null) {
            newHandler.enter();
        }
    }

    public void update(InputHandler input) {
        if (currentState != null) {
            GameStateHandler handler = states.get(currentState);
            if (handler != null) {
                handler.update(input);
            }
        }
    }

    public void render(Graphics2D g) {
        if (currentState != null) {
            // Se estiver pausado, pode opcionalmente renderizar o estado anterior por baixo
            if (currentState == GameState.PAUSED && previousState != null) {
                GameStateHandler bgHandler = states.get(previousState);
                if (bgHandler != null) {
                    bgHandler.render(g);
                }
            }

            GameStateHandler handler = states.get(currentState);
            if (handler != null) {
                handler.render(g);
            }
        }
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public GameState getPreviousState() {
        return previousState;
    }

    public GameStateHandler getStateHandler(GameState state) {
        return states.get(state);
    }
}
