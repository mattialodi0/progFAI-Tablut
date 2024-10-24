package it.unibo.ai.didattica.competition.tablut.ourClient;

import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;

public class Heuristics {
        public static int numAlive(State state) {
        return state.getNumberOf(Pawn.WHITE);
    }

    public static int numEaten(State state) {
        return state.getNumberOf(Pawn.BLACK) - 16;
    }

    public static float convergenceMiddle(List<int[]> pawns) {
        return 0;
    }

    public static int numberOfPawnsToReachKing(List<int[]> pawns, int[] kingPosition) {
        return 0;
    }
}
