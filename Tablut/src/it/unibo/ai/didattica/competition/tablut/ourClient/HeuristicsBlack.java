package it.unibo.ai.didattica.competition.tablut.ourClient;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import java.util.List;

public class HeuristicsBlack {

    // al posto delle distanze meglio ragionare per libere diagonali

    public static float convergenceMiddle(List<int[]> blackPawns) {
        return 0;
    }

    public static int canReachKing(List<int[]> blackPawns, List<int[]> emptyTiles, int[] kingPosition) {
        return 0;
    }

    public static int exitsBlocked(List<int[]> emptyTiles, List<int[]> escapeTiles, int[] kingPosition) {
        return 0;
    }

    public static int numAlive(State state) {
        return state.getNumberOf(Pawn.BLACK);
    }

    public static int numEaten(State state) {
        return state.getNumberOf(Pawn.WHITE) - 16;
    }
}
