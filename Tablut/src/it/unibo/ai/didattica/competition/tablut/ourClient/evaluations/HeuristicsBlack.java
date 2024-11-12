package it.unibo.ai.didattica.competition.tablut.ourClient.evaluations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.ourClient.GameHelper;

public class HeuristicsBlack extends Heuristics {

    private double[] weights;

    public HeuristicsBlack(double[] weights) {
        this.weights = weights;
    }

    public double evaluate(State state) {

        int[] kingPos = GameHelper.getKingPosition(state);
        List<int[]> emptyTiles = GameHelper.populateEmptyList(state);

        float alivePawns = numAlive(state);
        float eatenPawns = numEaten(state);
        float exitsBlocked = exitsBlocked(emptyTiles, kingPos);
        float kingReachable = numberReachableGoals(GameHelper.populatePawnList(state),
                kingPos, emptyTiles);
        int nearKing = pawnsNearKing(GameHelper.populatePawnList(state), kingPos);
        State newState = state.clone();
        newState.setTurn(state.getTurn().equals(Turn.WHITE) ? Turn.BLACK : Turn.WHITE);
        int possCaptures = possibleCaptures(GameHelper.populatePawnList(state),
                GameHelper.populatePawnList(newState), emptyTiles);
        return weights[0] * alivePawns + weights[1] * eatenPawns + weights[2] * kingReachable
                + weights[3] * exitsBlocked + weights[4] * nearKing + weights[5] * possCaptures;
    }

    // Computes the number of exits the king can reach and returns 4 - the number.
    // Is 0 if the king can reach 4 exits, and 4 if all the exits are closed for the
    // king.
    public static float exitsBlocked(List<int[]> emptyTiles, int[] kingPosition) {
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
        return 4 - escapesOpen;
    }

    // Returns the number of black pawns near the king
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
}
