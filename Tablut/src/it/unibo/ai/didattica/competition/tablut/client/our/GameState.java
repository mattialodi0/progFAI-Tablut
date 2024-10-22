package it.unibo.ai.didattica.competition.tablut.client.our;

public interface GameState {
    void makeMove(int[] state, int[] move);
    void backtrack(int[] state, int[] move);
    Boolean win(int[] state);
}
