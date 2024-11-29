package it.unibo.ai.didattica.competition.tablut.ourClient.evaluations;

import java.util.ArrayList;
import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.ourClient.ourUtilities.GameHelper;

public class Evaluations {

    public static float evaluate(State state) {
        float eval = evaluateFinal(state);
        return eval;
    }

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
            float king_center_distance = (float) (Math.sqrt(Math.pow((4 - x), 2) + Math.pow(4 - y, 2)));
            if (king_center_distance >= 1)
                king_center_distance = 10;
            else if (king_center_distance >= 0)
                king_center_distance = 5;
            else
                king_center_distance = 0;

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
            float surrounding_king = 0;
            if (k[0] == 4 && k[1] == 4) {
                switch (s) {
                    case 0:
                        surrounding_king = 0;
                        break;
                    case 1:
                        surrounding_king = -1;
                    case 2:
                        surrounding_king = -4;
                    case 3:
                        surrounding_king = -6;
                    default:
                        surrounding_king = -10;
                }
            } else if ((k[0] == 3 && k[1] == 4) || (k[0] == 5 && k[1] == 4) || (k[0] == 4 && k[1] == 5)
                    || (k[0] == 4 && k[1] == 3)) {
                switch (s) {
                    case 0:
                        surrounding_king = 1;
                        break;
                    case 1:
                        surrounding_king = -2;
                    case 2:
                        surrounding_king = -8;
                    default:
                        surrounding_king = -10;
                }
            } else {
                switch (s) {
                    case 0:
                        surrounding_king = 2;
                        break;
                    case 1:
                        surrounding_king = -2;
                    default:
                        surrounding_king = -10;
                }
            }

            int white_pawns = state.getNumberOf(Pawn.WHITE);
            int black_pawns = state.getNumberOf(Pawn.BLACK);
            int pawns = white_pawns + black_pawns;

            int king_open_files = GameHelper.getKingOpenFiles(state);
            float escapesOpen = 0;
            switch (king_open_files) {
                case 4:
                    escapesOpen = 14;
                case 3:
                    escapesOpen = 12;
                case 2:
                    escapesOpen = 10;
                case 1:
                    escapesOpen = 2;
                case 0:
                    escapesOpen = -1;
            }

            // if(black_pawns >= 12) {
            //     eval = 200 * alive + 300 * eaten + surrounding_king + escapesOpen - 2 * king_center_distance;
            // }
            // else
            //     eval = 200 * alive + 200 * eaten + surrounding_king + escapesOpen + king_center_distance;
                
            if (pawns > 20) { // early game
                eval = 250 * alive + 300 * eaten + surrounding_king + escapesOpen - 4 * king_center_distance;
            } else { // end game
                eval = 250 * alive + 200 * eaten + 2 * surrounding_king + 20 * escapesOpen;
            }
        } else {
            alive = (float) state.getNumberOf(Pawn.BLACK) / 16;
            eaten = (float) (state.getNumberOf(Pawn.WHITE)) / 8; // 8 -
            List<int[]> emptyTiles = GameHelper.populateEmptyList(state);
            int[] k = GameHelper.getKingPosition(state);

            float can_attack_king = Heuristics.approachingPawns(GameHelper.populatePawnList(state), k, emptyTiles,
                    state);
            int king_open_files = GameHelper.getKingOpenFiles(state);
            float escapesOpen = 0;
            switch (king_open_files) {
                case 4:
                    escapesOpen = -20;
                case 3:
                    escapesOpen = -18;
                case 2:
                    escapesOpen = -16;
                case 1:
                    escapesOpen = -5;
                case 0:
                    escapesOpen = 5;
            }

            int s = 0;
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
            if (k[0] == 4 && k[1] == 4) {
                s -= 2;
            } else if ((k[0] == 3 && k[1] == 4) || (k[0] == 5 && k[1] == 4) || (k[0] == 4 && k[1] == 5)
                    || (k[0] == 4 && k[1] == 3)) {
                s -= 1;
            }

            List<Integer> distances = new ArrayList<>();
            String str = state.toLinearString();
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) == 'B') {
                    int[] p = { i / 9, i % 9 };
                    distances.add(Math.abs(p[0] - 4) + Math.abs(p[1] - 4));
                }
            }
            float dispersion = distances.stream().mapToInt(f -> f).sum() / distances.size();

            int white_pawns = state.getNumberOf(Pawn.WHITE);
            int black_pawns = state.getNumberOf(Pawn.BLACK);
            int pawns = white_pawns + black_pawns;
            if (pawns > 20) {
                eval = -350 * alive + 450 * eaten - 5 * can_attack_king - 15 * escapesOpen - 15 * s + 5 * dispersion;
            } else {
                eval = -300 * alive + 300 * eaten - 10 * can_attack_king - 15 * escapesOpen - 15 * s + 5 * dispersion;
            }

        }
        return eval;
    }
}
