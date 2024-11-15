package it.unibo.ai.didattica.competition.tablut.ourClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class GameHelper {
    private static Turn playerColor;
    private static Game rules;
    private static Set<String> campSet = new HashSet<>();

    // i need to check if that a white pawn does not move on a camp
    private static int[][] camps = {
            // camps on the top
            { 0, 3 },
            { 0, 4 },
            { 0, 5 },
            { 1, 4 },

            // camps on the bottom
            { 8, 3 },
            { 8, 4 },
            { 8, 5 },
            { 7, 4 },

            // camps on the left
            { 3, 0 },
            { 4, 0 },
            { 5, 0 },
            { 4, 1 },

            // camps on the right
            { 3, 8 },
            { 4, 8 },
            { 5, 8 },
            { 4, 7 },

            // castle tail
            { 4, 4 },

    };

    // use hashmap to find faster if pawn wants to move on a camp o castle
    static {
        for (int[] camp : camps) {
            campSet.add(camp[0] + "," + camp[1]);
        }
    }

    public GameHelper(Turn t) {
        playerColor = t;
    }

    /* Compute the list of available moves of a player */
    public static List<Action> availableMoves(State state) {
        List<Action> moves = new ArrayList<Action>();
        List<int[]> pawns = populatePawnList(state);

        for (int[] p : pawns) {
            moves.addAll(getPawnMoves(state, p));
        }

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
        List<int[]> campsList = Arrays.asList(camps);

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

        // for each move i can go either up, or down, or left, or right
        // i first fix the column, and move up and down, then i fix the row
        // and go left and right.
        // i check every time if i find an obstacle.

        // Going upward
        for (int i = row - 1; i >= 0; i--) {
            if (isObstacle(state, i, column) || isCamp(i, column))
                break;

            addMoveIfValid(state, pawn, i, column, pawnMoves);
        }

        // Going downward
        for (int i = row + 1; i < state.getBoard().length; i++) {
            if (isObstacle(state, i, column) || isCamp(i, column))
                break;

            addMoveIfValid(state, pawn, i, column, pawnMoves);
        }

        // Going to the left
        for (int j = column - 1; j >= 0; j--) {
            if (isObstacle(state, row, j) || isCamp(row, j))
                break;

            addMoveIfValid(state, pawn, row, j, pawnMoves);
        }

        // Going to the right
        for (int j = column + 1; j < state.getBoard().length; j++) {
            if (isObstacle(state, row, j) || isCamp(row, j))
                break;

            addMoveIfValid(state, pawn, row, j, pawnMoves);
        }
        return pawnMoves;
    }


    /* serve per SemiRandom per vedere se una mossa è valida o no. E' uguale al metodo sopra ma ritorna true or false */
    public static boolean canPawnMove(State state, int[] pawn, int prow, int pcol) {
        List<Action> pawnMoves = new ArrayList<Action>();
        int row = pawn[0];
        int column = pawn[1];

        // for each move i can go either up, or down, or left, or right
        // i first fix the column, and move up and down, then i fix the row
        // and go left and right.
        // i check every time if i find an obstacle.

        // Going upward
        for (int i = row - 1; i >= 0; i--) {
            if (isObstacle(state, i, column) || isCamp(i, column)){
                return false;
            }

            addMoveIfValid(state, pawn, i, column, pawnMoves);
        }

        // Going downward
        for (int i = row + 1; i < state.getBoard().length; i++) {
            if (isObstacle(state, i, column) || isCamp(i, column)){
                return false;
            }

            addMoveIfValid(state, pawn, i, column, pawnMoves);
        }

        // Going to the left
        for (int j = column - 1; j >= 0; j--) {
            if (isObstacle(state, row, j) || isCamp(row, j)) return false;
            
            addMoveIfValid(state, pawn, row, j, pawnMoves);
        }

        // Going to the right
        for (int j = column + 1; j < state.getBoard().length; j++) {
            if (isObstacle(state, row, j) || isCamp(row, j)) return false;


            addMoveIfValid(state, pawn, row, j, pawnMoves);
        }
        
        // per ogni azione che il pedone può compiere, verifico che ci sia quella nella cella vuota (per completare la cattura a diamante)
        for(Action a: pawnMoves){
            if (a.getColumnTo()==pcol && a.getRowFrom()==prow) return true;
        }
        return false;
    }



    // check if the pawn is moving on an occupied cell
    private static boolean isObstacle(State state, int row, int col) {
        Pawn p = state.getPawn(row, col);
        // Considera ostacoli: pedine e caselle proibite
        return !p.equalsPawn(State.Pawn.EMPTY.toString());
    }

    // check if a white pawn is moving on a camp or castle
    private static boolean isCamp(int row, int col) {
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

}
