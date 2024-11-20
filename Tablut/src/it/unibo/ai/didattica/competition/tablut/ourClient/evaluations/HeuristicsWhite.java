package it.unibo.ai.didattica.competition.tablut.ourClient.evaluations;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.ourClient.GameHelper;

public class HeuristicsWhite extends Heuristics {

        private Float[] weights;

        public HeuristicsWhite(Float[] weights) {
                this.weights = weights;
        }

        public float evaluate(State state) {

                int[] kingPos = GameHelper.getKingPosition(state);
                List<int[]> emptyTiles = GameHelper.populateEmptyList(state);
                state.setTurn(Turn.BLACK);
                List<int[]> blackPaws = GameHelper.populatePawnList(state);
                state.setTurn(Turn.WHITE);
                float alivePawns = numAlive(state);
                float eatenPawns = numEaten(state);
                float escapesAccessible = escapesOpen(emptyTiles, kingPos);
                float freedomKing = kingFreedom(emptyTiles, kingPos);
                float inDangerKing = kingSecurity(blackPaws, emptyTiles, kingPos);

                //System.out.println("Alive pawns score: " + alivePawns + "Eaten Pawns score:" + (-eatenPawns)
                //              + "Accessible escapes: " + escapesAccessible + "Freedom of movement" + freedomKing);
                return 10 * weights[0] * alivePawns - 10 * weights[1] * eatenPawns + weights[2] * escapesAccessible
                                - 1 * inDangerKing;
        }

        // Gives a score depending on how many escape tiles are available to the king.
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
                                .filter(tile -> emptyTiles.stream()
                                                .anyMatch(emptyTile -> Arrays.equals(tile, emptyTile)))
                                .collect(Collectors.toList());
                int escapesOpen = numberReachableGoals(availableEscapingTiles, kingPosition, emptyTiles);
                switch (escapesOpen) {
                        case 0:
                                return -1;
                        case 1:
                                return 1;
                        case 2:
                                return 20;
                        case 3:
                                return 22;
                        case 4:
                                return 24;
                        default:
                                return 0;
                }
        }

}
