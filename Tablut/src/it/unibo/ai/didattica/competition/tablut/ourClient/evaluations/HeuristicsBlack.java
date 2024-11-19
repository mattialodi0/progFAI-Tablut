package it.unibo.ai.didattica.competition.tablut.ourClient.evaluations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.ourClient.GameHelper;

public class HeuristicsBlack extends Heuristics {

    private Float[] weights;

    public HeuristicsBlack(Float[] weights) {
        this.weights = weights;
    }

    public float evaluate(State state) {

        int[] kingPos = GameHelper.getKingPosition(state);
        List<int[]> emptyTiles = GameHelper.populateEmptyList(state);

        float alivePawns = numAlive(state);
        float eatenPawns = numEaten(state);
        float exitsBlocked = exitsBlocked(emptyTiles, kingPos);
        float attackingTheKing = approachingPawns(GameHelper.populatePawnList(state),
                kingPos, emptyTiles, state);
        float presenceAroundKing = closingKing(GameHelper.populatePawnList(state), kingPos);
        State newState = state.clone();
        newState.setTurn(state.getTurn().equals(Turn.WHITE) ? Turn.BLACK : Turn.WHITE);
        float possCaptures = possibleCaptures(GameHelper.populatePawnList(state),
                GameHelper.populatePawnList(newState), emptyTiles);
        // System.out.println("Alive pawns score: " + alivePawns + "Eaten Pawns score:"
        // + (-eatenPawns)
        // + "Exits blocked: " + exitsBlocked + "Attacking the king: " +
        // attackingTheKing + "Presence around king: "
        // + presenceAroundKing + "Poss captures: " + possCaptures);
        float score = - 10 * weights[0] * alivePawns + 10 * weights[1] * eatenPawns - weights[2] * attackingTheKing
                - weights[3] * exitsBlocked - weights[4] * presenceAroundKing - weights[5] * possCaptures;
        return score;

        // return weights[0] * alivePawns - weights[1] * eatenPawns;
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
                .filter(tile -> emptyTiles.stream()
                        .anyMatch(emptyTile -> Arrays.equals(tile, emptyTile)))
                .collect(Collectors.toList());
        int escapesOpen = numberReachableGoals(availableEscapingTiles, kingPosition, emptyTiles);
        switch (escapesOpen) {
            case 4:
                return -20;
            case 3:
                return -18;
            case 2:
                return -16;
            case 1:
                return -5;
            case 0:
                return 5;
            default:
                return 0;
        }
    }

    // Evaluates based on how many black tiles are near the king
    public static float closingKing(List<int[]> pawns, int[] kingPosition) {
        int nearPawns = pawnsNearKing(pawns, kingPosition);

        // if the king is on the throne the black must have more pawns around him
        if (Arrays.equals(kingPosition, new int[] { 4, 4 })) {
            return nearPawns + 2;
        } else if ((Arrays.equals(kingPosition, new int[] { 3, 4 }) || Arrays.equals(kingPosition, new int[] { 5, 4 })
                || Arrays.equals(kingPosition, new int[] { 4, 5 })
                || Arrays.equals(kingPosition, new int[] { 4, 3 }))) {
            return nearPawns + 1;
        } else {
            return nearPawns;
        }
    }
}
