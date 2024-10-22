package it.unibo.ai.didattica.competition.tablut.client.our;

public interface TreeSearch {
    int[] treeSearch(int[] state);
    float evaluate(int[] state);
    
    Boolean hasMoreTime();
} 