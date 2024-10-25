package it.unibo.ai.didattica.competition.tablut.ourClient.evaluations;

import java.util.Random;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class Evaluations {
    
    /* Basic heuristic, normalized between [-1, +1], more pieces -> more points */
    public static float evaluateMaterial(State state) {
        float eval = 0;

        if (state.getTurn() == Turn.WHITE) {
            eval = state.getNumberOf(Pawn.WHITE) * 2 - state.getNumberOf(Pawn.BLACK);
        } else {
            eval = state.getNumberOf(Pawn.BLACK) - state.getNumberOf(Pawn.WHITE) * 2;
        }

        // addition of a random factor to cosider different moves
        return eval / 16 + (new Random().nextInt(10)/10); 
    }
}
