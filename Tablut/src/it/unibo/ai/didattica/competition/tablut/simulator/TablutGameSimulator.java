package it.unibo.ai.didattica.competition.tablut.simulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.ourClient.GameHelper;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

public class TablutGameSimulator {

	TablutGameSimulator() {
		int game_reps = 100;
		int whiteWins = 0;
		int blackWins = 0;
		int draws = 0;
		int errors = 0;

		for (int i = 0; i < game_reps; i++) {
			Turn res = null;
			try {
				res = runGame();
			} catch (Exception e) {
			}

			switch (res) {
				case WHITEWIN:
					whiteWins++;
					break;
				case DRAW:
					draws++;
					break;
				case BLACKWIN:
					blackWins++;
					break;
				default:
					errors++;
					break;
			}
		}

		System.out.println("Simulation results for " + game_reps + " games:");
		System.out.println("White wins - " + whiteWins);
		System.out.println("Black wins - " + blackWins);
		System.out.println("Draws - " + draws);
		System.out.println("Erorrs - " + errors);
	}

	private Turn runGame()  {
		State state;
		int time = 60;
		int moveCache = -1;
		Action move;
		int repeated = 0;
		int turns = 0;

		// state & game setup
		state = new StateTablut();
		state.setTurn(State.Turn.WHITE);

		// game loop
		while (turns < 1000) {
			// black move
			move = blackMove(state);
			if(TablutGame.checkMove(state, move))
				TablutGame.makeMove(state, move);

			if(TablutGame.isGameover(state))
				break;

			// white move
			move = whiteMove(state);
			if(TablutGame.checkMove(state, move))
				TablutGame.makeMove(state, move);


			if(TablutGame.isGameover(state))
				break;

			// t = new Thread();
			// t.start();

			// // timer for the move
			// int counter = 0;
			// while (counter < time && t.isAlive()) {
			// 	Thread.sleep(1000);
			// 	counter++;
			// }

			turns++;
		}

		return state.getTurn();
	}

	private Action whiteMove(State state) {
		List<int[]> pawns = new ArrayList<int[]>();
		List<int[]> empty = new ArrayList<int[]>();

		if (state.getTurn().equals(StateTablut.Turn.WHITE)) {
			int[] buf;
			for (int i = 0; i < state.getBoard().length; i++) {
				for (int j = 0; j < state.getBoard().length; j++) {
					if (state.getPawn(i, j).equalsPawn(State.Pawn.WHITE.toString())
							|| state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())) {
						buf = new int[2];
						buf[0] = i;
						buf[1] = j;
						pawns.add(buf);
					} else if (state.getPawn(i, j).equalsPawn(State.Pawn.EMPTY.toString())) {
						buf = new int[2];
						buf[0] = i;
						buf[1] = j;
						empty.add(buf);
					}
				}
			}

			int[] selected = null;

			boolean found = false;
			Action a = null;
			try {
				a = new Action("z0", "z0", State.Turn.WHITE);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			while (!found) {
				if (pawns.size() > 1) {
					selected = pawns.get(new Random().nextInt(pawns.size() - 1));
				} else {
					selected = pawns.get(0);
				}

				String from = state.getBox(selected[0], selected[1]);

				selected = empty.get(new Random().nextInt(empty.size() - 1));
				String to = state.getBox(selected[0], selected[1]);

				try {
					a = new Action(from, to, State.Turn.WHITE);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try {
					checkMove(state, a);
					found = true;
				} catch (Exception e) {

				}
			}

			return a;
		}

		return null;
	}

	private Action blackMove(State state) {
		List<int[]> pawns = new ArrayList<int[]>();
		List<int[]> empty = new ArrayList<int[]>();

		if (state.getTurn().equals(StateTablut.Turn.BLACK)) {
			int[] buf;
			for (int i = 0; i < state.getBoard().length; i++) {
				for (int j = 0; j < state.getBoard().length; j++) {
					if (state.getPawn(i, j).equalsPawn(State.Pawn.BLACK.toString())) {
						buf = new int[2];
						buf[0] = i;
						buf[1] = j;
						pawns.add(buf);
					} else if (state.getPawn(i, j).equalsPawn(State.Pawn.EMPTY.toString())) {
						buf = new int[2];
						buf[0] = i;
						buf[1] = j;
						empty.add(buf);
					}
				}
			}

			int[] selected = null;

			boolean found = false;
			Action a = null;
			try {
				a = new Action("z0", "z0", State.Turn.BLACK);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			;
			while (!found) {
				selected = pawns.get(new Random().nextInt(pawns.size() - 1));
				String from = state.getBox(selected[0], selected[1]);

				selected = empty.get(new Random().nextInt(empty.size() - 1));
				String to = state.getBox(selected[0], selected[1]);

				try {
					a = new Action(from, to, State.Turn.BLACK);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				System.out.println("try: " + a.toString());
				try {
					checkMove(state, a);
					found = true;
				} catch (Exception e) {

				}

			}

			return a;
		}

		return null;
	}
}
