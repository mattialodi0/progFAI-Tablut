package it.unibo.ai.didattica.competition.tablut.ourClient.interfaces;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;


public interface TreeSearch {
    /* Alg. to search the game tree for the best move */
<<<<<<< HEAD
    public Action searchTree(State state);  //Hpw tp be able to write another class that uses different parameters?
=======
    Action searchTree(State state);
>>>>>>> main

    /* Evaluates a state returning a float value */
    float evaluate(State state);
    
     Boolean hasMoreTime();
} 