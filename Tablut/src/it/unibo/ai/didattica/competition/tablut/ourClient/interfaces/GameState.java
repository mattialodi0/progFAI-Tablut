package it.unibo.ai.didattica.competition.tablut.ourClient.interfaces;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public interface GameState {
    Action[] availableMoves(State state);
    State makeMove(State state, int[] move);
    Boolean win(State state);
}
