package it.unibo.ai.didattica.competition.tablut.simulator;

import java.util.ArrayList;
import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.exceptions.ActionException;
import it.unibo.ai.didattica.competition.tablut.exceptions.BoardException;
import it.unibo.ai.didattica.competition.tablut.exceptions.CitadelException;
import it.unibo.ai.didattica.competition.tablut.exceptions.ClimbingCitadelException;
import it.unibo.ai.didattica.competition.tablut.exceptions.ClimbingException;
import it.unibo.ai.didattica.competition.tablut.exceptions.DiagonalException;
import it.unibo.ai.didattica.competition.tablut.exceptions.OccupitedException;
import it.unibo.ai.didattica.competition.tablut.exceptions.PawnException;
import it.unibo.ai.didattica.competition.tablut.exceptions.StopException;
import it.unibo.ai.didattica.competition.tablut.exceptions.ThroneException;

public class TablutGame {
    private static List<String> citadels;

    static {
        citadels = new ArrayList<String>();
        citadels.add("a4");
        citadels.add("a5");
        citadels.add("a6");
        citadels.add("b5");
        citadels.add("d1");
        citadels.add("e1");
        citadels.add("f1");
        citadels.add("e2");
        citadels.add("i4");
        citadels.add("i5");
        citadels.add("i6");
        citadels.add("h5");
        citadels.add("d9");
        citadels.add("e9");
        citadels.add("f9");
        citadels.add("e8");
    }

    /* Plays a move and update a state */
    public static void makeMove(State state, Action move) {
        // move pawn

        // check for capures and endgames

        // update turn
    }

    /* Checks a move but does not update a state, returns true if no errors */
    public static Boolean checkMove(State state, Action move) {
        String error = "";

        // checks the move format
        if (move.getTo().length() != 2 || move.getFrom().length() != 2) {
            error = "ActionException: " + move.toString();
            return true;
        }
        int columnFrom = move.getColumnFrom();
        int columnTo = move.getColumnTo();
        int rowFrom = move.getRowFrom();
        int rowTo = move.getRowTo();

        // checks if move is outside the board
        if (columnFrom > state.getBoard().length - 1 || rowFrom > state.getBoard().length - 1
                || rowTo > state.getBoard().length - 1 || columnTo > state.getBoard().length - 1 || columnFrom < 0
                || rowFrom < 0 || rowTo < 0 || columnTo < 0) {
            error = "BoardException: " + move.toString();
            return true;
        }

        // checks for move on throne
        if (state.getPawn(rowTo, columnTo).equalsPawn(State.Pawn.THRONE.toString())) {
            error = "ThroneException: " + move.toString();
            return true;
        }

        // checks the destination square
        if (!state.getPawn(rowTo, columnTo).equalsPawn(State.Pawn.EMPTY.toString())) {
            error = "OccupitedException: " + move.toString();
            return true;
        }
        if (citadels.contains(state.getBox(rowTo, columnTo))
                && !citadels.contains(state.getBox(rowFrom, columnFrom))) {
            error = "CitadelException: " + move.toString();
            return true;
        }
        if (citadels.contains(state.getBox(rowTo, columnTo))
                && citadels.contains(state.getBox(rowFrom, columnFrom))) {
            if (rowFrom == rowTo) {
                if (columnFrom - columnTo > 5 || columnFrom - columnTo < -5) {
                    error = "CitadelException: " + move.toString();
                    return true;
                }
            } else {
                if (rowFrom - rowTo > 5 || rowFrom - rowTo < -5) {
                    error = "CitadelException: " + move.toString();
                    return true;
                }
            }

        }

        // checks for no movement
        if (rowFrom == rowTo && columnFrom == columnTo) {
            error = "StopException: " + move.toString();
            return true;
        }

        // checks for if pawn is own
        if (state.getTurn().equalsTurn(State.Turn.WHITE.toString())) {
            if (!state.getPawn(rowFrom, columnFrom).equalsPawn("W")
                    && !state.getPawn(rowFrom, columnFrom).equalsPawn("K")) {
                error = "PawnException (try to move enemy pawn): " + move.toString();
                return true;
            }
        }
        if (state.getTurn().equalsTurn(State.Turn.BLACK.toString())) {
            if (!state.getPawn(rowFrom, columnFrom).equalsPawn("B")) {
                error = "PawnException (try to move enemy pawn): " + move.toString();
                return true;
            }
        }

        // checks for no diagonal moves
        if (rowFrom != rowTo && columnFrom != columnTo) {
            error = "DiagonalException: " + move.toString();
            return true;
        }

        // checks for no crossing pawns
        if (rowFrom == rowTo) {
            if (columnFrom > columnTo) {
                for (int i = columnTo; i < columnFrom; i++) {
                    if (!state.getPawn(rowFrom, i).equalsPawn(State.Pawn.EMPTY.toString())) {
                        if (state.getPawn(rowFrom, i).equalsPawn(State.Pawn.THRONE.toString())) {
                            error = "ClimbingException (throne): " + move.toString();
                            return true;
                        } else {
                            error = "ClimbingException (pawn): " + move.toString();
                            return true;
                        }
                    }
                    if (citadels.contains(state.getBox(rowFrom, i))
                            && !citadels.contains(state.getBox(rowFrom, columnFrom))) {
                        error = "ClimbingException (citadel): " + move.toString();
                        return true;
                    }
                }
            } else {
                for (int i = columnFrom + 1; i <= columnTo; i++) {
                    if (!state.getPawn(rowFrom, i).equalsPawn(State.Pawn.EMPTY.toString())) {
                        if (state.getPawn(rowFrom, i).equalsPawn(State.Pawn.THRONE.toString())) {
                            error = "ClimbingException (throne): " + move.toString();
                            return true;
                        } else {
                            error = "ClimbingException (pawn): " + move.toString();
                            return true;
                        }
                    }
                    if (citadels.contains(state.getBox(rowFrom, i))
                            && !citadels.contains(state.getBox(rowFrom, columnFrom))) {
                        error = "ClimbingException (citadel): " + move.toString();
                        return true;
                    }
                }
            }
        } else {
            if (rowFrom > rowTo) {
                for (int i = rowTo; i < rowFrom; i++) {
                    if (!state.getPawn(i, columnFrom).equalsPawn(State.Pawn.EMPTY.toString())) {
                        if (state.getPawn(i, columnFrom).equalsPawn(State.Pawn.THRONE.toString())) {
                            error = "ClimbingException (throne): " + move.toString();
                            return true;
                        } else {
                            error = "ClimbingException (pawn): " + move.toString();
                            return true;
                        }
                    }
                    if (citadels.contains(state.getBox(i, columnFrom))
                            && !citadels.contains(state.getBox(rowFrom, columnFrom))) {
                                error = "ClimbingException (citadel): " + move.toString();
                                return true;
                    }
                }
            } else {
                for (int i = rowFrom + 1; i <= rowTo; i++) {
                    if (!state.getPawn(i, columnFrom).equalsPawn(State.Pawn.EMPTY.toString())) {
                        if (state.getPawn(i, columnFrom).equalsPawn(State.Pawn.THRONE.toString())) {
                            error = "ClimbingException (throne): " + move.toString();
                            return true;
                        } else {
                            error = "ClimbingException (pawn): " + move.toString();
                            return true;
                        }
                    }
                    if (citadels.contains(state.getBox(i, columnFrom))
                            && !citadels.contains(state.getBox(rowFrom, columnFrom))) {
                        error = "ClimbingException (citadel): " + move.toString();
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /* Checks a state for gameover */
    public static Boolean isGameover(State state) {
        if (state.getTurn() == Turn.WHITEWIN || state.getTurn() == Turn.BLACKWIN || state.getTurn() == Turn.DRAW)
            return true;
        else
            return false;
    }
}
