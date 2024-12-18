package it.unibo.ai.didattica.competition.tablut.ourClient.ourGame;

import java.io.IOException;
import java.net.UnknownHostException;
import it.unibo.ai.didattica.competition.tablut.client.TablutClient;
import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.ourClient.interfaces.TreeSearch;
import it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches.MultiThreadMinMaxLauncher;
import it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches.IterativeDeepening;
import it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches.MinMax;


public class TablutOurClient extends TablutClient {

	private int game;
	private TreeSearch searchStrategy;
	private static int max_time = 60;

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
		String name = "TablutConqueror";
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
		max_time = timeout;

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

		searchStrategy = new IterativeDeepening(max_time); 

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

			if (this.getPlayer().equals(Turn.WHITE)) {
				if (state.getTurn().equals(StateTablut.Turn.WHITE)) {
					Action best_move = searchStrategy.searchTree(state);

					System.out.println("Mossa scelta: " + best_move.toString());
					try {
						this.write(best_move);
					} catch (ClassNotFoundException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (state.getTurn().equals(StateTablut.Turn.BLACK)) {
					System.out.println("Waiting for your opponent move... ");
				}
				// ho vinto
				else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
					System.out.println("YOU WIN!");
					System.exit(0);
				}
				// ho perso
				else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
					System.out.println("YOU LOSE!");
					System.exit(0);
				}
				// pareggio
				else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
					System.out.println("DRAW!");
					System.exit(0);
				}
			} 
			else if ((this.getPlayer().equals(Turn.BLACK)
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
			else if (state.getTurn().equals(StateTablut.Turn.WHITE)) {
				System.out.println("Waiting for your opponent move... ");
			} else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
				System.out.println("YOU LOSE!");
				System.exit(0);
			} else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
				System.out.println("YOU WIN!");
				System.exit(0);
			} else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
				System.out.println("DRAW!");
				System.exit(0);
			}
		}
	}
}
