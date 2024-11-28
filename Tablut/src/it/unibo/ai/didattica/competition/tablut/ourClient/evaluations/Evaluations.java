package it.unibo.ai.didattica.competition.tablut.ourClient.evaluations;

import java.util.ArrayList;
import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.ourClient.ourUtilities.GameHelper;

public class Evaluations {

    public static float evaluate(State state) {
        // float eval = evaluateQuick(state);
        // float eval = evaluateFinal(state);
        float eval = evaluateFinal(state);
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

            if (pawns > 20) { // early game
                eval = 2 * Math.atan(material * 5) - Math.atan(king_center_distance * 5)
                        + Math.atan(king_open_files * 5) - (2 * Math.atan(dispersion * 5));
            } else if (pawns > 16) { // mid game
                eval = Math.atan(material * 5) + Math.atan(king_center_distance * 5) + Math.atan(king_open_files * 5)
                        + (2 * Math.atan(dispersion * 5));
            } else { // end game
                eval = Math.atan(material * 5) + 2 * Math.atan(king_center_distance * 5)
                        + 4 * Math.atan(king_open_files * 5);
            }

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
            float surrounding_king = s / 4;

            List<Integer> distances = new ArrayList<>();
            String str = state.toLinearString();
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) == 'B') {
                    int[] p = { i / 9, i % 9 };
                    distances.add(Math.abs(p[0] - 4) + Math.abs(p[1] - 4));
                }
            }
            float dispersion = distances.stream().mapToInt(f -> f).sum() / distances.size();

            if (pawns > 20) { // early game
                eval = 4 * Math.atan(material * 5) - Math.atan(king_open_files * 5) - (2 * Math.atan(dispersion * 5));
            } else if (pawns > 16) { // mid game
                eval = 2 * Math.atan(material * 5) - Math.atan(king_open_files * 5) + Math.atan(surrounding_king * 5)
                        - Math.atan(dispersion * 5);
            } else { // end game
                eval = Math.atan(material * 5) - 2 * Math.atan(king_open_files * 5) + Math.atan(surrounding_king * 5);
            }
        }

        return (float) eval;
    }

    private static float evaluateFinal(State state) {
        Turn currentTurn = state.getTurn();

        if (currentTurn == Turn.DRAW) {
            return 0;
        } else if (currentTurn == Turn.WHITEWIN) {
            return Float.POSITIVE_INFINITY;
        } else if (currentTurn == Turn.BLACKWIN) {
            return Float.NEGATIVE_INFINITY;
        }
        float eval = 0;
        float alive;
        float eaten;
        if (currentTurn == (Turn.WHITE)) {
            alive = (float) state.getNumberOf(Pawn.WHITE) / 8;
            eaten = (float) (16 - state.getNumberOf(Pawn.BLACK)) / 16;
            int king = state.toLinearString().indexOf('K');
            int x = (int) king / 9;
            int y = (int) king % 9;
            double king_center_distance = (double) (Math.sqrt(Math.pow((4 - x), 2) + Math.pow(4 - y, 2)) / 7);
            double king_open_files = (double) GameHelper.getKingOpenFiles(state);
            List<Integer> distances = new ArrayList<>();
            String str = state.toLinearString();
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) == 'W') {
                    int[] p = { i / 9, i % 9 };
                    // allied_pawns.add(p);
                    distances.add(Math.abs(p[0] - 4) + Math.abs(p[1] - 4));
                }
            }
            float dispersion = distances.stream().mapToInt(f -> f).sum() / (distances.size() * 7);
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
            float surrounding_king;
            switch (s) {
                case 0:
                    surrounding_king = 1;
                    break;
                case 1:
                    surrounding_king = -0.3f;
                default:
                    surrounding_king = -1;
                    break;
            }

            int white_pawns = state.getNumberOf(Pawn.WHITE);
            int black_pawns = state.getNumberOf(Pawn.BLACK);
            float material = ((white_pawns * 2) - black_pawns);
            int pawns = white_pawns + black_pawns;
            if (pawns > 18) { // early game
                // eval = (float) (30 * material - king_open_files - dispersion
                //         + 10 * surrounding_king);
                eval = 10 * eaten;
                // System.out.println("Im evaluating eval: " + eval + "----------------------------------------");
            } else { // end game
                eval = (float) (10 * material + king_open_files + surrounding_king);
            }
        } else {
            // a dispersion parameter to get nearer the king
            eval = evaluateAdvanced(state, currentTurn);
        }
        return eval;
    }
}
