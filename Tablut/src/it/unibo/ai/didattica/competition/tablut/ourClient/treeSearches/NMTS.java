package it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches;

import java.util.ArrayList;
import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.ourClient.GameHelper;
import it.unibo.ai.didattica.competition.tablut.ourClient.evaluations.Evaluations;
import it.unibo.ai.didattica.competition.tablut.ourClient.interfaces.TreeSearch;
import it.unibo.ai.didattica.competition.tablut.simulator.TablutGame;


public class NMTS implements TreeSearch {

    public static float maxEval = Float.NEGATIVE_INFINITY;
    public static float minEval = Float.NEGATIVE_INFINITY;
    private Action bestAction; // Here the best move is stored
    private Turn turn;

    public NMTS(Turn t) {
        this.turn = t;
    }

    @Override
    public Action searchTree(State state) {
        negMaxSearch(state, 6, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, 1, true);
        
        // System.out.println("Eval: "+ Evaluations.evaluateMaterial(TablutGame.makeMove(state, getBestAction()), turn));
        // System.out.println("Move: "+ getBestAction());

        return this.getBestAction();
    }

    // Returns the score of the root node - state. The best Action will be stored
    // in the attribute of the class. Player is always +1 at the beginning.
    // isRoot is used to check when it has sense to update the bestMove attribute so
    // it's not updated in every recursive call
    public float negMaxSearch(State state, int depth, float alpha, float beta, int player, Boolean isRoot) {

        // call to some function that checks the possible moves, if there are no
        // possible moves the array is empty, becuase it means that the game ends.
        List<Action> moves = availableActions(state);

        if (depth == 0 || moves.size() == 0) {
            // return Evaluations.evaluateAdvanced(state, turn);
            return Evaluations.evaluateMaterial(state, state.getTurn());
        }
        
        float score = Float.NEGATIVE_INFINITY;
        State prevNode = state.clone(); 

        for (Action a : moves) {
            state = TablutGame.makeMove(state, a);
            
            // call the other player
            float cur = -negMaxSearch(state, depth - 1, -beta, -alpha, -player, false);

            /* debug */
            if(cur > maxEval)
                maxEval = cur;
            if(cur < minEval)
                minEval = cur;
            /* debug */

            if (cur > score) {
                score = cur;
                if (isRoot) {
                    this.bestAction = a;
                }
            }
            if (score > alpha) {
                alpha = score;
            }
            // Undo move -> return in the current state
            state = prevNode;
            if (alpha >= beta) {
                break;
            }
        }
        return score;
    }

    @Override
    public Boolean hasMoreTime() {
        // TODO Auto-generated method stub
        return true;
    }

    // if the Turn == WW, BW, D return an empty list. Given a state we can get the
    // Turn with getTurn()
    public List<Action> availableActions(State state) {
        if(state.getTurn() == Turn.WHITEWIN || state.getTurn() == Turn.BLACKWIN || state.getTurn() == Turn.DRAW)
            return new ArrayList<Action>();
        else
            return GameHelper.availableMoves(state);
    }

    public Action getBestAction() {
        return this.bestAction;
    }

}
