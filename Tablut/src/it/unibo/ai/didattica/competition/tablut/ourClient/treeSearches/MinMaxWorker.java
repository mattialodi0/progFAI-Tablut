package it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.concurrent.atomic.AtomicBoolean;

public class MinMaxWorker implements Runnable {
    private int depth;
    private int maxTimeMillis;
    private State state;
    private AtomicBoolean stopSearch;
    private Action bestAction = null;

    public MinMaxWorker(int depth, int maxTimeMillis, State state, AtomicBoolean stopSearch) {
        this.depth = depth;
        this.maxTimeMillis = maxTimeMillis;
        this.state = state;
        this.stopSearch = stopSearch;
    }

    @Override
    public void run() {
        if(!stopSearch.get()){
            MinMax searchStrategy = new MinMax(depth);
            bestAction = searchStrategy.searchTree(state);
        }
    }

    public Action getBestAction() {
        return bestAction;
    }
}