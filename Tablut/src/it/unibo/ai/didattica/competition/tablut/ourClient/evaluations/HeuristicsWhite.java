package it.unibo.ai.didattica.competition.tablut.ourClient.evaluations;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.ourClient.GameHelper;

public class HeuristicsWhite extends Heuristics {

        private double[] weights;

        public HeuristicsWhite(double[] weights) {
                this.weights = weights;
        }

        public double evaluate(State state) {

                int[] kingPos = GameHelper.getKingPosition(state);
                List<int[]> emptyTiles = GameHelper.populateEmptyList(state);

                float alivePawns = numAlive(state);
                float eatenPawns = numEaten(state);
                float escapesAccessible = escapesOpen(emptyTiles, kingPos);
                float freedomKing = freedomOfMovement(emptyTiles, kingPos);

                return weights[0] * alivePawns + weights[1] * eatenPawns + weights[2] * escapesAccessible
                                + weights[3] * freedomKing;
        }

        // How many escape tiles can the king reach in one move.
        public static float escapesOpen(List<int[]> emptyTiles, int[] kingPosition) {
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
                int escapesOpen = numberReachableGoals(availableEscapingTiles, kingPosition, emptyTiles);
                return escapesOpen;
        }

}
