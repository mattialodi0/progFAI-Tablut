package it.unibo.ai.didattica.competition.tablut.ourClient.evaluations;

import java.util.List;

public class HeuristicsBlack extends Heuristics {

    public static int exitsBlocked(List<int[]> emptyTiles, List<int[]> escapeTiles, int[] kingPosition) {
        return 0;
    }

    // Manhattan distance
    public static float distanceFromKing(List<int[]> emptyTiles, List<int[]> blackPawns, int[] kingPosition) {
        return 0;
    }
}
