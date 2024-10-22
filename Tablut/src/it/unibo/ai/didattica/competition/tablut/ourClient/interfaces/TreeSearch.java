package it.unibo.ai.didattica.competition.tablut.ourClient.interfaces;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;


public interface TreeSearch {
    /* Alg. to search the game tree for the best move */
    public Action searchTree(State state);

    /* Evaluates a state returning a float value */
    public float evaluate(State state);
    
    public  Boolean hasMoreTime();
} 