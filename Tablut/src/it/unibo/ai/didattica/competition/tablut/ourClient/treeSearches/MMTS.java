package it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.ourClient.GameHelper;
import it.unibo.ai.didattica.competition.tablut.ourClient.LookupTable;
import it.unibo.ai.didattica.competition.tablut.ourClient.evaluations.Evaluations;
import it.unibo.ai.didattica.competition.tablut.ourClient.interfaces.TreeSearch;
import it.unibo.ai.didattica.competition.tablut.simulator.TablutGame;

/*
 * Implementation of the Minmax alg. with AlphaBeta pruning, lookuptable, and limited brenches
 */
public class MMTS implements TreeSearch {

    public static float maxEval = Float.NEGATIVE_INFINITY;
    public static float minEval = Float.POSITIVE_INFINITY;
    public static int avgs = 0;
    public static int avgs_num = 0;
    public int lookups = 0;
    public int lookups_hits = 0;
    

    private int depth;
    public LookupTable lookup = new LookupTable();

    public MMTS(int depth) {
        this.depth = depth;
    }

    @Override
    public Action searchTree(State state) {
        List<Action> moves = GameHelper.availableMoves(state);

        if (moves.size() == 0)
            System.out.println("big prolbem...");

        Action bestAction = null;
        State saved_state = state.clone();
        float score = 0;

        if (state.getTurn() == Turn.WHITE) {
            score = Float.NEGATIVE_INFINITY;
            for (Action m : moves) {
                state = TablutGame.makeMove(state, m);
                float cur = MiniMax(state, this.depth - 1, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, false);
                if (cur >= score) {
                    score = cur;
                    bestAction = m;
                }
                state = saved_state;
            }
        } else {
            score = Float.POSITIVE_INFINITY;
            for (Action m : moves) {
                state = TablutGame.makeMove(state, m);
                float cur = MiniMax(state, this.depth - 1, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, true);
                if (cur <= score) {
                    score = cur;
                    bestAction = m;
                }
                state = saved_state;
            }
        }

        try {
            // System.out.println("AAAAAAAA");
            // System.out.println("score "+score);
            // System.out.println("move"+bestAction);
            // System.out.println(TablutGame.makeMove(state, bestAction).toString());
            // System.out.println("Total lookups: " + lookups);
            // int perc = ((lookups_hits * 100) / lookups);
            // System.out.println("Lookup hits: " + lookups_hits + " - " + perc + "%");
            // avgs += perc;
            // avgs_num++;
        } catch (Exception e) {
        }

        return bestAction;
    }

    @Override
    public Boolean hasMoreTime() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hasMoreTime'");
    }

    private float MiniMax(State state, int depth, float alpha, float beta, Boolean isWhite) {

        // if this is a leaf
        if (state.getTurn().equals(Turn.DRAW)) {
            return 0;
        } else if (state.getTurn().equals(Turn.WHITEWIN)) {
            return Float.POSITIVE_INFINITY;
        } else if (state.getTurn().equals(Turn.BLACKWIN)) {
            return Float.NEGATIVE_INFINITY;
        }

        Float eval = lookup.lookForVisitedState(state.boardString());
        this.lookups++;
        if (eval != null) {
            this.lookups_hits++;
            return eval;
        }
        // max depth reached
        else if (depth <= 0) {
            eval = Evaluations.evaluateMaterial(state);
            // eval = Evaluations.evaluateAdvanced(state, state.getTurn());
            lookup.insertVisitededState(state.boardString(), eval);
            return eval;
        }

        List<Action> moves = GameHelper.availableMoves(state);
        if (moves.size() == 0) {
            return 0;
        }

        State saved_state = state.clone();
        List<Action> moves_evals = moves;

        // order moves by eval
        // if (this.depth - depth >= this.threshold) {
        List<Float> evals = new ArrayList<>();
        for (Action m : moves) {
            state = TablutGame.makeMove(state, m);
            evals.add(Evaluations.evaluateMaterial(state));
            // evals.add(Evaluations.evaluateAdvanced(state, state.getTurn()));
            state = saved_state;
        }
        moves_evals = orderByEval(moves, evals);
        // }

        if (isWhite) {
            float max_score = Float.NEGATIVE_INFINITY;
            int i = 0;
            for (Action m : moves_evals) {
                if (i > branchingFactor(this.depth - depth))
                    break;
                state = TablutGame.makeMove(state, m);
                float cur = MiniMax(state, depth - 1, alpha, beta, false);
                if (cur >= max_score) {
                    max_score = cur;
                }
                state = saved_state;
                if (beta <= alpha)
                    break;
                i++;
            }
            return max_score;
        } else {
            float min_score = Float.POSITIVE_INFINITY;
            int i = 0;
            for (Action m : moves_evals) {
                if (i > branchingFactor(this.depth - depth))
                    break;
                state = TablutGame.makeMove(state, m);
                float cur = MiniMax(state, depth - 1, alpha, beta, true);
                if (cur <= min_score) {
                    min_score = cur;
                }
                state = saved_state;
                if (beta <= alpha)
                    break;
                i++;
            }

            return min_score;
        }
    }

    private int branchingFactor(int depth) {
        if (depth <= 2)
            return 10;
        else if (depth <= 4)
            return 5;
        else if (depth <= 6)
            return 3;
        else if (depth <= this.depth)
            return 2;
        else
            return 0; // should not return this
    }

    private List<Action> orderByEval(List<Action> moves, List<Float> evals) {
        List<Pair<Action, Float>> pairedList = new ArrayList<>();

        for (int i = 0; i < moves.size(); i++) {
            pairedList.add(new Pair<>(moves.get(i), evals.get(i)));
        }

        // Ordinare la lista di coppie in base al valore Float
        pairedList.sort(Comparator.comparing(Pair::getSecond));

        // Estrai gli elementi ordinati
        List<Action> sortedItems = new ArrayList<>();
        for (Pair<Action, Float> pair : pairedList) {
            sortedItems.add(pair.getFirst());
        }

        return sortedItems;
    }

    class Pair<T, U> {
        private T first;
        private U second;

        public Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }

        public T getFirst() {
            return first;
        }

        public U getSecond() {
            return second;
        }
    }
}
