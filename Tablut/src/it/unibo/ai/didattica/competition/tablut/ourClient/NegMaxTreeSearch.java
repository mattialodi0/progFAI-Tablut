package it.unibo.ai.didattica.competition.tablut.ourClient;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.ourClient.interfaces.TreeSearch;
import java.util.ArrayList;
import java.util.List;

public class NegMaxTreeSearch implements TreeSearch {

    private Action bestAction; // Here the best move is stored
    private final Game rules;
    private Turn t;

    public NegMaxTreeSearch(Game rules) {
        this.rules = rules;
    }

    @Override
    public Action searchTree(State state) {
        negMaxSearch(state, 4, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 1, true);
        return this.getBestAction();
    }

    // Returns the score of the root node - state. The best Action will be stored
    // in the attribute of the class. Player is always +1 at the beginning.
    // isRoot is used to check when it has sense to update the bestMove attribute so
    // it's not updated in every recursive call
    public float negMaxSearch(State state, int depth, float alpha, float beta, int player, Boolean isRoot) {

        // call to some function that checks the possible moves, if there are no
        // possible moves the array is empty, becuase it means that the game ends.
        Action[] moves = availableActions(state);

        if (depth == 0 || moves.length == 0) {
            return evaluate(state);
        }
        float score = Float.NEGATIVE_INFINITY;
        State prevNode = state.clone(); // If clone doesn't do a depp copy it's not ok when returning to prevState.
                                        // Maybe do by ourselve a deepCopy or is it to costly? Maybe better to make a
                                        // move that changes the game state back. Just invert from and to of the Action
                                        // I think it deepCopies everything apart from the Turn? I don't think this is
                                        // a problem, because availableActions is done before
        // Even if clone() works as a deep copy maybe it's not efficient. Try reversing
        // the move? But again, maybe doing the checkMove twice is even more difficult
        for (Action a : moves) {
            try {
                state = rules.checkMove(state, a);
            } catch (Exception e) {
                // TODO!
            }
            // call the other player
            float cur = -negMaxSearch(state, depth - 1, -beta, -alpha, -player, false);
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
    public float evaluate(State state) {
        if (state.getTurn().equals(Turn.DRAW)) {
            return 0;
        } else if (state.getTurn().equals(Turn.WHITEWIN) && t.equals(Turn.WHITE)) {
            return 1;
        } else if (state.getTurn().equals(Turn.WHITEWIN) && t.equals(Turn.BLACK)) {
            return -1;
        } else if (state.getTurn().equals(Turn.BLACKWIN) && t.equals(Turn.WHITE)) {
            return -1;
        } else if (state.getTurn().equals(Turn.BLACKWIN) && t.equals(Turn.BLACK)) {
            return 1;
        }
        int[] kingPos = GameHelper.getKingPosition(state);
        List<int[]> emptyTiles = GameHelper.populateEmptyList(state);
        List<int[]> escapeTiles = new ArrayList<>();

        // Common heuristics, everyone will be computed accordingly to who is calling it
        int alivePawns = Heuristics.numAlive(state);
        int eatenPawns = Heuristics.numEaten(state);
        int kingReachable = Heuristics.numberOfPawnsToReachKing(GameHelper.populatePawnList(state), emptyTiles,
                kingPos);
        float conv = Heuristics.convergenceMiddle(GameHelper.populatePawnList(state));

        // white heuristics
        if (state.getTurn().equals(State.Turn.WHITE)) {
            int escapes = HeuristicsWhite.escapesOpen(emptyTiles, kingPos);
            int directions = HeuristicsWhite.freedomOfMovement(emptyTiles, kingPos);
            // Normalize it between 0 - 1
            return directions + conv + kingReachable + escapes + alivePawns + eatenPawns;
        } else if (state.getTurn().equals(State.Turn.BLACK)) { // black heuristics
            int exitsBlocked = HeuristicsBlack.exitsBlocked(emptyTiles, escapeTiles, kingPos);
            // Normalize between 0 - 1
            return conv + kingReachable + exitsBlocked + alivePawns + eatenPawns;
        }
        return 0;
    }

    @Override
    public Boolean hasMoreTime() {
        // TODO Auto-generated method stub
        return null;
    }

    // if the Turn == WW, BW, D return an empty list. Given a state we can get the
    // Turn with getTurn()
    public Action[] availableActions(State state) {
        state.getTurn();
        return null;
    }

    public Action getBestAction() {
        return this.bestAction;
    }

}
