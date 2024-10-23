package it.unibo.ai.didattica.competition.tablut.ourClient;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.ourClient.interfaces.GameState;
import it.unibo.ai.didattica.competition.tablut.ourClient.interfaces.TreeSearch;

public class NegMaxTreeSearch implements TreeSearch {

    private Action bestAction; // Here the best move is stored

    @Override
    public float evaluate(State state) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Boolean hasMoreTime() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Action searchTree(State state) {
        negMaxSearch(state, 4, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 1);
        return this.getBestAction();
    }

    // returns the evaluation of the root node, not possible to return the action
    // because of the recursivness of the algorithm. The best Action will be stored
    // in an attribute. Player is always +1 at the beginning.
    public float negMaxSearch(State state, int depth, float alpha, float beta, int player) {

        // call to some function that checks the possible moves, if there are no
        // possible moves the array is empty, becuase it means that the game ends.
        Action[] moves = availableActions(state);

        // check if we are at the last level of depth or the node is terminal, so if
        // the game is over.
        if (depth == 0 || moves.length == 0) {
            return evaluate(state);
        }
        float score = Float.NEGATIVE_INFINITY;

        for (Action a : moves) {
            // execute the move
            State prevNode = state.clone();

            State s = checkMove(state, a); 

            // call the other player
            float cur = -negMaxSearch(s, depth - 1, -beta, -alpha, -player);

            if (cur > score) {
                score = cur;
                this.bestAction = a;
            }
            if (score > alpha) {
                alpha = score;
            }

            // Undo move. Does checkMove() modify the state you pass to it. If it does so,
            // we have to store the state before calling it so once it returns we can take
            // back the parent node.

            state = prevNode;

            if (alpha >= beta) {
                break;
            }
        }
        return score;
    }

    // if the Turn == WW, BW, D return an empty list. Given a state we can get the
    // Turn with getTurn()
    public Action[] availableActions(State state) {
        return null;
    }

    public Action getBestAction() {
        return this.bestAction;
    }

}
