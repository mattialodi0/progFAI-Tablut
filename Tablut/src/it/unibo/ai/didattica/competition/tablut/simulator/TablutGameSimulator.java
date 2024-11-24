package it.unibo.ai.didattica.competition.tablut.simulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.didattica.competition.tablut.ourClient.GameHelper;
import it.unibo.ai.didattica.competition.tablut.ourClient.LookupTable;
import it.unibo.ai.didattica.competition.tablut.ourClient.interfaces.TreeSearch;
import it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches.IterativeDeepening;
import it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches.MinMax;
import it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches.MultiThreadMinMaxLauncher;
import it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches.SRTS;
import it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches.SemiRandom;
import it.unibo.ai.didattica.competition.tablut.ourClient.evaluations.Evaluations;

// TODO: draw check and timeout
public class TablutGameSimulator {

	private int MAX_TURNS = 1000;
	private int MATCHES = 1;
	int time = 60;
	private double time_tot = 0;

	Timer timer = new Timer(time);

	public static void main(String[] args) {
		TablutGameSimulator sim = new TablutGameSimulator();
		sim.run();
	}

	public void run() {
		int game_reps = MATCHES;
		int whiteWins = 0;
		int blackWins = 0;
		int draws = 0;
		int errors = 0;

		System.out.println("Starting simulation (MTMM vs MM)");

		for (int i = 0; i < game_reps; i++) {
			Turn res = null;
			try {
				res = runGame();
			} catch (Exception e) {
				System.out.print(res);
				e.printStackTrace();
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
		// System.out.println(" ");
		// System.out.println("Max eval: " + MMTS.maxEval);
		// System.out.println("Min eval: " + MMTS.minEval);
		// System.out.println("Avg lookup hit: " + ((MinMaxTreeSearch.avgs)/(MinMaxTreeSearch.avgs_num)) +"%");
		// System.out.println("Avg time per move: " + (time_tot/MATCHES) +"s");
	}

	private Turn runGame() throws TimeoutException {
		State state;
		Action move;
		int turns = 0;
		// int moveCache = -1;
		// int repeated = 0;

		// state & game setup
		state = new StateTablut();
		state.setTurn(State.Turn.WHITE);

		// game loop
		while (true) {
			if (turns > this.MAX_TURNS) {
				return Turn.DRAW;	// draw if exceed MAX_TURNS
			}

			// white move
			timer.start();
			move = whiteMove(state.clone());
			if (timer.timeOutOccurred()) {
				throw new TimeoutException("The move took too long and exceeded the allowed time limit.");
			}
			time_tot += timer.getTimer();
			// System.out.println("Time white: "+timer.getTimer());


			if (TablutGame.checkMove(state, move)) {
				TablutGame.makeMove(state, move);
			}

			if (TablutGame.isGameover(state))
				break;

			// black move
			timer.start();
			move = blackMove(state.clone());
			if (timer.timeOutOccurred()) {
				throw new TimeoutException("The move took too long and exceeded the allowed time limit.");
			}
			// System.out.println("Time black: "+timer.getTimer());

			if (TablutGame.checkMove(state, move)) {
				TablutGame.makeMove(state, move);
			}

			if (TablutGame.isGameover(state))
				break;

			// System.out.println(state.toString());
			turns++;
		}

		// System.out.println("Endgame state \n" + state.toString());
		// System.out.println("Turns: " + turns);

		return state.getTurn();
	}

	private Action whiteMove(State state) {
		//TreeSearch searchStrategy = new MultiThreadMinMaxLauncher(3); 
		//TreeSearch searchStrategy = new MinMax(4);
		//return searchStrategy.searchTree(state);
		return randMove(state);
	}
	
	private Action blackMove(State state) {
		TreeSearch searchStrategy = new MultiThreadMinMaxLauncher(3);
		return searchStrategy.searchTree(state);
		//return randMove(state);
	}

	private Action randMove(State state) {
        List<Action> available_actions = GameHelper.availableMoves(state);
        if (available_actions.size() == 0)
            return null;

        Random rand = new Random();
        return available_actions.get(rand.nextInt(available_actions.size()));
    }

	private static class Timer {
		private long duration;
		private long startTime;

		public Timer(int maxSeconds) {
			this.duration = (long) (1000 * maxSeconds);
		}

		public void start() {
			this.startTime = System.currentTimeMillis();
		}

		public double getTimer() {
			return (double) (System.currentTimeMillis() - this.startTime) / 1000;
		}

		public boolean timeOutOccurred() {
			boolean overTime = System.currentTimeMillis() > this.startTime + this.duration;
			return overTime;
		}
	}
}
