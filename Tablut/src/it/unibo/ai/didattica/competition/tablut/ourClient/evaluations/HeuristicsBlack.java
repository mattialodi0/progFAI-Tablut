package it.unibo.ai.didattica.competition.tablut.ourClient.evaluations;

import java.util.Arrays;
import java.util.List;

public class HeuristicsBlack extends Heuristics {

    public static int exitsBlocked(List<int[]> emptyTiles, int[] kingPosition) {
        return (16 - HeuristicsWhite.escapesOpen(emptyTiles, kingPosition));
    }

    // How many black tiles are in the close proximity of the king
    public static int pawnsNearKing(List<int[]> pawns, int[] kingPosition) {

        int numberPawns = 0;
  
        for (int[] pawn : pawns) {
            if ((Math.abs(pawn[0] - kingPosition[0]) == 1 && pawn[1] == kingPosition[1])
                    || (Math.abs(pawn[1] - kingPosition[1]) == 1 && pawn[0] == kingPosition[0])) {
                numberPawns++;
            }
        }
        return numberPawns;
    }

    // The manhattan distance of every black pawn from the king. The empty tiles are
    // useful if we will consider also where the blacks can move
    public static float distanceFromKing(List<int[]> emptyTiles, List<int[]> blackPawns, int[] kingPosition) {
        return 0;
    }

    
    public static int numberOfPawnsToReachKing(List<int[]> pawns, int[] kingPosition, List<int[]> empty) {
        int pawnCount = 0;

        for (int[] pawn : pawns) {
            if (canComeNearKing(pawn, kingPosition, empty)) {
                pawnCount += 1;
            }
        }
        return pawnCount;
    }

    // Checks how many pawns can touch the king with one move. Notice that I didn't
    protected static Boolean canComeNearKing(int[] pawn, int[] king, List<int[]> empty) {

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
