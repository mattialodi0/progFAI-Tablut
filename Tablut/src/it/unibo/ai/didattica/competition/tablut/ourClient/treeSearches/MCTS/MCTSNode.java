package it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches.MCTS;

import java.util.ArrayList;
import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.ourClient.GameHelper;

public class MCTSNode {
    private State state;
    private Action move;
    private MCTSNode parent;
    private List<MCTSNode> children;
    private int visits;              // possible runtime error caused by visibiilty
    private int wins;              // possible runtime error caused by visibiilty

    public MCTSNode(State state) {
        this.parent = null;
        this.children = null;
        this.state = state;
        this.move = null;
        this.visits = 0;
        this.wins = 0;
    }

    public MCTSNode(State state, Action move, MCTSNode parent) {
        this.parent = parent;
        this.children = null;
        this.state = state;
        this.move = move;
        this.visits = 0;
        this.wins = 0;
    }

    public Boolean isRoot() {
        return this.parent == null;
    }

    public Boolean isLeaf() {
        return this.state.getTurn() == Turn.WHITEWIN || this.state.getTurn() == Turn.BLACKWIN || this.state.getTurn() == Turn.DRAW;
    }

    public Boolean hasChildren() {
        return this.children.size() > 0;
    }

    public void expand() {
        List<Action> availableMoves = GameHelper.availableMoves(this.state);
        this.children = new ArrayList<MCTSNode>();

        for(Action m: availableMoves) {
            State new_state = state.clone();
            GameHelper.makeMove(new_state, m);
            this.children.add(new MCTSNode(new_state, m, this));
        }
    }

    public void update(Boolean result) {
        this.visits++;
        if(result)
            this.wins++;
    }

    // select the best child node with highest number of visits and return its move
    public Action bestChildAction() {
        if(children.size() == 0)
            return null;
        else {
            MCTSNode best_child = null;
            float best_child_ratio = 0;
            for(MCTSNode c: children) {
                if((c.wins/c.visits) >= best_child_ratio) {     // TODO change best child policy
                    best_child = c;
                    best_child_ratio = (c.visits/c.visits);
                }
            }
            return best_child.move;
        }
    }

    public void backPropogate(int result) {
        // TODO
    }
}
