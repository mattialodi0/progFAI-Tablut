package it.unibo.ai.didattica.competition.tablut.ourClient.evaluations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.ourClient.GameHelper;
import it.unibo.ai.didattica.competition.tablut.ourClient.Heuristics;
import it.unibo.ai.didattica.competition.tablut.ourClient.HeuristicsBlack;
import it.unibo.ai.didattica.competition.tablut.ourClient.HeuristicsWhite;

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

    
    public static float evaluateAdvanced(State state, Turn t) {
        if (state.getTurn().equals(Turn.DRAW)) {
            return 0;
        } else if (state.getTurn().equals(Turn.WHITEWIN) && t.equals(Turn.WHITE)) {
            return 1;
        } else if (state.getTurn().equals(Turn.WHITEWIN) && t.equals(Turn.BLACK)) {
            return -1;
        } else if (state.getTurn().equals(Turn.BLACKWIN) && t.equals(Turn.WHITE)) {
            return -1;
        } else if (state.getTurn().equals(Turn.BLACKWIN) && t.equals(Turn.BLACK)) {
            return 1;
        }
        int[] kingPos = GameHelper.getKingPosition(state);
        List<int[]> emptyTiles = GameHelper.populateEmptyList(state);
        List<int[]> escapeTiles = new ArrayList<>();

        // Common heuristics, everyone will be computed accordingly to who is calling it
        int alivePawns = Heuristics.numAlive(state);
        int eatenPawns = Heuristics.numEaten(state);
        int kingReachable = Heuristics.numberOfPawnsToReachKing(GameHelper.populatePawnList(state), emptyTiles,
                kingPos);
        float conv = Heuristics.convergenceMiddle(GameHelper.populatePawnList(state));

        // white heuristics
        if (state.getTurn().equals(State.Turn.WHITE)) {
            int escapes = HeuristicsWhite.escapesOpen(emptyTiles, kingPos);
            int directions = HeuristicsWhite.freedomOfMovement(emptyTiles, kingPos);
            // Normalize it between 0 - 1
            return directions - conv + kingReachable + escapes + alivePawns + eatenPawns;
        } else if (state.getTurn().equals(State.Turn.BLACK)) { // black heuristics
            int exitsBlocked = HeuristicsBlack.exitsBlocked(emptyTiles, escapeTiles, kingPos);
            // Normalize between 0 - 1
            return kingReachable - conv + exitsBlocked + alivePawns + eatenPawns;
        }
        return 0;
    }
}
