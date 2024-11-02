package it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches.MCTS;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches.TreeSearch;

public class MonteCarloTreeSearch implements TreeSearch {
    private Game rules;

    public MonteCarloTreeSearch(Game r) {
        this.rules = r;
    }

    @Override
    public Action searchTree(State state) {
        // v1
        // MCTSNode root = new MCTSNode(state.clone());

        // while(hasMoreTime()) {
        // MCTSNode leaf = traverse(root);
        // MCTSNode simulation_result = rollout(leaf);
        // backpropagate(leaf, simulation_result);
        // }

        // return root.bestChildAction();

        // v2
        MCTSNode root = new MCTSNode(state.clone());
        root.expand();

        while (hasMoreTime()) {
            // select promising node
            MCTSNode promising_node = null;

            // expand
            if (!promising_node.isLeaf()) {
                promising_node.expand();
            }
            
            // explore
            MCTSNode node_to_explore = promising_node;
            if (promising_node.hasChildren()) {
                node_to_explore = promising_node.getRandomChild();
            }

            // simulate
            int result = simulateRandomPlayout(node_to_explore);
            node_to_explore.backPropogate(result);
        }
        
        return root.bestChildAction();
    }

    /*
     * v2
     * 
     * private int simulateRandomPlayout(MCTSNode node) {}
     * 
     * 
     */

    @Override
    public Boolean hasMoreTime() {
        return true;
    }

    /*
     * v1
     * // node traversal
     * private MCTSNode traverse(MCTSNode node) {
     * while(!fullyExpanded(node)) {
     * node = bestUTC();
     * }
     * 
     * // in case no children are present / node is terminal
     * return pick_unvisited(node.children) or node
     * }
     * 
     * // randomly select a child node
     * private MCTSNode rollout(MCTSNode node) {
     * while(non_terminal(node)) {
     * node = rollout_policy(node);
     * }
     * 
     * return result(node);
     * }
     * 
     * private MCTSNode rollout_policy(MCTSNode node) {
     * return pick_random(node.children);
     * }
     * 
     * private void backpropagate(MCTSNode node, MCTSNode result) {
     * if(node.isRoot())
     * return;
     * node.stats = update_stats(node, result);
     * backpropagate(node.parent);
     * }
     */
}
