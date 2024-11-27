package it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.ourClient.GameHelper;
import it.unibo.ai.didattica.competition.tablut.ourClient.LookupTable;
import it.unibo.ai.didattica.competition.tablut.ourClient.evaluations.Evaluations;
import it.unibo.ai.didattica.competition.tablut.ourClient.interfaces.TreeSearch;
import it.unibo.ai.didattica.competition.tablut.simulator.TablutGame;

public class MultiThreadMinMaxLauncher implements TreeSearch {
    private int depth;
    public LookupTable lookup = new LookupTable();
    private MinMax MinMax;

    public MultiThreadMinMaxLauncher(int depth) {
        this.depth = depth;
        MinMax = new MinMax(depth);
    }

    public Action searchTree(State state) {

        Action bestAction = null;
        State saved_state = state.clone();
        float score = 0;

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
        moves_evals = MinMax.orderByEval(moves, evals, state.getTurn()==Turn.WHITE);
        List<ThreadStruct> threadStructList = new ArrayList<ThreadStruct>();

        if (state.getTurn() == Turn.WHITE) {
            score = Float.NEGATIVE_INFINITY;

            for (int j = 0; j < moves_evals.size(); j++) {
                // if (j > MinMax.branchingFactor(0))
                //     break;
                
                state = TablutGame.makeMove(state, moves_evals.get(j));

                MTMinMax mtMinMax = new MTMinMax(state, this.depth - 1, false);
                Thread object = new Thread(mtMinMax);
                object.start();
                ThreadStruct t = new ThreadStruct(mtMinMax, object, 0);
                threadStructList.add(t);

                state = saved_state.clone();
            }
            for (int j = 0; j < moves_evals.size(); j++) {
                // if (j > MinMax.branchingFactor(0))
                //     break;

                float cur = 0;
                try {
                    ThreadStruct t = threadStructList.get(j);
                    MTMinMax mtMinMax = t.gettMinMax();
                    Thread object = t.getobject();
                    object.join();
                    cur = mtMinMax.getEval();
                } catch (InterruptedException e) {
                    System.out.println("Exception reading thread");
                }

                if (cur >= score) {
                    score = cur;
                    bestAction = moves_evals.get(j);
                }
            }
        }
        else if (state.getTurn() == Turn.BLACK) {
            score = Float.POSITIVE_INFINITY;

            for (int j = 0; j < moves_evals.size(); j++) {
                // if (j > MinMax.branchingFactor(0))
                //     break;

                    state = TablutGame.makeMove(state, moves_evals.get(j));

                    MTMinMax mtMinMax = new MTMinMax(state, this.depth - 1, true);
                    Thread object = new Thread(mtMinMax);
                    object.start();
                    ThreadStruct t = new ThreadStruct(mtMinMax, object, 0);
                    threadStructList.add(t);
    
                    state = saved_state.clone();
            }
            for (int j = 0; j < moves_evals.size(); j++) {
                // if (j > MinMax.branchingFactor(0))
                //     break;

                float cur = 0;
                try {
                    ThreadStruct t = threadStructList.get(j);
                    MTMinMax mtMinMax = t.gettMinMax();
                    Thread object = t.getobject();
                    object.join();
                    cur = mtMinMax.getEval();
                } catch (InterruptedException e) {
                    System.out.println("Exception reading thread");
                }

                if (cur <= score) {
                    score = cur;
                    bestAction = moves_evals.get(j);
                }
            }
        } else {
            System.out.println("big prolbem...");
        }

        return bestAction;
    }

    @Override
    public Boolean hasMoreTime() {
        return true;
    }

    public class MTMinMax implements Runnable {
        private State state;
        private int depth;
        private float alpha;
        private float beta;
        private Boolean isWhite;
        private volatile float eval;

        public MTMinMax(State state, int depth, Boolean isWhite) {
            this.state = state;
            this.depth = depth;
            this.isWhite = isWhite;
        }

        @Override
        public void run() {
            try {
                MinMax minMax = new MinMax(depth);
                this.eval = minMax.MiniMax(this.state, this.depth, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, this.isWhite);
            } catch (Exception e) {
                System.out.println("Exception in thread");
            }
        }

        public float getEval() {
            return this.eval;
        }
    }

    public class ThreadStruct {
        MTMinMax mtMinMax;
        Thread object;
        float eval;

        public ThreadStruct(MTMinMax mtMinMax, Thread object, float eval) {
            this.mtMinMax = mtMinMax;
            this.object = object;
            this.eval = eval;
        }

        public MTMinMax gettMinMax() {
            return this.mtMinMax;
        }

        public Thread getobject() {
            return this.object;
        }

        public float geteval() {
            return this.eval;
        }
    }
}