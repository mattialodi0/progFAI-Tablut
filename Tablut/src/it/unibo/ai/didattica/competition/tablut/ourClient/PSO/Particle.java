package it.unibo.ai.didattica.competition.tablut.ourClient.PSO;

import java.util.Random;

public class Particle {
    private float[] position;
    private float[] velocity;
    private float[] personalBest;
    private float personalBestFitness;
    private Random random = new Random();

    public Particle(int dimension, float lowerBound, float upperBound) {
        position = new float[dimension];
        velocity = new float[dimension];
        personalBest = new float[dimension];

        for (int i = 0; i < dimension; i++) {
            position[i] = lowerBound + (upperBound - lowerBound) * random.nextFloat();
            velocity[i] = (upperBound - lowerBound) * (random.nextFloat() - 0.5f);
            personalBest[i] = position[i];
        }
        personalBestFitness = Float.NEGATIVE_INFINITY;
    }

    public float[] getPosition() { return position; }
    public float[] getVelocity() { return velocity; }
    public float[] getPersonalBest() { return personalBest; }
    public float getPersonalBestFitness() { return personalBestFitness; }

    public void setVelocity(float[] velocity) { this.velocity = velocity; }
    public void updatePersonalBest(float fitness) {
        if (fitness > personalBestFitness) {
            personalBestFitness = fitness;
            System.arraycopy(position, 0, personalBest, 0, position.length);
        }
    }
    public void updatePosition(float[] newPosition) {
        System.arraycopy(newPosition, 0, position, 0, position.length);
    }
}
