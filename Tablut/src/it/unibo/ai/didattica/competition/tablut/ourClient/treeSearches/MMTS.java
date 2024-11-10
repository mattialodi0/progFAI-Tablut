package it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches;

import java.util.ArrayList;
import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.ourClient.GameHelper;
import it.unibo.ai.didattica.competition.tablut.ourClient.evaluations.Evaluations;
import it.unibo.ai.didattica.competition.tablut.ourClient.interfaces.TreeSearch;
import it.unibo.ai.didattica.competition.tablut.simulator.TablutGame;

public class MMTS implements TreeSearch {

    public static float maxEval = Float.NEGATIVE_INFINITY;
    public static float minEval = Float.POSITIVE_INFINITY;
    private int depth;

    public MMTS(int depth) {
        this.depth = depth;
    }

    @Override
    public Action searchTree(State state) {

        Action bestAction = null;
        float score = Float.NEGATIVE_INFINITY;      
        List<Action> moves = availableActions(state);

        for (Action action : moves) {
            State newState = state.clone();
            newState = TablutGame.makeMove(state, action);
            
            float current = minPlayer(newState, depth - 1, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);

            if (current > score) {
                bestAction = action;
                score = current;
            }
        }

        // System.out.println("Eval: "+ Evaluations.evaluateMaterial(TablutGame.makeMove(state, bestAction), state.getTurn()));
        // System.out.println("Move: "+ getBestAction());
        
        return bestAction;
    }

    public float minPlayer(State state, int depth, float alpha, float beta) {
        List<Action> moves = availableActions(state); // Inefficient, better to control here if the game ended,
                                                      // because
        // if we are at depth 0 we have to control the available actions
        // even though we know we will not take any of them

        if (depth == 0 || moves.size() == 0) {
            return Evaluations.evaluateMaterial(state, state.getTurn());
        }

        // Check if is in the lookup table!

        float minScore = Float.POSITIVE_INFINITY;
        for (Action a : moves) {
            State newState = null;
            newState = TablutGame.makeMove(state, a);

            // call the other player
            float cur = maxPlayer(newState, depth - 1, alpha, beta);

            /* debug */
            if(cur > maxEval)
                maxEval = cur;
            if(cur < minEval)
                minEval = cur;
            /* debug */

            minScore = Math.min(minScore, cur);

            beta = Math.min(beta, cur);

            if (alpha <= beta) {
                break;
            }
        }
        return minScore;
    }

    public float maxPlayer(State state, int depth, float alpha, float beta) {
        List<Action> moves = availableActions(state);

        if (depth == 0 | moves.size() == 0) {
            return Evaluations.evaluateMaterial(state, state.getTurn());
        }

        float maxScore = Float.NEGATIVE_INFINITY;
        for (Action a : moves) {
            State newState = null;
            newState = TablutGame.makeMove(state, a);

            float cur = minPlayer(newState, depth - 1, alpha, beta);

            /* debug */
            if(cur > maxEval)
                maxEval = cur;
            if(cur < minEval)
                minEval = cur;
            /* debug */

            maxScore = Math.max(maxScore, cur);

            alpha = Math.max(maxScore, alpha);

            if (alpha >= beta) {
                break;
            }
        }
        return maxScore;
    }

    public List<Action> availableActions(State state) {
       // System.out.println(state);
        if (state.getTurn() == Turn.WHITEWIN || state.getTurn() == Turn.BLACKWIN || state.getTurn() == Turn.DRAW)
            return new ArrayList<Action>();
        else
            return GameHelper.availableMoves(state);
    }

    @Override
    public Boolean hasMoreTime() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hasMoreTime'");
    }
}
