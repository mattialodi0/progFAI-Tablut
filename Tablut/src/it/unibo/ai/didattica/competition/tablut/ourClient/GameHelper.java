package it.unibo.ai.didattica.competition.tablut.ourClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class GameHelper {
    private static Turn playerColor;

    public GameHelper(Turn t) {
        playerColor = t;
    }

    public static List<Action> availableMoves(State state) {
        List<Action> moves = new ArrayList<Action>();
        List<int[]> pawns = populatePawnList(state);

        try {
            for (int[] p : pawns) {
                moves.addAll(getPawnMoves(state, p));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("A P: " + pawns.size());
        System.out.println("A M: " + moves.size());
        System.out.println( "move: " + moves.get(0).getTurn() + " " + moves.get(0).getFrom() + "->" + moves.get(0).getTo());

        return moves;
    }

    public static Boolean win(State state) {
        if (playerColor == Turn.WHITE)
            return state.getTurn() == Turn.WHITEWIN;
        else
            return state.getTurn() == Turn.BLACKWIN;
    }

    public static List<int[]> populatePawnList(State state) {
        List<int[]> pawns = new ArrayList<int[]>();

        int[] buf;
        for (int i = 0; i < state.getBoard().length; i++) {
            for (int j = 0; j < state.getBoard().length; j++) {
                if (playerColor == Turn.WHITE) {
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

    // Just the available empty, for the white don't show the black citadels.
    // Care not to return also the throne!
    public static List<int[]> populateEmptyList(State state) {
        List<int[]> empty = new ArrayList<int[]>();

        int[] buf;
        for (int i = 0; i < state.getBoard().length; i++) {
            for (int j = 0; j < state.getBoard().length; j++) {
                if (state.getPawn(i, j).equalsPawn(State.Pawn.EMPTY.toString())) {
                    buf = new int[2];
                    buf[0] = i;
                    buf[1] = j;
                    empty.add(buf);
                }
            }
        }

        return empty;
    }

    // check if the pawn is moving on an occupied cell
    public static boolean isObstacle(State state, int row, int col) {
        Pawn p = state.getPawn(row, col);
        // Considera ostacoli: pedine e caselle proibite
        return !p.equalsPawn(State.Pawn.EMPTY.toString());
    }

    // check if a white pawn is moving on a camp or castle
    public static boolean isCamp(Set<String> s, int row, int col) {
        return s.contains(row + "," + col);
    }

    // if the move is legit, then add the move in the list of moves
    public static void addMoveIfValid(State state, int[] pawn, int targetRow, int targetCol, List<Action> moves)
            throws IOException {
        String from = state.getBox(pawn[0], pawn[1]);
        String to = state.getBox(targetRow, targetCol);

        Action move = new Action(from, to, state.getTurn());

        moves.add(move);
    }

    public static List<Action> getPawnMoves(State state, int[] pawn) throws IOException {
        List<Action> pawnMoves = new ArrayList<Action>();
        int row = pawn[0];
        int column = pawn[1];

        // i need to check if that a white pawn does not move on a camp
        int[][] camps = {
                // camps on the top
                { 0, 4 },
                { 0, 5 },
                { 0, 6 },
                { 1, 5 },

                // camps on the bottom
                { state.getBoard()[0].length, 4 },
                { state.getBoard()[0].length, 5 },
                { state.getBoard()[0].length, 6 },
                { state.getBoard()[0].length - 1, 5 },

                // camps on the left
                { 4, 0 },
                { 5, 0 },
                { 6, 0 },
                { 5, 1 },

                // camps on the right
                { 5, state.getBoard()[0].length },
                { 6, state.getBoard()[0].length },
                { 5, state.getBoard()[0].length - 1 },
                { 4, state.getBoard()[0].length },

                // castle tail
                { 5, 5 },

        };

        // use hashmap to find faster if pawn wants to move on a camp o castle
        Set<String> campSet = new HashSet<String>();

        for (int[] camp : camps) {
            campSet.add(camp[0] + "," + camp[1]);
        }

        // for each move i can go either up, or down, or left, or right
        // i first fix the column, and move up and down, then i fix the row
        // and go left and right.
        // i check every time if i find an obstacle.

        // Going upward
        for (int i = row - 1; i >= 0; i--) {
            if (isObstacle(state, i, column))
                break;
            if (state.getPawn(i, column).equalsPawn(State.Pawn.WHITE.toString())) {
                if (isCamp(campSet, i, column)) {
                    break;
                }
            }
            addMoveIfValid(state, pawn, i, column, pawnMoves);
        }

        // Going downward
        for (int i = row + 1; i < state.getBoard().length; i++) {
            if (isObstacle(state, i, column))
                break;
            if (state.getPawn(i, column).equalsPawn(State.Pawn.WHITE.toString())) {
                if (isCamp(campSet, i, column)) {
                    break;
                }
            }

            addMoveIfValid(state, pawn, i, column, pawnMoves);
        }

        // Going to the left
        for (int j = column - 1; j >= 0; j--) {
            if (isObstacle(state, row, j))
                break;
            if (state.getPawn(row, j).equalsPawn(State.Pawn.WHITE.toString())) {
                if (isCamp(campSet, j, column)) {
                    break;
                }
            }
            addMoveIfValid(state, pawn, row, j, pawnMoves);
        }

        // Going to the right
        for (int j = column + 1; j < state.getBoard().length; j++) {
            if (isObstacle(state, row, j))
                break;
            if (state.getPawn(row, j).equalsPawn(State.Pawn.WHITE.toString())) {
                if (isCamp(campSet, j, column)) {
                    break;
                }
            }
            addMoveIfValid(state, pawn, row, j, pawnMoves);
        }
        return pawnMoves;
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
}
