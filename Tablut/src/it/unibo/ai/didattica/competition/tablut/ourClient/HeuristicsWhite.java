package it.unibo.ai.didattica.competition.tablut.ourClient;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import java.util.List;


// Make a Heuristics class that will have numEaten -> checks the state.turn and if its opponent counts;
//Will have also numAlive, numberOfPawnsToReachKing, ConvergenceMiddle
public class HeuristicsWhite {

    // quanti bianchi possono affiancare il re?
    public static int numberOfPawnsToReachKing(List<int[]> whitePawns, int[] kingPosition) {
        return 0;
    }

    public static float convergenceMiddle(List<int[]> blackPawns) {
        return 0;
    }

    // how many escape tiles accessible?
    public static int escapesOpen(List<int[]> escapeTiles, int[] kingPosition) {
        return 0;
    }

    public static int numAlive(State state) {
        return state.getNumberOf(Pawn.WHITE);
    }

    public static int numEaten(State state) {
        return state.getNumberOf(Pawn.BLACK) - 16;
    }
}
