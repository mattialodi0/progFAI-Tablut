package it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.ourClient.GameHelper;
import it.unibo.ai.didattica.competition.tablut.ourClient.LookupTable;
import it.unibo.ai.didattica.competition.tablut.ourClient.evaluations.Evaluations;
import it.unibo.ai.didattica.competition.tablut.ourClient.interfaces.TreeSearch;
import it.unibo.ai.didattica.competition.tablut.simulator.TablutGame;

public class MinMaxRunnable implements TreeSearch, Runnable  {
    public static float maxEval = Float.NEGATIVE_INFINITY;
    public static float minEval = Float.POSITIVE_INFINITY;
    public static int avgs = 0;
    public static int avgs_num = 0;
    public int nodes = 0;
    public int lookups = 0;
    public int lookups_hits = 0;
    public int nont_hits = 0;
    AtomicBoolean stopSearch;
    State state;
    Action bestAction1 = null;

    private int depth;
    public LookupTable lookup = new LookupTable();

    public MinMaxRunnable(int depth, AtomicBoolean stopSearch, State state) {
        this.depth = depth;
        this.stopSearch = stopSearch;
        this.state = state;
    }
    

    @Override
    public void run(){
        try {// Inserisci qui lo stato iniziale (da passare come parametro o ottenuto da altrove)
            bestAction1 = searchTree(this.state.clone());
        } catch (Exception e) {
            // Gestione delle eccezioni durante la ricerca
            System.out.println("Errore durante l'esecuzione di MinMaxRunnable: " + e.getMessage());
        }
    }

    public Action getBestAction() {
        return bestAction1;
    }
    
    @Override
    public Action searchTree(State state) {
        Action bestAction = null;
        State saved_state = state.clone();
        float score = 0;
        float alpha = Float.NEGATIVE_INFINITY;
        float beta = Float.POSITIVE_INFINITY;

        // get possible moves
        List<Action> moves = GameHelper.availableMoves(state.clone());
        if (moves.size() == 0)
            System.out.println("big problem...");
        List<Action> moves_evals = moves;

        // order moves by eval
        List<Float> evals = new ArrayList<>();
        for (Action m : moves) {
            state = TablutGame.makeMove(state.clone(), m);
            evals.add(Evaluations.evaluateAdvanced(state.clone(), state.getTurn()));
            state = saved_state.clone();
        }
        moves_evals = orderByEval(moves, evals, state.getTurn()==Turn.WHITE);
        

        if (state.getTurn() == Turn.WHITE) {
            score = Float.NEGATIVE_INFINITY;
            for (Action m : moves_evals) {
                if (stopSearch.get()) break; 

                state = TablutGame.makeMove(state.clone(), m);
                float cur = MiniMax(state.clone(), this.depth - 1, alpha, beta, false);
                if (cur > score) {
                    score = cur;
                    bestAction = m;
                    //this.bestAction1 = bestAction;
                }
                alpha = Math.max(alpha, cur);
                if (beta <= alpha)
                    break;
                state = saved_state.clone();
                
            }
        } else if (state.getTurn() == Turn.BLACK) {
            score = Float.POSITIVE_INFINITY;
            for (Action m : moves_evals) {
                if (stopSearch.get()) break;

                state = TablutGame.makeMove(state.clone(), m);
                float cur = MiniMax(state.clone(), this.depth - 1, alpha, beta, true);
                if (cur < score) {
                    score = cur;
                    bestAction = m;
                    //this.bestAction1 = bestAction;
                }
                beta = Math.min(beta, cur);
                if (beta <= alpha)
                break;
                state = saved_state.clone();
                
            }
        } else {
            System.out.println("big problem...");
        }

        // debug prints
        try {
            // System.out.println(" ");
            // System.out.println("score "+score);
            // System.out.println("move"+bestAction);
            // System.out.println(TablutGame.makeMove(state, bestAction).toString());
            // System.out.println("Nodes visited: " + nodes);
            // System.out.println("Total lookups: " + lookups);
            int perc = ((lookups_hits * 100) / lookups);
            // System.out.println("Lookup hits: " + lookups_hits + " - " + perc + "%");
            avgs += perc;
            avgs_num++;
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

        // Float eval = lookup.lookForVisitedState(state.boardString());
        // this.lookups++;
        // if (eval != null) {
        // this.lookups_hits++;
        // return eval;
        // }
        // // max depth reached
        // else if (depth <= 0) {
        // eval = Evaluations.evaluateMaterial(state);
        // // eval = Evaluations.evaluateAdvanced(state, state.getTurn());
        // lookup.insertVisitededState(state.boardString(), eval);
        // return eval;
        // }

        if (depth <= 0) {
            Float eval = lookup.lookForVisitedState(state.boardString());
            if (eval == null) {
                // eval = Evaluations.evaluateMaterial(state);
                eval = Evaluations.evaluateAdvanced(state.clone(), state.getTurn());
                lookup.insertVisitededState(state.boardString(), eval);
                this.lookups_hits++;
            }
            this.lookups++;
            return eval;
        }

        List<Action> moves = GameHelper.availableMoves(state.clone());
        if (moves.size() == 0) {
            return 0;
        }

        State saved_state = state.clone();
        List<Action> moves_evals = moves;

        // order moves by eval
        List<Float> evals = new ArrayList<>();
        for (Action m : moves) {
            state = TablutGame.makeMove(state.clone(), m);
            // evals.add(Evaluations.evaluateMaterial(state));
            evals.add(Evaluations.evaluateAdvanced(state.clone(), state.getTurn()));
            state = saved_state.clone();
        }
        moves_evals = orderByEval(moves, evals, state.getTurn()==Turn.WHITE);

        if (isWhite) {
            float max_score = Float.NEGATIVE_INFINITY;
            
            for (Action m : moves_evals) {
                if(stopSearch.get()) break;

                state = TablutGame.makeMove(state.clone(), m);
                float cur = MiniMax(state.clone(), depth - 1, alpha, beta, false);
                max_score = Math.max(max_score, cur);
                alpha = Math.max(alpha, cur);
                if (beta <= alpha)
                    break;
                state = saved_state.clone();
            }
            return max_score;
        } else {
            float min_score = Float.POSITIVE_INFINITY;

            for (Action m : moves_evals) {
                if(stopSearch.get()) break;

                state = TablutGame.makeMove(state.clone(), m);
                float cur = MiniMax(state.clone(), depth - 1, alpha, beta, true);
                min_score = Math.min(min_score, cur);
                beta = Math.min(beta, cur);
                if (beta <= alpha)
                    break;
                state = saved_state.clone();
            }

            return min_score;
        }
    }

    protected int branchingFactor(int depth) {
        /*if (depth <= 2)
            return 15;
        else if (depth <= 4)
            return 10;
        else if (depth <= 6)
            return 5;
        else if (depth <= this.depth)
            return 3;
        else
            return 0;*/ // should not return this
        return 50;
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