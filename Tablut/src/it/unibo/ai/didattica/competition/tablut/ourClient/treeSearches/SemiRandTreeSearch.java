package it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches;

import java.util.List;
import java.util.Random;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.ourClient.GameHelper;
import it.unibo.ai.didattica.competition.tablut.ourClient.interfaces.TreeSearch;
import it.unibo.ai.didattica.competition.tablut.simulator.TablutGame;


public class SemiRandTreeSearch implements TreeSearch {
    Turn player;

    @Override
    public Action searchTree(State state) {
        this.player = state.getTurn();
        Action bestAction = null;
        State saved_state = state.clone();
        float score = 0;
        List<Action> moves = GameHelper.availableMoves(state);

        if (moves.size() == 0)
            System.out.println("big prolbem...");

        score = Float.NEGATIVE_INFINITY;
        for (Action m : moves) {
            state = TablutGame.makeMove(state, m);

            float cur = Eval(state, true);
            if (cur >= score) {
                score = cur;
                bestAction = m;
            }
            state = saved_state.clone();
        }
        if (score < Float.POSITIVE_INFINITY) {
            for (Action m : moves) {
                state = TablutGame.makeMove(state, m);

                float cur = EnemyMove(state);
                if (cur >= score) {
                    score = cur;
                    bestAction = m;
                }
                state = saved_state.clone();
            }
        }

        return bestAction;
    }

    @Override
    public Boolean hasMoreTime() {
        return true;
    }

    private float Eval(State state, Boolean isPlayer) {
        if (isPlayer) {
            if ((state.getTurn().equals(Turn.WHITEWIN) && this.player.equals(Turn.WHITE))
                    || (state.getTurn().equals(Turn.BLACKWIN) && this.player.equals(Turn.BLACK))) {
                return Float.POSITIVE_INFINITY;
            } else if ((state.getTurn().equals(Turn.WHITEWIN) && this.player.equals(Turn.BLACK))
                    || (state.getTurn().equals(Turn.BLACKWIN) && this.player.equals(Turn.WHITE))) {
                return Float.NEGATIVE_INFINITY;
            } else {
                Random random = new Random();
                return (random.nextFloat() - 0.5f);
            }
        } else {
            if ((state.getTurn().equals(Turn.WHITEWIN) && this.player.equals(Turn.WHITE))
                    || (state.getTurn().equals(Turn.BLACKWIN) && this.player.equals(Turn.BLACK))) {
                return Float.NEGATIVE_INFINITY;
            } else if ((state.getTurn().equals(Turn.WHITEWIN) && this.player.equals(Turn.BLACK))
                    || (state.getTurn().equals(Turn.BLACKWIN) && this.player.equals(Turn.WHITE))) {
                return Float.POSITIVE_INFINITY;
            } else {
                Random random = new Random();
                return (random.nextFloat() - 0.5f);
            }
        }
    }

    private float EnemyMove(State state) {
        List<Action> moves = GameHelper.availableMoves(state);
            if (moves.size() == 0) {
                return 0;
            }

            State saved_state = state.clone();
            List<Action> moves_evals = moves;

            float min_score = Float.POSITIVE_INFINITY;
            for (Action m : moves_evals) {
                state = TablutGame.makeMove(state, m);
                float cur = Eval(state, true);
                min_score = Math.min(min_score, cur);
                state = saved_state.clone();
            }

            return min_score;
    }
}
