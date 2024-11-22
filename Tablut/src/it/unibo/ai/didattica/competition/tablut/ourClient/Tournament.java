package it.unibo.ai.didattica.competition.tablut.ourClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.unibo.ai.didattica.competition.tablut.ourClient.evaluations.HeuristicsWhite;
import it.unibo.ai.didattica.competition.tablut.ourClient.interfaces.TreeSearch;
import it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches.MinMax;

public class Tournament {

    public static void main(String[] args) {

        List<Float[]> blackWeights = new ArrayList<Float[]>();
        for (int i = 0; i < 16; i++) {
            Float[] black = new Float[5];

            Random random = new Random();

            // Fill the array with random float numbers
            for (int j = 0; j < 5; j++) {
                black[j] = 50 * random.nextFloat(); // Generates a random float between 0.0 and 1.0
            }
            blackWeights.add(black);
        }

        List<Float[]> whiteWeights = new ArrayList<Float[]>();
        for (int i = 0; i < 16; i++) {
            Float[] white = new Float[4];

            Random random = new Random();

            // Fill the array with random float numbers
            for (int j = 0; j < 4; j++) {
                white[j] = 50 * random.nextFloat(); // Generates a random float between 0.0 and 1.0
            }
            whiteWeights.add(white);
        }

        for (Float[] weightWhite : whiteWeights) {
            HeuristicsWhite hw = new HeuristicsWhite(weightWhite);
            for (Float[] weightBlack : blackWeights) {
                
            }
        }
    }

}
