package it.unibo.ai.didattica.competition.tablut.ourClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;

import it.unibo.ai.didattica.competition.tablut.client.TablutClient;
import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.ourClient.interfaces.TreeSearch;

/**
 * 
 * @author A. Piretti, Andrea Galassi
 *
 */
public class TablutOurClient extends TablutClient {

	private int game;
	private TreeSearch searchStrategy;
	private GameHelper gameHelper;

	public TablutOurClient(String player, String name, int gameChosen, int timeout, String ipAddress)
			throws UnknownHostException, IOException {
		super(player, name, timeout, ipAddress);
		game = gameChosen;
	}

	public TablutOurClient(String player, String name, int timeout, String ipAddress)
			throws UnknownHostException, IOException {
		this(player, name, 4, timeout, ipAddress);
	}

	public TablutOurClient(String player, int timeout, String ipAddress) throws UnknownHostException, IOException {
		this(player, "random", 4, timeout, ipAddress);
	}

	public TablutOurClient(String player) throws UnknownHostException, IOException {
		this(player, "random", 4, 60, "localhost");
	}

	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		int gametype = 4;
		String role = "";
		String name = "random";
		String ipAddress = "localhost";
		int timeout = 60;
		// TODO: change the behavior?
		if (args.length < 1) {
			System.out.println("You must specify which player you are (WHITE or BLACK)");
			System.exit(-1);
		} else {
			System.out.println(args[0]);
			role = (args[0]);
		}
		if (args.length == 2) {
			System.out.println(args[1]);
			timeout = Integer.parseInt(args[1]);
		}
		if (args.length == 3) {
			ipAddress = args[2];
		}
		System.out.println("Selected client: " + args[0]);

		TablutOurClient client = new TablutOurClient(role, name, gametype, timeout, ipAddress);
		client.run();
	}

	@Override
	public void run() {

		try {
			this.declareName();
		} catch (Exception e) {
			e.printStackTrace();
		}

		State state;

		Game rules = null;
		switch (this.game) {
			case 1:
				state = new StateTablut();
				rules = new GameTablut();
				break;
			case 2:
				state = new StateTablut();
				rules = new GameModernTablut();
				break;
			case 3:
				state = new StateBrandub();
				rules = new GameTablut();
				break;
			case 4:
				state = new StateTablut();
				state.setTurn(State.Turn.WHITE);
				rules = new GameAshtonTablut(99, 0, "garbage", "fake", "fake");
				System.out.println("Ashton Tablut game");
				break;
			default:
				System.out.println("Error in game selection");
				System.exit(4);
		}

		searchStrategy = new BasicTreeSearch(rules);
		gameHelper = new GameHelper(this.getPlayer(), rules);

		List<int[]> pawns = new ArrayList<int[]>();
		List<int[]> empty = new ArrayList<int[]>();

		System.out.println("You are player " + this.getPlayer().toString() + "!");

		while (true) {
			try {
				this.read();
			} catch (ClassNotFoundException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(1);
			}
			System.out.println("Current state:");
			state = this.getCurrentState();
			System.out.println(state.toString());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}

			if ((this.getPlayer().equals(Turn.WHITE)
					&& this.getCurrentState().getTurn().equals(StateTablut.Turn.WHITE))) {
				Action best_move = searchStrategy.searchTree(state);

				try {
					rules.checkMove(state, best_move);
				} catch (Exception e) {
				}

				System.out.println("Mossa scelta: " + best_move.toString());
				try {
					this.write(best_move);
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if ((this.getPlayer().equals(Turn.BLACK)
					&& this.getCurrentState().getTurn().equals(StateTablut.Turn.BLACK))) {
				Action best_move = searchStrategy.searchTree(state);

				try {
					rules.checkMove(state, best_move);
				} catch (Exception e) {
				}

				System.out.println("Mossa scelta: " + best_move.toString());
				try {
					this.write(best_move);
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			/*
			 * if (this.getPlayer().equals(Turn.WHITE)) {
			 * // Mio turno
			 * if (this.getCurrentState().getTurn().equals(StateTablut.Turn.WHITE)) {
			 * int[] buf;
			 * 
			 * // Popola l'array dei pezzi e delle caselle vuote
			 * for (int i = 0; i < state.getBoard().length; i++) {
			 * for (int j = 0; j < state.getBoard().length; j++) {
			 * if (state.getPawn(i, j).equalsPawn(State.Pawn.WHITE.toString())
			 * || state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())) {
			 * buf = new int[2];
			 * buf[0] = i;
			 * buf[1] = j;
			 * pawns.add(buf);
			 * } else if (state.getPawn(i, j).equalsPawn(State.Pawn.EMPTY.toString())) {
			 * buf = new int[2];
			 * buf[0] = i;
			 * buf[1] = j;
			 * empty.add(buf);
			 * }
			 * }
			 * }
			 * 
			 * // System.out.println("AAAAAAAAAAA");
			 * // for (int i = 0; i < state.getBoard().length; i++) {
			 * // for (int j = 0; j < state.getBoard().length; j++) {
			 * // System.out.print(state.getPawn(i, j).toString());
			 * // }
			 * // }
			 * // System.out.print("\n\n");
			 * 
			 * int[] selected = null;
			 * 
			 * boolean found = false;
			 * Action a = null;
			 * try {
			 * a = new Action("z0", "z0", State.Turn.WHITE);
			 * } catch (IOException e1) {
			 * // TODO Auto-generated catch block
			 * e1.printStackTrace();
			 * }
			 * 
			 * // Trova una mossa a caso, prendendo un pezzo casuale e scegliendo una mossa
			 * casuale per quel pezzo
			 * while (!found) {
			 * if (pawns.size() > 1) {
			 * selected = pawns.get(new Random().nextInt(pawns.size() - 1));
			 * } else {
			 * selected = pawns.get(0);
			 * }
			 * 
			 * String from = this.getCurrentState().getBox(selected[0], selected[1]);
			 * 
			 * selected = empty.get(new Random().nextInt(empty.size() - 1));
			 * String to = this.getCurrentState().getBox(selected[0], selected[1]);
			 * 
			 * // System.out.print("Pieces["+pawns.size()+"]: ");
			 * // for(int m = 0; m < pawns.size(); m++) {
			 * // System.out.print(pawns.get(m)[0] +","+ pawns.get(m)[1] + " ");
			 * // }
			 * // System.out.print("\n Spaces["+empty.size()+"]: ");
			 * 
			 * // for(int m = 0; m < empty.size(); m++) {
			 * // System.out.print(empty.get(m)[0] +","+ empty.get(m)[1] + " ");
			 * // }
			 * // System.out.print("\n Trying piece: ");
			 * // System.out.print(selected[0] + "," + selected[1]);
			 * // System.out.println(String.format(" from: %s to: %s", from, to));
			 * 
			 * 
			 * try {
			 * a = new Action(from, to, State.Turn.WHITE);
			 * } catch (IOException e1) {
			 * // TODO Auto-generated catch block
			 * e1.printStackTrace();
			 * }
			 * 
			 * try {
			 * rules.checkMove(state, a);
			 * found = true;
			 * } catch (Exception e) {
			 * 
			 * }
			 * }
			 * 
			 * System.out.println("Mossa scelta: " + a.toString());
			 * try {
			 * this.write(a);
			 * } catch (ClassNotFoundException | IOException e) {
			 * // TODO Auto-generated catch block
			 * e.printStackTrace();
			 * }
			 * pawns.clear();
			 * empty.clear();
			 * 
			 * }
			 * // Turno dell'avversario
			 * else if (state.getTurn().equals(StateTablut.Turn.BLACK)) {
			 * System.out.println("Waiting for your opponent move... ");
			 * }
			 * // ho vinto
			 * else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
			 * System.out.println("YOU WIN!");
			 * System.exit(0);
			 * }
			 * // ho perso
			 * else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
			 * System.out.println("YOU LOSE!");
			 * System.exit(0);
			 * }
			 * // pareggio
			 * else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
			 * System.out.println("DRAW!");
			 * System.exit(0);
			 * }
			 * 
			 * } else {
			 * 
			 * // Mio turno
			 * if (this.getCurrentState().getTurn().equals(StateTablut.Turn.BLACK)) {
			 * int[] buf;
			 * for (int i = 0; i < state.getBoard().length; i++) {
			 * for (int j = 0; j < state.getBoard().length; j++) {
			 * if (state.getPawn(i, j).equalsPawn(State.Pawn.BLACK.toString())) {
			 * buf = new int[2];
			 * buf[0] = i;
			 * buf[1] = j;
			 * pawns.add(buf);
			 * } else if (state.getPawn(i, j).equalsPawn(State.Pawn.EMPTY.toString())) {
			 * buf = new int[2];
			 * buf[0] = i;
			 * buf[1] = j;
			 * empty.add(buf);
			 * }
			 * }
			 * }
			 * 
			 * int[] selected = null;
			 * 
			 * boolean found = false;
			 * Action a = null;
			 * try {
			 * a = new Action("z0", "z0", State.Turn.BLACK);
			 * } catch (IOException e1) {
			 * // TODO Auto-generated catch block
			 * e1.printStackTrace();
			 * }
			 * ;
			 * while (!found) {
			 * selected = pawns.get(new Random().nextInt(pawns.size() - 1));
			 * String from = this.getCurrentState().getBox(selected[0], selected[1]);
			 * 
			 * selected = empty.get(new Random().nextInt(empty.size() - 1));
			 * String to = this.getCurrentState().getBox(selected[0], selected[1]);
			 * 
			 * try {
			 * a = new Action(from, to, State.Turn.BLACK);
			 * } catch (IOException e1) {
			 * // TODO Auto-generated catch block
			 * e1.printStackTrace();
			 * }
			 * 
			 * System.out.println("try: " + a.toString());
			 * try {
			 * rules.checkMove(state, a);
			 * found = true;
			 * } catch (Exception e) {
			 * 
			 * }
			 * 
			 * }
			 * 
			 * System.out.println("Mossa scelta: " + a.toString());
			 * try {
			 * this.write(a);
			 * } catch (ClassNotFoundException | IOException e) {
			 * // TODO Auto-generated catch block
			 * e.printStackTrace();
			 * }
			 * pawns.clear();
			 * empty.clear();
			 * 
			 * }
			 * 
			 * else if (state.getTurn().equals(StateTablut.Turn.WHITE)) {
			 * System.out.println("Waiting for your opponent move... ");
			 * } else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
			 * System.out.println("YOU LOSE!");
			 * System.exit(0);
			 * } else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
			 * System.out.println("YOU WIN!");
			 * System.exit(0);
			 * } else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
			 * System.out.println("DRAW!");
			 * System.exit(0);
			 * }
			 * 
			 * }
			 */
		}
	}
}

/*
 * Client functioning:
 * 
 * 0. get the game state
 * 1. checks the state for win/lose/draw
 * 2. checks if can win in a move
 * 3. starts the search of the best possible move
 * - checks time
 * - evaluates a level of the tree
 * - choose a move
 * 
 * lookup tables to not re-evaluate the same position
 * heuristics to evaluate position -> DL/RL
 */