package it.unibo.ai.didattica.competition.tablut.ourClient.ourUtilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class GameHelper {
    private static Set<String> campSet = new HashSet<>();

    // i need to check if that a white pawn does not move on a camp
    public static List<int[]> camps = Arrays.asList(
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


    // use hashmap to find faster if pawn wants to move on a camp o castle
    static {
        for (int[] camp : camps) {
            campSet.add(camp[0] + "," + camp[1]);
        }
    }


    /* Compute the list of available moves of a player */
    public static List<Action> availableMoves(State state) {
        List<Action> moves = new ArrayList<Action>();
        List<int[]> pawns = populatePawnList(state);

        for (int[] p : pawns) {
            //System.out.println("row: "+p[0]+" column: "+p[1]);
            moves.addAll(getPawnMoves(state, p));
        }
        Collections.shuffle(moves);
        return moves;
    }

    public static int[] getKingPosition(State state) {
        int[] res = new int[2];
        for (int i = 0; i < state.getBoard().length; i++) {
            for (int j = 0; j < state.getBoard().length; j++) {
                if (state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())) {
                    res[0] = i;
                    res[1] = j;
                }
            }
        }
        return res;
    }

    public static List<int[]> populatePawnList(State state) {
        List<int[]> pawns = new ArrayList<int[]>();

        int[] buf;
        for (int i = 0; i < state.getBoard().length; i++) {
            for (int j = 0; j < state.getBoard().length; j++) {
                if (state.getTurn() == Turn.WHITE) {
                    if (state.getPawn(i, j).equalsPawn(State.Pawn.WHITE.toString())
                            || state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())) {
                        buf = new int[2];
                        buf[0] = i;
                        buf[1] = j;
                        pawns.add(buf);
                    }
                } else {
                    if (state.getPawn(i, j).equalsPawn(State.Pawn.BLACK.toString())) {
                        buf = new int[2];
                        buf[0] = i;
                        buf[1] = j;
                        pawns.add(buf);
                    }
                }
            }
        }

        return pawns;
    }

    // Returns the empty tiles (not the camps)
    public static List<int[]> populateEmptyList(State state) {
        List<int[]> empty = new ArrayList<int[]>();
        List<int[]> campsList = camps;

        int[] buf;
        for (int i = 0; i < state.getBoard().length; i++) {
            for (int j = 0; j < state.getBoard().length; j++) {
                int[] pawn = {i,j};
                if (state.getPawn(i, j).equalsPawn(State.Pawn.EMPTY.toString()) && !(campsList.stream().anyMatch(camp -> Arrays.equals(camp,pawn)))) {
                    buf = new int[2];
                    buf[0] = i;
                    buf[1] = j;
                    empty.add(buf);
                }
            }
        }

        return empty;
    }

    public static List<Action> getPawnMoves(State state, int[] pawn) {
        List<Action> pawnMoves = new ArrayList<Action>();
        int row = pawn[0];
        int column = pawn[1];
        //if(state.getTurn().equals(State.Turn.BLACK)) System.out.println("row: "+row+" column: "+column);
        // for each move i can go either up, or down, or left, or right
        // i first fix the column, and move up and down, then i fix the row
        // and go left and right.
        // i check every time if i find an obstacle.
        boolean criticalTile = false;
        int [][] dir = {{ 0, 4 }, { 4, 0 }, { 8, 4 }, { 4, 8 }};
        for(int[] d:dir){
            if(row == d[0] && column == d[1]){
                criticalTile = true;
            }
        }
        // Going upward
        for (int i = row - 1; i >= 0; i--) {
            if (isObstacle(state, i, column, criticalTile) || isCamp(state, i, column, criticalTile)){
                break;
            }
            addMoveIfValid(state, pawn, i, column, pawnMoves);
        }

        // Going downward
        for (int i = row + 1; i < state.getBoard().length; i++) {
            if (isObstacle(state, i, column, criticalTile) || isCamp(state, i, column, criticalTile)){
                break;
            }
            addMoveIfValid(state, pawn, i, column, pawnMoves);
        }

        // Going to the left
        for (int j = column - 1; j >= 0; j--) {
            if (isObstacle(state, row, j, criticalTile) || isCamp(state, row, j,criticalTile)){
                    break;
            }
            addMoveIfValid(state, pawn, row, j, pawnMoves);
        }

        // Going to the right
        for (int j = column + 1; j < state.getBoard().length; j++) {
            if (isObstacle(state, row, j, criticalTile) || isCamp(state, row, j, criticalTile)){                    
                    break;
            }
            addMoveIfValid(state, pawn, row, j, pawnMoves);
        }
        //System.out.println("moves: "+pawnMoves);
        return pawnMoves;
    }

    // check if the pawn is moving on an occupied cell
    
    private static boolean isObstacle(State state, int row, int col, boolean isCritical) {
        List<int[]> empty = GameHelper.populateEmptyList(state);
        int[] pos = new int[]{row, col};
    
        // Unisci le caselle vuote e i campi in un Set
        List<int[]> mergedList = new ArrayList<>();
        mergedList = Stream.concat(empty.stream(), camps.stream())
                    .collect(Collectors.toList());
    
        if (isCritical) {
            // Controlla se le coordinate (row, col) sono contenute nel Set
            return !mergedList.stream().anyMatch(tile -> Arrays.equals(pos, tile));
        } else {
            // Controlla se il contenuto della casella è vuoto
            Pawn p = state.getPawn(row, col);
            return !p.equalsPawn(State.Pawn.EMPTY.toString());
        }
    }

    // check if a white pawn is moving on a camp or castle
    // check if a white pawn is moving on a camp or castle
    private static boolean isCamp(State state, int row, int col, boolean isCritical) {
        if (isCritical) {
            // Crea una copia temporanea del campSet per rimuovere le celle critiche vuote
            Set<String> campSet2 = new HashSet<>(campSet);
    
            // Definisci i campi critici
            List<String> criticalCamps = Arrays.asList(
                "0,3", "0,5", "1,4",
                "3,0", "5,0", "4,1",
                "3,8", "5,8", "4,7",
                "8,3", "8,5", "7,4"
            );
    
            // Rimuovi i campi critici dal campSet2 solo se sono vuoti
            for (String critical : criticalCamps) {
                String[] coords = critical.split(",");
                int critRow = Integer.parseInt(coords[0]);
                int critCol = Integer.parseInt(coords[1]);
    
                // Controlla se il campo critico è vuoto
                if (state.getPawn(critRow, critCol).equalsPawn(State.Pawn.EMPTY.toString())) {
                    campSet2.remove(critical);
                }
            }
    
            // Controlla se la cella (row, col) è presente nel campSet2 modificato
            return campSet2.contains(row + "," + col);
        }
    
        // Se non è una mossa critica, controlla nel campSet originale
        return campSet.contains(row + "," + col);
    }

    // if the move is legit, then add the move in the list of moves
    private static void addMoveIfValid(State state, int[] pawn, int targetRow, int targetCol, List<Action> moves) {
        try {
            String from = state.getBox(pawn[0], pawn[1]);
            String to = state.getBox(targetRow, targetCol);

            Action move = new Action(from, to, state.getTurn());

            moves.add(move);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    
    public static float getKingOpenFiles(State state) {
        int[] king_pos = getKingPosition(state);
        int open = 4;

        int i = 0;
        i = king_pos[1]-1;
        while(i >= 0) {
            if(state.getPawn(king_pos[0], i) == Pawn.EMPTY)
                i--;
            else {
                open--;
                break;
            }
        }

        i = king_pos[1]+1;
        while(i < 9) {
            if(state.getPawn(king_pos[0], i) == Pawn.EMPTY)
                i++;
            else {
                open--;
                break;
            }
        }

        i = king_pos[0]-1;
        while(i >= 0) {
            if(state.getPawn(i, king_pos[1]) == Pawn.EMPTY)
                i--;
            else {
                open--;
                break;
            }
        }

        i = king_pos[0]-1;
        while(i < 9) {
            if(state.getPawn(i, king_pos[1]) == Pawn.EMPTY)
                i++;
            else {
                open--;
                break;
            }
        }
    
        switch (open) {
            case 0:
                    return -1;
            case 1:
                    return 0.2f;
            case 2:
                    return 1;
            case 3:
                    return 1;
            case 4:
                    return 1;
            default:
                    return 0;
    }
    }

}
