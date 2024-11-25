package it.unibo.ai.didattica.competition.tablut.ourClient.ML;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.ourClient.GameHelper;
import it.unibo.ai.didattica.competition.tablut.simulator.TablutGame;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

/*
 * We assume in the dataset there are no final state
 */
public class TablutStateSimulator {
    int MATCHES = 100;
    int MAX_TURNS = 500;

    public static void main(String[] args) {
        List<String> dataset = parseJSONDataset();
        List<State> states = stringsToStates(dataset);
        List<Double> win_percs = new ArrayList<>();

        TablutStateSimulator sim = new TablutStateSimulator();
        int i = 0;
        for (State s : states) {
            if (i % 100 == 0 && i > 0)
                System.out.println(i);
                // break;
            double win_perc = sim.run(s);
            System.out.println(win_perc);
            win_percs.add(win_perc);
            i++;
        }

        printJSON(win_percs);

        return;
    }

    public double run(State state) {
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
                // e.printStackTrace();
                System.out.println("ERR");
            }

            if (res == null) {
                errors++;
            } else
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

        // state = saved_state.clone();
        // state.setTurn(Turn.BLACK);

        // for (i = 0; i < MATCHES; i++) {
        //     Turn res = null;
        //     try {
        //         res = playRandGame(state.clone());
        //     } catch (Exception e) {
        //         // System.out.print(res);
        //         System.out.println("ERR");
        //     }

        //     if (res == null) {
        //         errors++;
        //     } else
        //         switch (res) {
        //             case WHITEWIN:
        //                 whiteWins++;
        //                 break;
        //             case DRAW:
        //                 draws++;
        //                 break;
        //             case BLACKWIN:
        //                 blackWins++;
        //                 break;
        //             default:
        //                 errors++;
        //                 break;
        //         }
        // }

        return (double) whiteWins / MATCHES;
    }

    private Turn playRandGame(State state) throws NullPointerException {
        Action move;
        int turns = 0;

        if (TablutGame.isGameover(state))
            return null;

        try {
            // game loop
            while (true) {
                if ((turns > this.MAX_TURNS)) {
                    return Turn.DRAW; // draw if exceed MAX_TURNS
                }

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

                if (TablutGame.isGameover(state))
                    break;

                turns++;
            }
        } catch (NullPointerException e) {
            throw e;
        }

        return state.getTurn();
    }

    private static List<String> parseJSONDataset() {
        Gson gson = new Gson();
        String jsonOutput = "";
        try {
            jsonOutput = new String(Files.readAllBytes(Paths.get("dataset.json")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Type listType = new TypeToken<List<String>>() {
        }.getType();
        List<String> dataset = gson.fromJson(jsonOutput, listType);
        return dataset;
    }

    private static void printJSON(List<Double> list) {
        try {
            Writer writer = new FileWriter("dataset_y.json");
            Gson gson = new GsonBuilder().create();
            gson.toJson(list, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<State> stringsToStates(List<String> dataset) {
        List<State> states = new ArrayList<>();

        State tmp_state = new StateTablut();
        Pawn[][] tmp_board = new Pawn[9][9];

        for (String s : dataset) {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    tmp_board[i][j] = Pawn.fromString(Character.toString(s.charAt(i * 9 + j)));
                }
            }
            tmp_state.setBoard(tmp_board);
            states.add(tmp_state.clone());
        }

        return states;
    }

    private Action randMove(State state) {
        List<Action> available_actions = GameHelper.availableMoves(state);
        if (available_actions.size() == 0)
            return null;

        Random rand = new Random();
        return available_actions.get(rand.nextInt(available_actions.size()));
    }

}
