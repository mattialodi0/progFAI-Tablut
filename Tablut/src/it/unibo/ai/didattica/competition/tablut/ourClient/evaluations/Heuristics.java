package it.unibo.ai.didattica.competition.tablut.ourClient.evaluations;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Heuristics {

    // Returns the normalized (between 0,1) number of alive pawns.
    public static float numAlive(State state) {
        if (state.getTurn().equals(Turn.WHITE)) {
            return (float) state.getNumberOf(Pawn.WHITE) / 8;
        } else {
            return (float) state.getNumberOf(Pawn.BLACK) / 16;
        }
    }

    // Returns the normalized (between 0,1) number of eaten pawns. More it eats the
    // lower the score is, in HeuristicsWhite and Black have to be with - sign.
    public static float numEaten(State state) {
        if (state.getTurn().equals(Turn.WHITE)) {
            return (float) (state.getNumberOf(Pawn.BLACK)) / 16;
        } else {
            return (float) (state.getNumberOf(Pawn.WHITE)) / 8;
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
    public static Boolean canReach(int[] start, int[] goal, List<int[]> empty) {

        List<int[]> camps = Arrays.asList(
                // camps on the top
                new int[] { 0, 3 },
                new int[] { 0, 4 },
                new int[] { 0, 5 },
                new int[] { 1, 4 },

                // camps on the bottom
                new int[] { 8, 3 },
                new int[] { 8, 4 },
                new int[] { 8, 5 },
                new int[] { 7, 4 },

                // camps on the left
                new int[] { 3, 0 },
                new int[] { 4, 0 },
                new int[] { 5, 0 },
                new int[] { 4, 1 },

                // camps on the right
                new int[] { 3, 8 },
                new int[] { 4, 8 },
                new int[] { 5, 8 },
                new int[] { 4, 7 });

        List<int[]> mergedList = new ArrayList<>();
        int j = 0;
        if (camps.stream().anyMatch(camp -> Arrays.equals(camp, start))) {
            mergedList = Stream.concat(empty.stream(), camps.stream())
                    .collect(Collectors.toList());
            j = 1;
        }
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
                if (j == 0) {
                    if (!empty.stream().anyMatch(tile -> Arrays.equals(pos, tile))) {
                        break;
                    }
                } else if (j == 1) {
                    if (!mergedList.stream().anyMatch(tile -> Arrays.equals(pos, tile))) {
                        break;
                    }
                }

                // check if the current tile is the goal
                if ((x == goal[0]) && (y == goal[1])) {
                    return true;
                }
            }
        }

        return false;
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

    // Evaluates how capable is the black of attacking the king. How many of the
    // black pawns can touch the goal (king). Is between 0 and 1
    public static float approachingPawns(List<int[]> pawns, int[] goal, List<int[]> empty,
            State state) {
        int pawnCount = 0;

        for (int[] pawn : pawns) {
            if (canComeNear(pawn, goal, empty)) {
                pawnCount += 1;
            }
        }
        if (pawnCount == 0) {
            return -1f;
        }
        return (float) pawnCount / state.getNumberOf(Pawn.BLACK);
    }

    protected static Boolean canComeNear(int[] pawn, int[] goal, List<int[]> empty) {

        List<int[]> camps = Arrays.asList(
                // camps on the top
                new int[] { 0, 3 },
                new int[] { 0, 4 },
                new int[] { 0, 5 },
                new int[] { 1, 4 },

                // camps on the bottom
                new int[] { 8, 3 },
                new int[] { 8, 4 },
                new int[] { 8, 5 },
                new int[] { 7, 4 },

                // camps on the left
                new int[] { 3, 0 },
                new int[] { 4, 0 },
                new int[] { 5, 0 },
                new int[] { 4, 1 },

                // camps on the right
                new int[] { 3, 8 },
                new int[] { 4, 8 },
                new int[] { 5, 8 },
                new int[] { 4, 7 });

        List<int[]> mergedList = new ArrayList<>();
        int j = 0;
        if (camps.stream().anyMatch(camp -> Arrays.equals(camp, pawn))) {
            mergedList = Stream.concat(empty.stream(), camps.stream())
                    .collect(Collectors.toList());
            j = 1;
        }

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
                if (j == 0) {
                    if (!empty.stream().anyMatch(tile -> Arrays.equals(pos, tile))) {
                        break;
                    }
                }else if(j == 1){
                    if (!mergedList.stream().anyMatch(tile -> Arrays.equals(pos, tile))) {
                        break;
                    }
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

    // TODOOOOO
    // -------------------------------------------------------------------------------------
    public static float kingSecurity(List<int[]> blackPawns, List<int[]> myPawns, int[] king) {
        int posCounter = 0;

        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        // King on throne: when more than two black around is bad
        if (Arrays.equals(king, new int[] { 4, 4 })) {
            for(int[] dir: directions){
                int newRow = king[0] + dir[0];
                int newCol = king[1] + dir[1];
                for(int[] bpwan:blackPawns){
                    if (Arrays.equals(bpwan, new int[]{newRow, newCol})){
                        posCounter++;
                    }
                }
            }
            if(posCounter >= 2){
                return 10;
            }
        }

        // King near throne: two black around is bad
        else if(Arrays.equals(king, new int[] { 4, 3 }) || 
                Arrays.equals(king, new int[] { 4, 5 }) ||
                Arrays.equals(king, new int[] { 3, 4 }) ||
                Arrays.equals(king, new int[] { 5, 4 })){
                    for(int[] dir: directions){
                        int newRow = king[0] + dir[0];
                        int newCol = king[1] + dir[1];
                        for(int[] bpwan:blackPawns){
                            if (Arrays.equals(bpwan, new int[]{newRow, newCol})){
                                posCounter++;
                            }
                        }
                    }
                    if (posCounter == 1){
                        return 1;
                    }else if(posCounter == 2) return 10;
                
        }

        // Otherwise: one black near bad
        else{
            for(int[] dir: directions){
                int newRow = king[0] + dir[0];
                int newCol = king[1] + dir[1];
                for(int[] bpwan:blackPawns){
                    if (Arrays.equals(bpwan, new int[]{newRow, newCol})){
                        posCounter++;
                    }
                }
            }
            if (posCounter >= 1){
                return 10;
            }
        }

        // Some bonus points for when white are near?

        return 0;
    }

    // Evaluates the freedom of movement of the king. Depending on the position of
    // the king (on
    // throne, near throne, neither) mult its degrees of freedom. If only
    // one degree of freedom returns a negative value.
    public static float kingFreedom(List<int[]> emptyTiles, int[] king) {

        int free = freedomOfMovement(emptyTiles, king);
        float tolerance = 1;

        // if the king is on the throne has a higher tolerance
        if (Arrays.equals(king, new int[] { 4, 4 })) {
            tolerance = 4;
        }

        // if the king has the throne on one side has a lower tolerance, still better
        // than 1
        if (Arrays.equals(king, new int[] { 5, 4 }) || Arrays.equals(king, new int[] { 3, 4 })
                || Arrays.equals(king, new int[] { 4, 3 }) || Arrays.equals(king, new int[] { 4, 5 })) {
            tolerance = 2.5f;
        }

        float res = 0;
        switch (free) {
            case 3:
                res = 1 * tolerance;
                break;
            case 4:
                res = 1.5f * tolerance;
                break;
            case 2:
                res = 0.5f * tolerance;
                break;
            case 1:
                res = -(0.5f * (1 / tolerance));
                break;
        }
        return res;
    }

    // Checks how many adjacent free tiles a pawn has.
    public static int freedomOfMovement(List<int[]> emptyTiles, int[] pawn) {
        int freeTilesNear = 0;
        for (int[] empty : emptyTiles) {
            if ((Math.abs(empty[0] - pawn[0]) == 1 && empty[1] == pawn[1])
                    || (Math.abs(empty[1] - pawn[1]) == 1 && empty[0] == pawn[0])) {
                freeTilesNear++;
            }
        }
        return freeTilesNear;
    }

    // Evaluates based on the number of possible enemy pawns a player can capture in
    // the next
    // move
    public static float possibleCaptures(List<int[]> myPawns, List<int[]> enemPawns, List<int[]> emptyTiles) {

        int possCaptures = 0;
        // Check how (where we have to move the black pawn) can enemy pawns be captured
        // in the following move and popolate criticalTiles list: here the tiles that
        // lead to the capture of a pawn are stored. I consider only one critical tile
        // for each enemy pawn as is enough for capturing it.

        List<int[]> criticalTiles = new ArrayList<>();
        for (int[] enem : enemPawns) {
            int[] captureTile = capturingTile(enem, emptyTiles);
            if (!Arrays.equals(captureTile, new int[] { -100, -100 })) {
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
        switch (possCaptures) {
            case 0:
                return -0.5f;
            default:
                int n = possCaptures / 2;
                return 0.5f * n + 0.5f;
        }
    }

    // Returns the position where to move if we want to capture the pawn. If there
    // is no possible capture returns {-100,-100}.
    public static int[] capturingTile(int[] pawn, List<int[]> emptyTiles) {

        int[][] nearPawn = {
                new int[] { pawn[0], pawn[1] + 1 },
                new int[] { pawn[0], pawn[1] - 1 },
                new int[] { pawn[0] + 1, pawn[1] },
                new int[] { pawn[0] - 1, pawn[1] } };

        if (emptyTiles.stream()
                .anyMatch(emptyTile -> Arrays.equals(nearPawn[0], emptyTile))
                ^ emptyTiles.stream()
                        .anyMatch(emptyTile -> Arrays.equals(nearPawn[1], emptyTile))) {
            return emptyTiles.stream()
                    .anyMatch(emptyTile -> Arrays.equals(nearPawn[0], emptyTile)) ? nearPawn[0] : nearPawn[1];
        } else if (emptyTiles.stream()
                .anyMatch(emptyTile -> Arrays.equals(nearPawn[2], emptyTile))
                ^ emptyTiles.stream()
                        .anyMatch(emptyTile -> Arrays.equals(nearPawn[3], emptyTile))) {
            return emptyTiles.stream()
                    .anyMatch(emptyTile -> Arrays.equals(nearPawn[2], emptyTile)) ? nearPawn[2] : nearPawn[3];
        }
        return new int[] { -100, -100 };
    }
}
