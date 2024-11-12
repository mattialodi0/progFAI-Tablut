package it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches;

import java.util.List;
import java.util.Random;
import java.io.IOException;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.ourClient.GameHelper;
import it.unibo.ai.didattica.competition.tablut.ourClient.evaluations.Evaluations;
import it.unibo.ai.didattica.competition.tablut.ourClient.interfaces.TreeSearch;


/* Visit only the first level of the tree */
public class BasicTreeSearch implements TreeSearch {
    private Game rules;

    public BasicTreeSearch(Game r) {
        this.rules = r;
    }

    @Override
    public Action searchTree(State state) {
        try {            
            Action best_move = null;
            float best_move_eval = -9999;
            List<Action> moves = GameHelper.availableMoves(state);


            for(Action m : moves) {
                System.out.println();
                State clone_state = state.clone();
                State s = rules.checkMove(clone_state, m);
                float e = Evaluations.evaluateMaterial(s);
                
                if(e > best_move_eval) {
                    best_move = m;
                    best_move_eval = e;
                }
            }
            
            System.out.println("Move:, "+best_move);
            System.out.println("Eval: "+best_move_eval);
            
            return best_move;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return randomMove(state);
        }
    }

    public Boolean hasMoreTime() {
        return true;
    }

    private Action randomMove(State state) {
        int[] selected = null;
		boolean found = false;
		Action a = null;
		try {
			a = new Action("z0", "z0", state.getTurn());
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		List<int[]> pawns = GameHelper.populatePawnList(state);
		List<int[]> empty = GameHelper.populateEmptyList(state);

        while (!found) {
            if (pawns.size() > 1) {
                selected = pawns.get(new Random().nextInt(pawns.size() - 1));
            } else {
                selected = pawns.get(0);
            }

            String from = state.getBox(selected[0], selected[1]);

            selected = empty.get(new Random().nextInt(empty.size() - 1));
            String to = state.getBox(selected[0], selected[1]);


            try {
                a = new Action(from, to, state.getTurn());
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            try {
                rules.checkMove(state, a);
                found = true;
            } catch (Exception e) {}

        }

        return a;
    }
}