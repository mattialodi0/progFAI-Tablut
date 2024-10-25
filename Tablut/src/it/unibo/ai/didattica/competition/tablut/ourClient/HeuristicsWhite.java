package it.unibo.ai.didattica.competition.tablut.ourClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HeuristicsWhite extends Heuristics {

        // add something that helps the other pawns and not only the king

        // add something that considers only the distance of the king from the nearest
        // escape tile. Manhattan distance

        // How much escape tiles can be reached
        public static int escapesOpen(List<int[]> emptyTiles, int[] kingPosition) {
                List<int[]> escapingTiles = Arrays.asList(
                                new int[] { 0, 1 },
                                new int[] { 0, 2 },
                                new int[] { 0, 6 },
                                new int[] { 0, 7 },
                                new int[] { 1, 0 },
                                new int[] { 1, 8 },
                                new int[] { 2, 0 },
                                new int[] { 2, 8 },
                                new int[] { 6, 8 },
                                new int[] { 7, 8 },
                                new int[] { 6, 0 },
                                new int[] { 7, 0 },
                                new int[] { 8, 1 },
                                new int[] { 8, 2 },
                                new int[] { 8, 6 },
                                new int[] { 8, 7 });
                List<int[]> availableEscapingTiles = escapingTiles.stream()
                                .filter(emptyTiles::contains)
                                .collect(Collectors.toList());
                int escapesOpen = numberOfPawnsToReachKing(availableEscapingTiles, emptyTiles, kingPosition);
                return escapesOpen;
        }

        public static int freedomOfMovement(List<int[]> emptyTiles, int[] kingPosition) {

                // Maybe better because the other way it was a double scan. Depends how much
                // more efficient are the streams
                int freeTilesNear = 0;
                for (int[] empty : emptyTiles) {
                        if ((Math.abs(empty[0] - kingPosition[0]) == 1 && empty[1] == kingPosition[1])
                                        || (Math.abs(empty[1] - kingPosition[1]) == 1 && empty[0] == kingPosition[0])) {
                                freeTilesNear++;
                        }
                }
                return freeTilesNear;
                /*
                 * List<int[]> toCheckTiles = Arrays.asList(
                 * new int[] { kingPosition[0] + 1, kingPosition[1] },
                 * new int[] { kingPosition[0] - 1, kingPosition[1] },
                 * new int[] { kingPosition[0], kingPosition[1] + 1 },
                 * new int[] { kingPosition[0], kingPosition[1] - 1 });
                 * int openDirections = (int) toCheckTiles.stream().filter(tileToCheck ->
                 * emptyTiles.stream()
                 * .anyMatch(item2 -> Arrays.equals(tileToCheck, item2)))
                 * .count();
                 * return openDirections;
                 */
        }
}
