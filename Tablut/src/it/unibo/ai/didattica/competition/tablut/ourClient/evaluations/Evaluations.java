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

        Random random = new Random();
        eval = ((state.getNumberOf(Pawn.WHITE) * 2) - state.getNumberOf(Pawn.BLACK)) + (random.nextFloat()/1000);

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

            gameWeights[0] = 35.1f;
            gameWeights[1] = 18f;
            gameWeights[2] = 5f;
            gameWeights[3] = 42.0f;

            HeuristicsWhite heuristic = new HeuristicsWhite(gameWeights);

            return heuristic.evaluate(state);    
        } else if(state.getTurn().equals(Turn.BLACK)){
            Float[] gameWeights = new Float[6];

            gameWeights[0] = 25.2f;
            gameWeights[1] = 45.0f;
            gameWeights[2] = 15.0f;
            gameWeights[3] = 10.0f;
            gameWeights[4] = 15.0f;
            gameWeights[5] = 20.0f;

            HeuristicsBlack heuristic = new HeuristicsBlack(gameWeights);
            return heuristic.evaluate(state);
        }
        return 0;
    }

    /*
     * Patient evaluation for white, tries to keep the position and waits, not
     * openiing lines to the king
     */
    public static float evaluatePatient(State state) {
        return evaluatePatient(state, Turn.WHITE);
    }

    public static float evaluatePatient(State state, Turn t) {
        // TODO
        return 0f;
    }

    /* Aggressive evaluation for black, always tries to capture pieces */
    public static float evaluateAggressive(State state) {
        return evaluateAggressive(state, Turn.BLACK);
    }

    public static float evaluateAggressive(State state, Turn t) {
        // TODO
        return 0f;
    }

    public static float evaluateAlgiseWhite(State state) {
        int black_pawns = 0;
        int white_pawns = 0;
        int free_way_for_king = 0;
        int black_near_king = 0;
        int king_pos = 0;
        int strategic_free = 0;

        return (float) ((black_pawns * 12) + (white_pawns * 22) + (free_way_for_king * 50) + (black_near_king * 6)
                + (king_pos * 0.4) + (strategic_free));
    }

    public static float evaluateAlgiseBlack(State state) {
        int black_pawns = 0;
        int white_pawns = 0;
        int free_way_for_king = 0;
        int black_near_king = 0;
        int surround = 0;

        return (float) ((black_pawns * 5) + (white_pawns * 10) + (free_way_for_king * 15) + (black_near_king * 9)
                + (surround * 900));
    }
}
