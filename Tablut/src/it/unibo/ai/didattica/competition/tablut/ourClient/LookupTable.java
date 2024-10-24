package it.unibo.ai.didattica.competition.tablut.ourClient;

import java.util.HashMap;

public class LookupTable {
    private int MAX_DIM = 10000;
    private HashMap<String, Float> visited_states;

    public LookupTable() {
        visited_states = new HashMap<String, Float>();
        visited_states = new HashMap<String, Float>();
    }

    public Float lookForVisitedState(String state_str) {
        if(visited_states.containsKey(state_str))
            return visited_states.get(state_str);
        else return null;
    }

    public void insertVisitededState(String state_str, float eval) {
        if(visited_states.size() < MAX_DIM)
            visited_states.put(state_str, new Float(eval));
    }
}
