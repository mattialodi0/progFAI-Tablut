package it.unibo.ai.didattica.competition.tablut.ourClient.evaluations;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Heuristics {

    // Returns the normalized (between 0,2) number of alive pawns.
    public static float numAlive(State state) {
        if (state.getTurn().equals(Turn.WHITE)) {
            return state.getNumberOf(Pawn.WHITE) / 9;
        } else {
            return state.getNumberOf(Pawn.BLACK) / 16;
        }
    }

    // Returns the normalized (between 0,2) number of eaten pawns.
    public static float numEaten(State state) {
        if (state.getTurn().equals(Turn.WHITE)) {
            return (16 - state.getNumberOf(Pawn.BLACK)) / 16;
        } else {
            return (9 - state.getNumberOf(Pawn.WHITE)) / 9;
        }
    }

    // minimize the manhattan distance from the center. I don't think it's good
    public static float convergenceMiddle(List<int[]> pawns) {
        float distance = 0;
        for (int[] pawn : pawns) {
            distance += Math.abs(pawn[0] - 4) + Math.abs(pawn[1] - 4);
        }
        return distance;
    }

    // Takes the list of goals and start position, counts how many goals can the
    // start reach with one move.
    public static int numberReachableGoals(List<int[]> goals, int[] start, List<int[]> empty) {

        int reachedGoals = 0;

        for (int[] goal : goals) {
            if (canReach(start, goal, empty)) {
                reachedGoals += 1;
            }
        }
        return reachedGoals;
    }

    // The function checks if the start can reach the goal in one move
    protected static Boolean canReach(int[] start, int[] goal, List<int[]> empty) {

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

                int[] pos = new int[] { x, y };
                // stream operation returns true if the current tile is in empty, if not empty
                // we should change direction
                if (!empty.stream().anyMatch(tile -> Arrays.equals(pos, tile))) {
                    break;
                }

                // check if the current tile is the goal
                if ((x == goal[0]) && (y == goal[1])) {
                    return true;
                }
            }
        }

        return false;
    }

    // Returns the number of pawns that cans can touch the goal with one move,
    // divided by the number of player's alive pawns.
    public static float approachingPawns(List<int[]> pawns, int[] goal, List<int[]> empty,
            State state) {
        int pawnCount = 0;

        for (int[] pawn : pawns) {
            if (canComeNear(pawn, goal, empty)) {
                pawnCount += 1;
            }
        }
        return pawnCount / numAlive(state);
    }

    protected static Boolean canComeNear(int[] pawn, int[] goal, List<int[]> empty) {

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

                // break if the current position is not an empty tile
                int[] pos = new int[] { x, y };
                if (!empty.stream().anyMatch(tile -> Arrays.equals(pos, tile))) {
                    break;
                }

                if (x < 0 || x > 8 || y < 0 || y > 8) {
                    break;
                }

                // check if near the goal
                if ((Math.abs(x - goal[0]) == 1 && y == goal[1]) ||
                        (Math.abs(y - goal[1]) == 1 && x == goal[0])) {
                    return true;
                }
            }
        }
        return false;
    }

    // Returns the number of adjacent free tiles
    public static float freedomOfMovement(List<int[]> emptyTiles, int[] pawn) {
        int freeTilesNear = 0;
        for (int[] empty : emptyTiles) {
            if ((Math.abs(empty[0] - pawn[0]) == 1 && empty[1] == pawn[1])
                    || (Math.abs(empty[1] - pawn[1]) == 1 && empty[0] == pawn[0])) {
                freeTilesNear++;
            }
        }
        return freeTilesNear;
    }

    // Returns the number of possible enemy pawns a player can capture in the next
    // move
    public static int possibleCaptures(List<int[]> myPawns, List<int[]> enemPawns, List<int[]> emptyTiles) {

        int possCaptures = 0;
        // Check how (where we have to move the black pawn) can enemy pawns be captured
        // in the following move and popolate criticalTiles list: here the tiles that
        // lead to the capture of a pawn are stored. I consider only one critical tile
        // for each enemy pawn as is enough for capturing it.

        List<int[]> criticalTiles = new ArrayList<>();
        for (int[] enem : enemPawns) {
            int[] captureTile = capturingTile(enem, emptyTiles);
            if (!captureTile.equals(new int[] { -100, -100 })) {
                criticalTiles.add(captureTile);
            }
        }
        // For every critical tile check if one of my pawns can reach it. If yes
        // increment possCaptures and check for another critical tile.
        for (int[] dang : criticalTiles) {
            for (int[] pawn : myPawns) {
                if (canReach(pawn, dang, emptyTiles)) {
                    possCaptures += 1;
                    break;
                }
            }
        }
        return possCaptures;
    }

    // Returns the position where to move if we want to capture the pawn. If there
    // is no possible capture returns {-100,-100}.
    public static int[] capturingTile(int[] pawn, List<int[]> emptyTiles) {

        int[][] nearPawn = {
                new int[] { pawn[0], pawn[1] + 1 },
                new int[] { pawn[0], pawn[1] - 1 },
                new int[] { pawn[0] + 1, pawn[1] },
                new int[] { pawn[0] - 1, pawn[1] } };

        if (emptyTiles.contains(nearPawn[0]) ^ emptyTiles.contains(nearPawn[1])) {
            return emptyTiles.contains(nearPawn[0]) ? nearPawn[0] : nearPawn[1];
        } else if (emptyTiles.contains(nearPawn[2]) ^ emptyTiles.contains(nearPawn[3])) {
            return emptyTiles.contains(nearPawn[2]) ? nearPawn[2] : nearPawn[3];
        }
        return new int[] { -100, -100 };
    }
}
