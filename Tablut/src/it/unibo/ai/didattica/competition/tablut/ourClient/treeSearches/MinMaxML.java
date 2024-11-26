package it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
public class MinMaxML implements TreeSearch {

    public static float maxEval = Float.NEGATIVE_INFINITY;
    public static float minEval = Float.POSITIVE_INFINITY;
    public static int avgs = 0;
    public static int avgs_num = 0;
    public static int tot_branching_cuts = 0;
    public static int good_branching_cuts = 0;
    public int nodes = 0;
    public int lookups = 0;
    public int lookups_hits = 0;
    public int nont_hits = 0;

    private int depth;
    public LookupTable lookup = new LookupTable();

    public MinMaxML(int depth) {
        this.depth = depth;
    }

    @Override
    public Action searchTree(State state) {
        Action bestAction = null;
        State saved_state = state.clone();
        float score = 0;
        float alpha = Float.NEGATIVE_INFINITY;
        float beta = Float.POSITIVE_INFINITY;

        // get possible moves
        List<Action> moves = GameHelper.availableMoves(state);
        if (moves.size() == 0)
            System.out.println("big prolbem...");
        List<Action> moves_evals = moves;

        // order moves by eval
        List<Float> evals = new ArrayList<>();
        for (Action m : moves) {
            state = TablutGame.makeMove(state, m);
            evals.add(Evaluations.evaluate(state));
            state = saved_state.clone();
        }
        moves_evals = orderByEval(moves, evals, state.getTurn()==Turn.WHITE);
        int i = 0;

        if (state.getTurn() == Turn.WHITE) {
            score = Float.NEGATIVE_INFINITY;
            for (Action m : moves_evals) {
                state = TablutGame.makeMove(state, m);
                float cur = MiniMax(state, this.depth - 1, alpha, beta, false);
                if (cur >= score) {
                    score = cur;
                    bestAction = m;
                }
                alpha = Math.max(alpha, cur);
                if (beta <= alpha)
                    break;
                state = saved_state.clone();
                i++;
            }
        } else if (state.getTurn() == Turn.BLACK) {
            score = Float.POSITIVE_INFINITY;
            for (Action m : moves_evals) {
                state = TablutGame.makeMove(state, m);
                float cur = MiniMax(state, this.depth - 1, alpha, beta, true);
                if (cur <= score) {
                    score = cur;
                    bestAction = m;
                }
                beta = Math.min(beta, cur);
                if (beta <= alpha)
                break;
                state = saved_state.clone();
                i++;
            }
        } else {
            System.out.println("big prolbem...");
        }

        // debug prints
        try {
            // System.out.println(" ");
            // System.out.println("score "+score);
            // System.out.println("move"+bestAction);
            // System.out.println(TablutGame.makeMove(state, bestAction).toString());
            // System.out.println("Nodes visited: " + nodes);
            // System.out.println("Total lookups: " + lookups);
            // int perc = ((lookups_hits * 100) / lookups);
            // System.out.println("Lookup hits: " + lookups_hits + " - " + perc + "%");
            // avgs += perc;
            // avgs_num++;
            // tot_branching_cuts++;
            // if((moves_evals.indexOf(bestAction) <= branchingFactor(this.depth - depth))) {
            //     good_branching_cuts++;
            // }
            // System.out.println(moves_evals.indexOf(bestAction) +"/"+ (moves_evals.size()-1));
        } catch (Exception e) {
        }

        return bestAction;
    }

    @Override
    public Boolean hasMoreTime() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hasMoreTime'");
    }

    
    protected float MiniMax(State state, int depth, float alpha, float beta, Boolean isWhite) {
        this.nodes++;

        // if this is a leaf
        if (state.getTurn().equals(Turn.DRAW)) {
            return 0;
        } else if (state.getTurn().equals(Turn.WHITEWIN)) {
            return Float.POSITIVE_INFINITY;
        } else if (state.getTurn().equals(Turn.BLACKWIN)) {
            return Float.NEGATIVE_INFINITY;
        }

        if (depth <= 0) {
            Float eval = lookup.lookForVisitedState(state.boardString());
            if (eval == null) {
                eval = Evaluations.evaluate(state);
                lookup.insertVisitededState(state.boardString(), eval);
                this.lookups_hits++;
            }
            this.lookups++;
            return eval;
        }

        List<Action> moves = GameHelper.availableMoves(state);
        if (moves.size() == 0) {
            return 0;
        }

        State saved_state = state.clone();
        List<Action> moves_evals = moves;

        // order moves by eval
        List<Float> evals = new ArrayList<>();
        for (Action m : moves) {
            state = TablutGame.makeMove(state, m);
            evals.add(Evaluations.evaluate(state));
            state = saved_state.clone();
        }
        moves_evals = orderByEval(moves, evals, state.getTurn()==Turn.WHITE);

        if (isWhite) {
            float max_score = Float.NEGATIVE_INFINITY;
            int i = 0;
            for (Action m : moves_evals) {
                state = TablutGame.makeMove(state, m);
                float cur = MiniMax(state, depth - 1, alpha, beta, false);
                max_score = Math.max(max_score, cur);
                alpha = Math.max(alpha, cur);
                if (beta <= alpha)
                    break;
                state = saved_state.clone();
                i++;
            }
            return max_score;
        } else {
            float min_score = Float.POSITIVE_INFINITY;
            int i = 0;
            for (Action m : moves_evals) {
                state = TablutGame.makeMove(state, m);
                float cur = MiniMax(state, depth - 1, alpha, beta, true);
                min_score = Math.min(min_score, cur);
                beta = Math.min(beta, cur);
                if (beta <= alpha)
                    break;
                i++;
                state = saved_state.clone();
            }

            return min_score;
        }
    }

    protected List<Action> orderByEval(List<Action> moves, List<Float> evals, Boolean isWhite) {
        List<Pair<Action, Float>> pairedList = new ArrayList<>();

        for (int i = 0; i < moves.size(); i++) {
            pairedList.add(new Pair<>(moves.get(i), evals.get(i)));
        }

        pairedList.sort(Comparator.comparing(Pair::getSecond));
        if(isWhite) {
            Collections.reverse(pairedList);
        }
        
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