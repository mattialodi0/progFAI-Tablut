package it.unibo.ai.didattica.competition.tablut.ourClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches.MinMax;
import it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches.MinMaxLimited;
import it.unibo.ai.didattica.competition.tablut.simulator.TablutGame;
import it.unibo.ai.didattica.competition.tablut.simulator.TablutGameSimulator;

public class Testing {

    private int differenceCounter;
    private int actionsOfGame;

    public static void main(String[] args) {
        Testing test = new Testing();
        test.run();
    }

    public void run() {
        int i = 0;
        List<Float> results = new ArrayList<>();
        while( i < 1){
            Turn res = runGame();
            results.add((float) differenceCounter / actionsOfGame);
            this.differenceCounter = 0;
            this.actionsOfGame = 0;
            i += 1;
        }
        System.out.println("Ratio of equal chosen actions: " + (results.stream().mapToDouble(Float::floatValue).sum()) / results.size());
    }

    private Turn runGame() {
        State state;
        Action move;
        int turns = 0;

        // state & game setup
        state = new StateTablut();
        state.setTurn(State.Turn.WHITE);
        int j = 0;
        // game loop
        while (true) {
            j ++;
            if (turns > 1000) {
                return Turn.DRAW; // draw if exceed MAX_TURNS
            }

            // white move
            move = whiteMove(state.clone());

            if (TablutGame.checkMove(state, move)) {
                TablutGame.makeMove(state, move);
            }

            if (TablutGame.isGameover(state))
                break;

            // black move
            move = blackMove(state.clone());

            if (TablutGame.checkMove(state, move)) {
                TablutGame.makeMove(state, move);
            }

            if (TablutGame.isGameover(state))
                break;

            turns++;
        }
        this.actionsOfGame = j;
        return state.getTurn();
    }

    private Action whiteMove(State state) {
        MinMax minMax = new MinMax(3);
        MinMaxLimited minMaxNoLimit = new MinMaxLimited(3);

        Action minMaxChoses = minMax.searchTree(state.clone());
        Action minMaxNoLimitChoses = minMaxNoLimit.searchTree(state.clone());
        System.out.println(minMaxChoses + "    " + minMaxNoLimitChoses);
        if (minMaxChoses.toString().equals(minMaxNoLimitChoses.toString())) {
            this.differenceCounter  += 1;
        }
        return minMaxChoses;
    }

    private Action blackMove(State state) {
        // MinMax minMax = new MinMax(3);
        // MinMaxNoLimit minMaxNoLimit = new MinMaxNoLimit(3);

        // Action minMaxChoses = minMax.searchTree(state.clone());
        // Action minMaxNoLimitChoses = minMaxNoLimit.searchTree(state.clone());
        // if (minMaxChoses.toString().equals(minMaxNoLimitChoses.toString())) {
        //     this.differenceCounter  += 1;
        // }
        // return minMaxChoses;
        Action action = randMove(state.clone());
        System.out.println("Black move: " + action);
        return action;

    }

    private Action randMove(State state) {
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

				if (TablutGame.checkMove(state, a))
					found = true;
			}

			return a;
		} else if (state.getTurn().equals(StateTablut.Turn.BLACK)) {
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
					a = new Action(from, to, State.Turn.BLACK);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				if (TablutGame.checkMove(state, a))
					found = true;

			}
			return a;
		}else
			return null;
	}
}
