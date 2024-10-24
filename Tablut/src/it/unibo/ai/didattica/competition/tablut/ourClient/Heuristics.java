package it.unibo.ai.didattica.competition.tablut.ourClient;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import java.util.Arrays;
import java.util.List;

public class Heuristics {
    public static int numAlive(State state) {
        if (state.getTurn().equals(Turn.WHITE)) {
            return state.getNumberOf(Pawn.WHITE) + 1; // because the king is not counted and if we came to this point he
                                                      // is still on the table
        } else {
            return state.getNumberOf(Pawn.BLACK);
        }
    }

    // number of eaten could also not be considered for the white
    public static int numEaten(State state) {
        if (state.getTurn().equals(Turn.WHITE)) {
            return state.getNumberOf(Pawn.BLACK) - 16;
        } else {
            return state.getNumberOf(Pawn.WHITE) - 9;
        }
    }

    public static float convergenceMiddle(List<int[]> pawns) {
        return 0;
    }

    // Maybe this is interesting only for the blacks, the white have to pose more attention to the fact that the king is free
    public static int pawnsNearKing(List<int[]> pawns, int[] kingPosition) {
        List<int[]> toCheckTiles = Arrays.asList(
            new int[]{kingPosition[0]+1, kingPosition[1]},
            new int[]{kingPosition[0]-1, kingPosition[1]},
            new int[]{kingPosition[0], kingPosition[1]+1},
            new int[]{kingPosition[0], kingPosition[1]-1}
        );
        int pawnsNear = (int) toCheckTiles.stream().filter(item1 -> pawns.stream()
                .anyMatch(item2 -> Arrays.equals(item1, item2)))
                .count();
        return pawnsNear;
    }

    public static int numberOfPawnsToReachKing(List<int[]> pawns, List<int[]> empty, int[] kingPosition) {
        return 0;
    }
}
