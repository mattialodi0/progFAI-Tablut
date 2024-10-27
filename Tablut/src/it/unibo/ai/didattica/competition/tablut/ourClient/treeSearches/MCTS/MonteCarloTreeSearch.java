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
        //     MCTSNode leaf = traverse(root);
        //     MCTSNode simulation_result = rollout(leaf);
        //     backpropagate(leaf, simulation_result);
        // }

        // return root.bestChildAction();


        // v2
        /* root_node  = Node(None, None)
  while time remains:
    n, s = root_node, copy.deepcopy(state)
    while not n.is_leaf():    # select leaf
      n = tree_policy_child(n)
      s.addmove(n.move)
    n.expand_node(s)          # expand
    n = tree_policy_child(n)
    while not terminal(s):    # simulate
      s = simulation_policy_child(s)
    result = evaluate(s)
    while n.has_parent():     # propagate
      n.update(result)
      n = n.parent

return best_move(tree) */
    }


    /* v2
     * 
     */
    
    @Override
    public Boolean hasMoreTime() {
        return true;
    }

    /* v1
    // node traversal
    private MCTSNode traverse(MCTSNode node) {
        while(!fullyExpanded(node)) {
            node = bestUTC();
        }

        // in case no children are present / node is terminal 
        return pick_unvisited(node.children) or node 
    }

    // randomly select a child node
    private MCTSNode rollout(MCTSNode node) {
        while(non_terminal(node)) {
            node = rollout_policy(node);
        }

        return result(node);
    }

    private MCTSNode rollout_policy(MCTSNode node) {
        return pick_random(node.children);
    }

    private void backpropagate(MCTSNode node, MCTSNode result) {
        if(node.isRoot())
            return;
        node.stats = update_stats(node, result);
        backpropagate(node.parent);
    }
    */
}
