package it.unibo.ai.didattica.competition.tablut.ourClient.evaluations;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import java.util.Arrays;
import java.util.List;

public class Heuristics {

    // not that important lower scale in the evaluation
    public static float numAlive(State state) {
        if (state.getTurn().equals(Turn.WHITE)) {
            return ((2 * state.getNumberOf(Pawn.WHITE)) / 9) - 1;
        } else {
            return ((2 * state.getNumberOf(Pawn.BLACK)) / 16) - 1;
        }
    }

    public static float numEaten(State state) {
        if (state.getTurn().equals(Turn.WHITE)) {
            return ((2 * state.getNumberOf(Pawn.BLACK)) / 16) - 1;
        } else {
            return ((2 * state.getNumberOf(Pawn.WHITE)) / 9) - 1;
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

    // Takes the list of goals and start position, counts how many goals can the
    // start reach with one move
    public static int numberToReachGoal(List<int[]> goals, int[] start, List<int[]> empty) {

        int reachedGoals = 0;

        for (int[] goal : goals) {
            if (ableToReach(start, goal, empty)) {
                reachedGoals += 1;
            }
        }
        return reachedGoals;
    }

    // The function checks if the start can reach the goal in one move
    protected static Boolean ableToReach(int[] start, int[] goal, List<int[]> empty) {

        int[][] directions = {
                { 1, 0 },
                { -1, 0 },
                { 0, 1 },
                { 0, -1 }
        };

        for (int[] direction : directions) {
            int x = start[0];
            int y = start[1];

            while (true) {
                x += direction[0];
                y += direction[1];

                // check if out of the board
                if (x < 0 || x > 8 || y < 0 || y > 8) {
                    break;
                }

                // check if it is not an empty tile
                int[] pos = new int[] { x, y };
                if (!empty.stream().anyMatch(tile -> Arrays.equals(pos, tile))) {
                    break;
                }

                // check if on the goal
                if ((x == goal[0]) && (y == goal[1])) {
                    return true;
                }
            }
        }
        return false;
    }
}
