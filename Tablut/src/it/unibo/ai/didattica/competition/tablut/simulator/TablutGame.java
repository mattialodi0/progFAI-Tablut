package it.unibo.ai.didattica.competition.tablut.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class TablutGame {
    private static List<String> citadels;
    private static int movesWithutCapturing = 0;

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

    /* Plays a move and update a state */   // TODO draw check
    public static void makeMove(State state, Action move) {
        // move pawn
        state = movePawn(state, move);

        // check for capures and endgames
        if (state.getTurn().equalsTurn("W")) {
			state = checkCaptureBlack(state, move);
		} else if (state.getTurn().equalsTurn("B")) {
			state = checkCaptureWhite(state, move);
		}

		// if something has been captured, clear cache for draws
		// if (movesWithutCapturing == 0) {
		// 	drawConditions.clear();
		// }
        //
		// controllo pareggio
		// int trovati = 0;
		// for (State s : drawConditions) {
		// 	if (s.equals(state)) {
		// 		trovati++;
		// 		if (trovati > repeated_moves_allowed) {
		// 			state.setTurn(State.Turn.DRAW);
		// 			break;
		// 		}
		// 	} else {
		// 		// DEBUG: //
		// 		// System.out.println("DIVERSI:");
		// 		// System.out.println("STATO VECCHIO:\t" + s.toLinearString());
		// 		// System.out.println("STATO NUOVO:\t" +
		// 		// state.toLinearString());
		// 	}
		// }
		// if (cache_size >= 0 && drawConditions.size() > cache_size) {
		// 	drawConditions.remove(0);
		// }
		// drawConditions.add(state.clone());

        //System.out.println("Stato:\n" + state.toString());
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


    private static State movePawn(State state, Action a) {
		State.Pawn pawn = state.getPawn(a.getRowFrom(), a.getColumnFrom());
		State.Pawn[][] newBoard = state.getBoard();

        // libero il trono o una casella qualunque
		if (a.getColumnFrom() == 4 && a.getRowFrom() == 4) {
			newBoard[a.getRowFrom()][a.getColumnFrom()] = State.Pawn.THRONE;
		} else {
			newBoard[a.getRowFrom()][a.getColumnFrom()] = State.Pawn.EMPTY;
		}

		// metto nel nuovo tabellone la pedina mossa
		newBoard[a.getRowTo()][a.getColumnTo()] = pawn;
		// aggiorno il tabellone
		state.setBoard(newBoard);
		// cambio il turno
		if (state.getTurn().equalsTurn(State.Turn.WHITE.toString())) {
			state.setTurn(State.Turn.BLACK);
		} else {
			state.setTurn(State.Turn.WHITE);
		}

		return state;
	}

    private static State checkCaptureWhite(State state, Action a) {
		// controllo se mangio a destra
		if (a.getColumnTo() < state.getBoard().length - 2
				&& state.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("B")
				&& (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("W")
						|| state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("T")
						|| state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("K")
						|| (citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() + 2))
								&& !(a.getColumnTo() + 2 == 8 && a.getRowTo() == 4)
								&& !(a.getColumnTo() + 2 == 4 && a.getRowTo() == 0)
								&& !(a.getColumnTo() + 2 == 4 && a.getRowTo() == 8)
								&& !(a.getColumnTo() + 2 == 0 && a.getRowTo() == 4)))) {
			state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
			movesWithutCapturing = -1;
		}
		// controllo se mangio a sinistra
		if (a.getColumnTo() > 1 && state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("B")
				&& (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("W")
						|| state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("T")
						|| state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("K")
						|| (citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() - 2))
								&& !(a.getColumnTo() - 2 == 8 && a.getRowTo() == 4)
								&& !(a.getColumnTo() - 2 == 4 && a.getRowTo() == 0)
								&& !(a.getColumnTo() - 2 == 4 && a.getRowTo() == 8)
								&& !(a.getColumnTo() - 2 == 0 && a.getRowTo() == 4)))) {
			state.removePawn(a.getRowTo(), a.getColumnTo() - 1);
			movesWithutCapturing = -1;
		}
		// controllo se mangio sopra
		if (a.getRowTo() > 1 && state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("B")
				&& (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("W")
						|| state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("T")
						|| state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("K")
						|| (citadels.contains(state.getBox(a.getRowTo() - 2, a.getColumnTo()))
								&& !(a.getColumnTo() == 8 && a.getRowTo() - 2 == 4)
								&& !(a.getColumnTo() == 4 && a.getRowTo() - 2 == 0)
								&& !(a.getColumnTo() == 4 && a.getRowTo() - 2 == 8)
								&& !(a.getColumnTo() == 0 && a.getRowTo() - 2 == 4)))) {
			state.removePawn(a.getRowTo() - 1, a.getColumnTo());
			movesWithutCapturing = -1;
		}
		// controllo se mangio sotto
		if (a.getRowTo() < state.getBoard().length - 2
				&& state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("B")
				&& (state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("W")
						|| state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("T")
						|| state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("K")
						|| (citadels.contains(state.getBox(a.getRowTo() + 2, a.getColumnTo()))
								&& !(a.getColumnTo() == 8 && a.getRowTo() + 2 == 4)
								&& !(a.getColumnTo() == 4 && a.getRowTo() + 2 == 0)
								&& !(a.getColumnTo() == 4 && a.getRowTo() + 2 == 8)
								&& !(a.getColumnTo() == 0 && a.getRowTo() + 2 == 4)))) {
			state.removePawn(a.getRowTo() + 1, a.getColumnTo());
			movesWithutCapturing = -1;
		}
		// controllo se ho vinto
		if (a.getRowTo() == 0 || a.getRowTo() == state.getBoard().length - 1 || a.getColumnTo() == 0
				|| a.getColumnTo() == state.getBoard().length - 1) {
			if (state.getPawn(a.getRowTo(), a.getColumnTo()).equalsPawn("K")) {
				state.setTurn(State.Turn.WHITEWIN);
			}
		}
		// TODO: implement the winning condition of the capture of the last
		// black checker

		movesWithutCapturing++;
		return state;
	}

	private static State checkCaptureBlack(State state, Action a) {

		checkCaptureBlackPawnRight(state, a);
		checkCaptureBlackPawnLeft(state, a);
		checkCaptureBlackPawnUp(state, a);
		checkCaptureBlackPawnDown(state, a);
		checkCaptureBlackKingRight(state, a);
		checkCaptureBlackKingLeft(state, a);
		checkCaptureBlackKingDown(state, a);
		checkCaptureBlackKingUp(state, a);

		movesWithutCapturing++;
		return state;
	}

	private static State checkCaptureBlackKingLeft(State state, Action a) {
		// ho il re sulla sinistra
		if (a.getColumnTo() > 1 && state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("K")) {
			// re sul trono
			if (state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e5")) {
				if (state.getPawn(3, 4).equalsPawn("B") && state.getPawn(4, 3).equalsPawn("B")
						&& state.getPawn(5, 4).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			// re adiacente al trono
			if (state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e4")) {
				if (state.getPawn(2, 4).equalsPawn("B") && state.getPawn(3, 3).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			if (state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("f5")) {
				if (state.getPawn(5, 5).equalsPawn("B") && state.getPawn(3, 5).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			if (state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e6")) {
				if (state.getPawn(6, 4).equalsPawn("B") && state.getPawn(5, 3).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			// sono fuori dalle zone del trono
			if (!state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e5")
					&& !state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e6")
					&& !state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e4")
					&& !state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("f5")) {
				if (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("B")
						|| citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() - 2))) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
		}
		return state;
	}

	private static State checkCaptureBlackKingRight(State state, Action a) {
		// ho il re sulla destra
		if (a.getColumnTo() < state.getBoard().length - 2
				&& (state.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("K"))) {
			// re sul trono
			if (state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e5")) {
				if (state.getPawn(3, 4).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B")
						&& state.getPawn(5, 4).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			// re adiacente al trono
			if (state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e4")) {
				if (state.getPawn(2, 4).equalsPawn("B") && state.getPawn(3, 5).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			if (state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e6")) {
				if (state.getPawn(5, 5).equalsPawn("B") && state.getPawn(6, 4).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			if (state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("d5")) {
				if (state.getPawn(3, 3).equalsPawn("B") && state.getPawn(5, 3).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			// sono fuori dalle zone del trono
			if (!state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("d5")
					&& !state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e6")
					&& !state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e4")
					&& !state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e5")) {
				if (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("B")
						|| citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() + 2))) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
		}
		return state;
	}

	private static State checkCaptureBlackKingDown(State state, Action a) {
		// ho il re sotto
		if (a.getRowTo() < state.getBoard().length - 2
				&& state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("K")) {
			//System.out.println("Ho il re sotto");
			// re sul trono
			if (state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("e5")) {
				if (state.getPawn(5, 4).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B")
						&& state.getPawn(4, 3).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			// re adiacente al trono
			if (state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("e4")) {
				if (state.getPawn(3, 3).equalsPawn("B") && state.getPawn(3, 5).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			if (state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("d5")) {
				if (state.getPawn(4, 2).equalsPawn("B") && state.getPawn(5, 3).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			if (state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("f5")) {
				if (state.getPawn(4, 6).equalsPawn("B") && state.getPawn(5, 5).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			// sono fuori dalle zone del trono
			if (!state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("d5")
					&& !state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("e4")
					&& !state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("f5")
					&& !state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("e5")) {
				if (state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("B")
						|| citadels.contains(state.getBox(a.getRowTo() + 2, a.getColumnTo()))) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
		}
		return state;
	}

	private static State checkCaptureBlackKingUp(State state, Action a) {
		// ho il re sopra
		if (a.getRowTo() > 1 && state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("K")) {
			// re sul trono
			if (state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("e5")) {
				if (state.getPawn(3, 4).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B")
						&& state.getPawn(4, 3).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			// re adiacente al trono
			if (state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("e6")) {
				if (state.getPawn(5, 3).equalsPawn("B") && state.getPawn(5, 5).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			if (state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("d5")) {
				if (state.getPawn(4, 2).equalsPawn("B") && state.getPawn(3, 3).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			if (state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("f5")) {
				if (state.getPawn(4, 6).equalsPawn("B") && state.getPawn(3, 5).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			// sono fuori dalle zone del trono
			if (!state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("d5")
					&& !state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("e6")
					&& !state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("f5")
					&& !state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("e5")) {
				if (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("B")
						|| citadels.contains(state.getBox(a.getRowTo() - 2, a.getColumnTo()))) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
		}
		return state;
	}

	private static State checkCaptureBlackPawnRight(State state, Action a) {
		// mangio a destra
		if (a.getColumnTo() < state.getBoard().length - 2
				&& state.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("W")) {
			if (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("B")) {
				state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
				movesWithutCapturing = -1;
			}
			if (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("T")) {
				state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
				movesWithutCapturing = -1;
			}
			if (citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() + 2))) {
				state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
				movesWithutCapturing = -1;
            }
			if (state.getBox(a.getRowTo(), a.getColumnTo() + 2).equals("e5")) {
				state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
				movesWithutCapturing = -1;
			}
		}
		return state;
	}

	private static State checkCaptureBlackPawnLeft(State state, Action a) {
		// mangio a sinistra
		if (a.getColumnTo() > 1 && state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("W")
				&& (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("B")
						|| state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("T")
						|| citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() - 2))
						|| (state.getBox(a.getRowTo(), a.getColumnTo() - 2).equals("e5")))) {
			state.removePawn(a.getRowTo(), a.getColumnTo() - 1);
			movesWithutCapturing = -1;
		}
		return state;
	}

	private static State checkCaptureBlackPawnUp(State state, Action a) {
		// controllo se mangio sopra
		if (a.getRowTo() > 1 && state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("W")
				&& (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("B")
						|| state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("T")
						|| citadels.contains(state.getBox(a.getRowTo() - 2, a.getColumnTo()))
						|| (state.getBox(a.getRowTo() - 2, a.getColumnTo()).equals("e5")))) {
			state.removePawn(a.getRowTo() - 1, a.getColumnTo());
			movesWithutCapturing = -1;
		}
		return state;
	}

	private static State checkCaptureBlackPawnDown(State state, Action a) {
		// controllo se mangio sotto
		if (a.getRowTo() < state.getBoard().length - 2
				&& state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("W")
				&& (state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("B")
						|| state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("T")
						|| this.citadels.contains(state.getBox(a.getRowTo() + 2, a.getColumnTo()))
						|| (state.getBox(a.getRowTo() + 2, a.getColumnTo()).equals("e5")))) {
			state.removePawn(a.getRowTo() + 1, a.getColumnTo());
			movesWithutCapturing = -1;
		}
		return state;
	}

}
