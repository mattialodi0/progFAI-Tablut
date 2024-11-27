package it.unibo.ai.didattica.competition.tablut.ourClient.PSO;

public class Main {
    public static void main(String[] args) {
        int swarmSize = 10;
        int dimension = 4; // Number of weights to optimize
        int maxIterations = 100;
        float lowerBound = -1.0f; // Lower bound for weights
        float upperBound = 3.0f; // Upper bound for weights
        float lowerBoundVelocity = -1f;
        float upperBoundVelocity = 1f;
        float inertia = 0.7f;
        float cognitiveCoeff = 1.5f;
        float socialCoeff = 1.5f;

        PSOalg pso = new PSOalg(swarmSize, dimension, maxIterations, lowerBound, upperBound, inertia, cognitiveCoeff,
                socialCoeff, lowerBoundVelocity, upperBoundVelocity);
        float[] bestWeights = pso.optimize();

        System.out.println("Optimized Weights:");
        for (float weight : bestWeights) {
            System.out.printf("%.4f ", weight);
        }
    }
}
