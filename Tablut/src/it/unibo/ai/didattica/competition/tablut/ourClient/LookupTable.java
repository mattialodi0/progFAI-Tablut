package it.unibo.ai.didattica.competition.tablut.ourClient;

import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

/* 
 * board can be rotated but the eval stays the same,
 * might be worthy to check this
 * 
 * make it a cache (more access -> no deletion)
 */

public class LookupTable {
    private int MAX_DIM = 1000;
    private HashMap<String, Float> visited_states;
    public int i=0;

    public LookupTable() {
        visited_states = new HashMap<String, Float>();
    }

    public int dim() {
        return visited_states.size();
    }

    public Float lookForVisitedState(String state_str) {
        if (visited_states.containsKey(state_str))
            return visited_states.get(state_str);
        else
            return null;
    }

    public void insertVisitededState(String state_str, float eval) {
        if (visited_states.size() < MAX_DIM)
            visited_states.put(state_str, (float)(eval));

        if (this.dim() >= MAX_DIM) {
            Random r = new Random();
            if (r.nextInt(10000) == 0) {
                Optional<String> firstKey = visited_states.keySet().stream().findFirst();
                if (firstKey.isPresent()) {
                    String key = firstKey.get();
                    visited_states.remove(key);
                    i++;
                }
            }
        }
    }
}
