package it.unibo.ai.didattica.competition.tablut.ourClient.interfaces;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;


public interface TreeSearch {
    /* Alg. to search the game tree for the best move */
    Action searchTree(State state);

    Boolean hasMoreTime();
} 