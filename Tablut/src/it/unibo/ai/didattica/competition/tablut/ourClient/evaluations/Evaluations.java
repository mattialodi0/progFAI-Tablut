package it.unibo.ai.didattica.competition.tablut.ourClient.evaluations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.ourClient.GameHelper;

public class Evaluations {

    public static float evaluate(State state) {
        float eval = evaluateQuick(state);
        // float eval = evaluateAdvanced(state, state.getTurn());
        // System.out.println(eval);
        return eval;
    }

    /* Basic heuristic, normalized between, more pieces -> more points */
    public static float evaluateMaterial(State state) {
        if (state.getTurn().equals(Turn.DRAW)) {
            return 0;
        } else if (state.getTurn().equals(Turn.WHITEWIN)) {
            return Float.POSITIVE_INFINITY;
        } else if (state.getTurn().equals(Turn.BLACKWIN)) {
            return Float.NEGATIVE_INFINITY;
        }

        float eval = 0;

        // Random random = new Random();
        eval = ((state.getNumberOf(Pawn.WHITE) * 2) - state.getNumberOf(Pawn.BLACK));// +
                                                                                     // ((random.nextFloat()-0.5f)/1000);

        return (float) Math.atan(eval * 5);
        // return eval;
    }

    /* Tomaz heuristic */
    public static float evaluateAdvanced(State state, Turn t) {
        Turn currentTurn = state.getTurn();
        if (currentTurn == Turn.DRAW) {
            return 0;
        } else if (currentTurn == Turn.WHITEWIN) {
            return Float.POSITIVE_INFINITY;
        } else if (currentTurn == Turn.BLACKWIN) {
            return Float.NEGATIVE_INFINITY;
        }

        if (currentTurn == Turn.WHITE) {
            Float[] gameWeights = { 35f, 18f, 42f, 5f };

            HeuristicsWhite heuristic = new HeuristicsWhite(gameWeights);
            return heuristic.evaluate(state);
        } else if (currentTurn == Turn.BLACK) {
            Float[] gameWeights = { 35f, 45.0f, 5.0f, 15.0f, 15.0f, 9.0f };

            HeuristicsBlack heuristic = new HeuristicsBlack(gameWeights);
            float e = heuristic.evaluate(state);
            return e;
        } else
            return 0;
    }

    public static float evaluateQuick(State state) {
        Turn currentTurn = state.getTurn();
        if (currentTurn == Turn.DRAW) {
            return 0;
        } else if (currentTurn == Turn.WHITEWIN) {
            return Float.POSITIVE_INFINITY;
        } else if (currentTurn == Turn.BLACKWIN) {
            return Float.NEGATIVE_INFINITY;
        }

        double eval = 0;

        int white_pawns = state.getNumberOf(Pawn.WHITE);
        int black_pawns = state.getNumberOf(Pawn.BLACK);
        int pawns = white_pawns + black_pawns;
        double material = (double) ((white_pawns * 2) - black_pawns) / 16;
        
        double king_open_files = (double) GameHelper.getKingOpenFiles(state) / 4;

        if (currentTurn == Turn.WHITE) {
            int k = state.toLinearString().indexOf('K');
            int x = (int) k / 9;
            int y = (int) k % 9;
            double king_center_distance = (double) (Math.sqrt(Math.pow((4 - x), 2) + Math.pow(4 - y, 2)) / 7);


            // List<int[]> allied_pawns = new ArrayList<>();
            List<Integer> distances = new ArrayList<>();
            String s = state.toLinearString();
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == 'W') {
                    int[] p = { i / 9, i % 9 };
                    // allied_pawns.add(p);
                    distances.add(Math.abs(p[0] - 4) + Math.abs(p[1] - 4));
                }
            }
            float dispersion = distances.stream().mapToInt(f -> f).sum() / distances.size();

            // intuitive
            if (pawns > 20) { // early game
                eval = 2 * Math.atan(material) - Math.atan(king_center_distance) - Math.atan(king_open_files) - (2 * Math.atan(dispersion));
            } else if (pawns > 16) { // mid game
                eval = Math.atan(material) + Math.atan(king_center_distance) - Math.atan(king_open_files) + (2 * Math.atan(dispersion));
            } else { // end game
                eval = Math.atan(material) + 2 * Math.atan(king_center_distance) - 4 * Math.atan(king_open_files);
            }

            // ML
            // if (pawns > 20) { // early game
            // eval = -1.52 * Math.atan(material) + 0.42 * Math.atan(king_center_distance)
            // - 0.011 * Math.atan(king_open_files) - 0.22 * Math.atan(dispersion) + 3.18;
            // } else if (pawns > 16) { // mid game
            // eval = -0.19 * Math.atan(material) + 0.017 * Math.atan(king_center_distance)
            // - 0.07 * Math.atan(dispersion) + 1;
            // } else { // end game
            // eval = -0.124 * Math.atan(material) + 0.034 * Math.atan(king_center_distance)
            // - 0.044 * Math.atan(king_open_files) + 0.95;
            // }

        } else if (currentTurn == Turn.BLACK) {
            int s = 0;

            int[] k = GameHelper.getKingPosition(state);
            try {
                if (state.getPawn(k[0] - 1, k[1]) == Pawn.BLACK)
                    s++;
            } catch (Exception e) {
            }
            try {
                if (state.getPawn(k[0] + 1, k[1]) == Pawn.BLACK)
                    s++;
            } catch (Exception e) {
            }
            try {
                if (state.getPawn(k[0], k[1] - 1) == Pawn.BLACK)
                    s++;
            } catch (Exception e) {
            }
            try {
                if (state.getPawn(k[0], k[1] + 1) == Pawn.BLACK)
                    s++;
            } catch (Exception e) {
            }
            float capturinging_king = s / 4;

            List<Integer> distances = new ArrayList<>();
            String str = state.toLinearString();
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) == 'B') {
                    int[] p = { i / 9, i % 9 };
                    distances.add(Math.abs(p[0] - k[0]) + Math.abs(p[1] - k[1]));
                }
            }
            float surrounding_king = distances.stream().mapToInt(f -> f).sum() / distances.size();

            if (pawns > 20) { // early game
                eval = 6 * Math.atan(material) + Math.atan(king_open_files) + Math.atan(capturinging_king) - 2 * Math.atan(surrounding_king);
            } else if (pawns > 16) { // mid game
                eval = 2 * Math.atan(material) + Math.atan(king_open_files) + Math.atan(capturinging_king) - Math.atan(surrounding_king);
            } else { // end game
                eval = Math.atan(material) + 5 * Math.atan(king_open_files) + Math.atan(capturinging_king) - Math.atan(surrounding_king);
            }
        }

        return (float) eval;
    }

}
