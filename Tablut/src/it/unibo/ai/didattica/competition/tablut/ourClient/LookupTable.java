package it.unibo.ai.didattica.competition.tablut.ourClient;

import java.util.ArrayList;
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
    public int i = 0;

    public LookupTable() {
        visited_states = new HashMap<String, Float>();
    }

    public int dim() {
        return visited_states.size();
    }

    public Float lookForVisitedState(String state_str) {
        String[] eq_state_str = equivalentStates(state_str);

        if (visited_states.containsKey(state_str))
        // if (visited_states.containsKey(eq_state_str[0]) ||
        //     visited_states.containsKey(eq_state_str[1]) ||
        //     visited_states.containsKey(eq_state_str[2]) ||
        //     visited_states.containsKey(eq_state_str[3]))
            return visited_states.get(state_str);
        else
            return null;
    }

    public void insertVisitededState(String state_str, float eval) {
        if (visited_states.size() < MAX_DIM)
            visited_states.put(state_str, new Float(eval));

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

    private String[] equivalentStates(String state) {
        String[] eq_states = new String[4];
        eq_states[0] = state;
        eq_states[1] = rotateState(eq_states[0]);
        eq_states[2] = rotateState(eq_states[1]);
        eq_states[3] = rotateState(eq_states[2]);

        return eq_states;
    }

    private String rotateState(String state) {
        // Creiamo una matrice 9x9 a partire dalla stringa
        char[][] matriceOrig = new char[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                matriceOrig[i][j] = state.charAt(i * 9 + j);
            }
        }

        // Creiamo una nuova matrice 9x9 per la rotazione
        char[][] matriceRuotata = new char[9][9];

        // Ruotiamo la matrice di 90 gradi in senso orario
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                matriceRuotata[j][8 - i] = matriceOrig[i][j];
            }
        }

        // Costruiamo la stringa risultante dalla matrice ruotata
        StringBuilder result = new StringBuilder(81);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                result.append(matriceRuotata[i][j]);
            }
        }

        return result.toString();
    }
}
