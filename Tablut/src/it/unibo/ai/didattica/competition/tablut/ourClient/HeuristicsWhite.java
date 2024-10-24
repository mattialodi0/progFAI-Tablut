package it.unibo.ai.didattica.competition.tablut.ourClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HeuristicsWhite extends Heuristics {

    // number of clear paths to the escape tiles
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
        
        return 1;
    }

    public static int freedomOfMovement(List<int[]> emptyTiles, int[] kingPosition) {
        List<int[]> toCheckTiles = Arrays.asList(
                new int[] { kingPosition[0] + 1, kingPosition[1] },
                new int[] { kingPosition[0] - 1, kingPosition[1] },
                new int[] { kingPosition[0], kingPosition[1] + 1 },
                new int[] { kingPosition[0], kingPosition[1] - 1 });
        int openDirections = (int) toCheckTiles.stream().filter(item1 -> emptyTiles.stream()
                .anyMatch(item2 -> Arrays.equals(item1, item2)))
                .count();
        return openDirections;
    }
}
