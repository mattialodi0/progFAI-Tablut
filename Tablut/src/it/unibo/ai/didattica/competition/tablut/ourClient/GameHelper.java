package it.unibo.ai.didattica.competition.tablut.ourClient;

import java.util.ArrayList;
import java.util.List;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class GameHelper {
    private static Turn playerColor;
    private static Game rules;

    public GameHelper(Turn t, Game r) {
        playerColor = t;
        rules = r;
    }

    public static List<Action> availableMoves(State state) {
        List<Action> moves = new ArrayList<Action>();
        List<int[]> pawns = populatePawnList(state);

        for (int[] p : pawns) {
            moves.addAll(getPawnMoves(state, p));
        }

        System.out.println("A P: " + pawns.size());
        System.out.println("A M: " + moves.size());

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

    // terrible implementation, but good enough for now
    private static List<Action> getPawnMoves(State state, int[] pawn) {
        List<Action> pawnMoves = new ArrayList<Action>();
        List<int[]> empty = populateEmptyList(state);

        for (int[] e : empty) {
            try {
                String from = state.getBox(pawn[0], pawn[1]);
                String to = state.getBox(e[0], e[1]);
                Action move = new Action(from, to, state.getTurn());

                rules.checkMove(state, move);
                pawnMoves.add(move);
            } catch (Exception ex) {
            }
        }

        return pawnMoves;
    }
}
