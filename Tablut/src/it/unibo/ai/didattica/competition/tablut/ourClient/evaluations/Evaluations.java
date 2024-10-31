package it.unibo.ai.didattica.competition.tablut.ourClient.evaluations;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.ourClient.GameHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Evaluations {

    /* Basic heuristic, normalized between [-1, +1], more pieces -> more points */
    public static float evaluateMaterial(State state, Turn t) {
        float eval = 0;

        if (t == Turn.WHITE) {
            eval = state.getNumberOf(Pawn.WHITE) * 2 - state.getNumberOf(Pawn.BLACK);
        } else {
            eval = state.getNumberOf(Pawn.BLACK) - state.getNumberOf(Pawn.WHITE) * 2;
        }

        // addition of a random factor to cosider different moves
        Random rand = new Random();
        return eval / 16 + (rand.nextFloat() / 10);
    }

    /* Tomaz heuristic */
    public static float evaluateAdvanced(State state, Turn t) {
        if (state.getTurn().equals(Turn.DRAW)) {
            return 0;
        } else if (state.getTurn().equals(Turn.WHITEWIN) && t.equals(Turn.WHITE)) {
            return Float.POSITIVE_INFINITY;
        } else if (state.getTurn().equals(Turn.WHITEWIN) && t.equals(Turn.BLACK)) {
            return Float.NEGATIVE_INFINITY;
        } else if (state.getTurn().equals(Turn.BLACKWIN) && t.equals(Turn.WHITE)) {
            return Float.NEGATIVE_INFINITY;
        } else if (state.getTurn().equals(Turn.BLACKWIN) && t.equals(Turn.BLACK)) {
            return Float.POSITIVE_INFINITY;
        }
        int[] kingPos = GameHelper.getKingPosition(state);
        List<int[]> emptyTiles = GameHelper.populateEmptyList(state);
        // List<int[]> escapeTiles = new ArrayList<>();

        // Common heuristics, everyone will be computed accordingly to who is calling it
        float alivePawns = Heuristics.numAlive(state);
        float eatenPawns = Heuristics.numEaten(state);
        // white heuristics
        if (state.getTurn().equals(State.Turn.WHITE)) {
            float escapesAccessible = HeuristicsWhite.escapesOpen(emptyTiles, kingPos);
            float freedomKing = HeuristicsWhite.freedomOfMovement(emptyTiles, kingPos);
            return freedomKing + escapesAccessible + alivePawns + eatenPawns;

        } else if (state.getTurn().equals(State.Turn.BLACK)) { // black heuristics
            float exitsBlocked = HeuristicsBlack.exitsBlocked(emptyTiles, kingPos);
            float kingReachable = HeuristicsBlack.numberOfPawnsToReachKing(GameHelper.populatePawnList(state),
                    kingPos, emptyTiles, state);
            return alivePawns + eatenPawns;
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
}
