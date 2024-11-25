package it.unibo.ai.didattica.competition.tablut.ourClient.evaluations;

import java.util.List;
import java.util.Random;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.ourClient.GameHelper;

public class Evaluations {

    /* Basic heuristic, normalized between, more pieces -> more points */
    public static float evaluateMaterial(State state) {
        if (state.getTurn().equals(Turn.DRAW)) {
            return 0;
        } else if (state.getTurn().equals(Turn.WHITEWIN)) {
            return Float.POSITIVE_INFINITY;
        } else if (state.getTurn().equals(Turn.BLACKWIN)) {
            return Float.NEGATIVE_INFINITY;
        }

        float eval = 0;

        // Random random = new Random();
        eval = ((state.getNumberOf(Pawn.WHITE) * 2) - state.getNumberOf(Pawn.BLACK));// + ((random.nextFloat()-0.5f)/1000);

        return (float) Math.atan(eval*5); 
        // return eval;
    }

    /* Tomaz heuristic */
    public static float evaluateAdvanced(State state, Turn t) {
        if (state.getTurn().equals(Turn.DRAW)) {
            return 0;
        } else if (state.getTurn().equals(Turn.WHITEWIN)) {
            return Float.POSITIVE_INFINITY;
        } else if (state.getTurn().equals(Turn.BLACKWIN)) {
            return Float.NEGATIVE_INFINITY;
        }

        int[] kingPos = GameHelper.getKingPosition(state);
        List<int[]> emptyTiles = GameHelper.populateEmptyList(state);
        // List<int[]> escapeTiles = new ArrayList<>();

        if (state.getTurn().equals(Turn.WHITE)){
            Float[] gameWeights = new Float[4];

            gameWeights[0] = 35f;
            gameWeights[1] = 18f;
            gameWeights[2] = 42f;
            gameWeights[3] = 5.0f;

            HeuristicsWhite heuristic = new HeuristicsWhite(gameWeights);
            return heuristic.evaluate(state);    
        } else if(state.getTurn().equals(Turn.BLACK)){
            Float[] gameWeights = new Float[6];

            gameWeights[0] = 35f;
            gameWeights[1] = 45.0f;
            gameWeights[2] = 5.0f;
            gameWeights[3] = 15.0f;
            gameWeights[4] = 15.0f;
            gameWeights[5] = 9.0f;

            HeuristicsBlack heuristic = new HeuristicsBlack(gameWeights);
            return heuristic.evaluate(state);
        }
        return 0;
    }

    /*
     * Patient evaluation for white, tries to keep the position and waits, not
     * openiing lines to the king
     */
}
