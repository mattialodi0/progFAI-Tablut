package it.unibo.ai.didattica.competition.tablut.ourClient.ML;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.simulator.TablutGame;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;


/*
 * We assume in the dataset there are no final state
 */
public class TablutStateSimulator {
    int MATCHES = 100;
    int MAX_TURNS = 500;

    public static void main(String[] args) {

        List<String> dataset = new ArrayList<>();
        dataset.add("OOOOBOOOOOOOOBOOOOOOOOWOOOOOOOBWOOOBBBOBKBOBBBOOOWOOWBOOOWWOOOOOOOOBOOOOOOOBBBOOO");
        List<State> states = stringsToStates(dataset);

        TablutStateSimulator sim = new TablutStateSimulator();
        for (State s : states) {
            sim.run(s);

            break; // To remove
        }
    }

    public void run(State state) {
        State saved_state = state.clone();
        state.setTurn(Turn.WHITE);
        int whiteWins = 0;
        int blackWins = 0;
        int draws = 0;
        int errors = 0;
        
        int i;
        for (i = 0; i < MATCHES; i++) {
            Turn res = null;
            try {
                res = playRandGame(state.clone());
            } catch (Exception e) {
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

        System.out.println("Simulation results for " + MATCHES + " games from state \n" + state);
        System.out.println("White wins - " + whiteWins);
        System.out.println("Black wins - " + blackWins);
        System.out.println("Draws - " + draws);
        System.out.println("Erorrs - " + errors);

        state = saved_state.clone();
        state.setTurn(Turn.BLACK);
        whiteWins = 0;
        blackWins = 0;
        draws = 0;
        errors = 0;
        for (i = 0; i < MATCHES; i++) {
            Turn res = null;
            try {
                res = playRandGame(state.clone());
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

        System.out.println("Simulation results for " + MATCHES + " games from state \n" + state);
        System.out.println("White wins - " + whiteWins);
        System.out.println("Black wins - " + blackWins);
        System.out.println("Draws - " + draws);
        System.out.println("Erorrs - " + errors);
    }

    private Turn playRandGame(State state) {
        Action move;
        int turns = 0;

        // game loop
        while (true) {
            if (turns > this.MAX_TURNS) {
                return Turn.DRAW; // draw if exceed MAX_TURNS
            }

            if (TablutGame.isGameover(state))
                break;

            // white move
            move = randMove(state.clone());

            if (TablutGame.checkMove(state, move)) {
                TablutGame.makeMove(state, move);
            }

            if (TablutGame.isGameover(state))
                break;

            // black move
            move = randMove(state.clone());

            if (TablutGame.checkMove(state, move)) {
                TablutGame.makeMove(state, move);
            }

            turns++;
        }

        return state.getTurn();
    }

    private static List<State> stringsToStates(List<String> dataset) {
        List<State> states = new ArrayList<>();

        State tmp_state = new StateTablut();
        Pawn[][] tmp_board = new Pawn[9][9];

        for (String s : dataset) {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    tmp_board[i][j] = Pawn.fromString(Character.toString(s.charAt(i*9 + j)));
                }
            }
            tmp_state.setBoard(tmp_board);
            states.add(tmp_state.clone());
        }

        return states;
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
        } else
            return null;
    }

}
