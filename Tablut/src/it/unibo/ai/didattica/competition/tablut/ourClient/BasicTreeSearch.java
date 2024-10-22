package it.unibo.ai.didattica.competition.tablut.ourClient;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.ourClient.interfaces.GameState;
import it.unibo.ai.didattica.competition.tablut.ourClient.interfaces.TreeSearch;

/* Visit only the first level of the tree */
public class BasicTreeSearch implements TreeSearch {
    private Turn playerColor;

    public BasicTreeSearch(Turn t) {
        this.playerColor = t;
    }
    
    public Action searchTree(State state) {
        Action best_move = randomMove(state);
        // float best_move_eval = -9999;
        // Action[] moves = GameState.availableMoves(state);

        // moves.forEach(m -> {
        //     State s = GameState.makeMove(state, m);
        //     float e = evaluate(s);

        //     if(e > best_move_eval) {
        //         best_move = m;
        //         best_move_eval = e;
        //     }
        // })

        return best_move;
    }

    /* Basic heuristic, normalized between [-1, +1], more pieces -> more points  */
    public float evaluate(State state) {
        float eval = 0;

        if(playerColor == Turn.WHITE) {
            eval = state.getNumberOf(Pawn.WHITE)*2 - state.getNumberOf(Pawn.BLACK);
        }
        else {
            eval = state.getNumberOf(Pawn.BLACK) - state.getNumberOf(Pawn.WHITE)*2;
        }

        return eval / 16;
    }
    
    public Boolean hasMoreTime() {
        return true;
    }


    private Action randomMove(State state) {
        // TODO: copy random player code
        return null;
    }
}