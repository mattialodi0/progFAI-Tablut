package it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.ourClient.interfaces.TreeSearch;

public class IterativeDeepening implements TreeSearch {
    private long start_time = 0;

    public IterativeDeepening() {
        this.start_time = System.currentTimeMillis();
    }

    @Override
    public Action searchTree(State state) {
        Action bestAction = null;
        int i = 0;
        while(hasMoreTime() && i <= 8) {
            TreeSearch searchStrategy = new MinMax(i); 
            i++;
		    bestAction = searchStrategy.searchTree(state);
        }        
        return bestAction;
    }

    @Override
    public Boolean hasMoreTime() {
        return (System.currentTimeMillis()-start_time) < 5000;
    }
}
