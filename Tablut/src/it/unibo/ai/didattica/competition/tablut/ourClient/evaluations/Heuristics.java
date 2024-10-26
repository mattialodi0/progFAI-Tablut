package it.unibo.ai.didattica.competition.tablut.ourClient.evaluations;

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

    // minimize the manhattan distance from the center, could be good to consider
    // also the emptyTiles
    public static float convergenceMiddle(List<int[]> pawns) {
        float distance = 0;
        for (int[] pawn : pawns) {
            distance += Math.abs(pawn[0] - 4) + Math.abs(pawn[1] - 4);
        }
        return distance;
    }

    // Maybe this is interesting only for the blacks, the white have to pose more
    // attention to the fact that the king is free
    public static int pawnsNearKing(List<int[]> pawns, int[] kingPosition) {

        int numberPawns = 0;

        for (int[] pawn : pawns) {
            if ((Math.abs(pawn[0] - kingPosition[0]) == 1 && pawn[1] == kingPosition[1])
                    || (Math.abs(pawn[1] - kingPosition[1]) == 1 && pawn[0] == kingPosition[0])) {
                numberPawns++;
            }
        }
        return numberPawns;

        // Less efficient
        /*
         * List<int[]> toCheckTiles = Arrays.asList(
         * new int[] { kingPosition[0] + 1, kingPosition[1] },
         * new int[] { kingPosition[0] - 1, kingPosition[1] },
         * new int[] { kingPosition[0], kingPosition[1] + 1 },
         * new int[] { kingPosition[0], kingPosition[1] - 1 });
         * int pawnsNear = (int) toCheckTiles.stream().filter(item1 -> pawns.stream()
         * .anyMatch(item2 -> Arrays.equals(item1, item2)))
         * .count();
         * return pawnsNear;
         */
    }

    // Not efficient, but I think it could be very helpful.
    public static int numberOfPawnsToReachKing(List<int[]> pawns, List<int[]> empty, int[] kingPosition) {
        int pawnCount = 0;

        for (int[] pawn : pawns) {
            if (canReach(pawn, kingPosition, empty)) {
                pawnCount += 1;
            }
        }
        return pawnCount;
    }

    private static Boolean canReach(int[] pawn, int[] king, List<int[]> empty) {

        int[][] directions = {
                { 1, 0 },
                { -1, 0 },
                { 0, 1 },
                { 0, -1 }
        };
        for (int[] direction : directions) {
            int x = pawn[0];
            int y = pawn[1];

            while (true) {
                x += direction[0];
                y += direction[1];

                // check if it is not an empty tile
                int[] pos = new int[] { x, y };
                if (!empty.stream().anyMatch(tile -> Arrays.equals(pos, tile))) {
                    break;
                }

                if (x < 0 || x > 8 || y < 0 || y > 8) {
                    break;
                }

                // check if near the king
                if ((Math.abs(x - king[0]) == 1 && y == king[1]) ||
                        (Math.abs(y - king[1]) == 1 && x == king[0])) {
                    return true;
                }
            }
        }
        return false;
    }
}
