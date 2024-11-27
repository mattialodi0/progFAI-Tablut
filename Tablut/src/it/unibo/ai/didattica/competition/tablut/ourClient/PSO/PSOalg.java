package it.unibo.ai.didattica.competition.tablut.ourClient.PSO;

import java.io.FileWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.didattica.competition.tablut.ourClient.GameHelper;
import it.unibo.ai.didattica.competition.tablut.ourClient.interfaces.TreeSearch;
import it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches.MinMax;
import it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches.MinMaxLimited;
import it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches.SemiRandTreeSearch;
import it.unibo.ai.didattica.competition.tablut.simulator.TablutGame;

public class PSOalg {
    private int swarmSize;
    private int dimension;
    private int maxIterations;
    private float lowerBound;
    private float upperBound;
    private float lowerBoundVelocity;
    private float upperBoundVelocity;
    private float inertia;
    private float cognitiveCoeff;
    private float socialCoeff;
    private Particle[] swarm;
    private float[] globalBest;
    private float globalBestFitness;
    private Random random = new Random();
    private List<State> states;
    private List<Float> probabilities;

    int time = 60;
    Timer timer = new Timer(time);
    private double time_tot = 0;

    public PSOalg(int swarmSize, int dimension, int maxIterations,
            float lowerBound, float upperBound,
            float inertia, float cognitiveCoeff, float socialCoeff, float lowerBoundVelocity,
            float upperBoundVelocity) {
        this.swarmSize = swarmSize;
        this.dimension = dimension;
        this.maxIterations = maxIterations;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.inertia = inertia;
        this.cognitiveCoeff = cognitiveCoeff;
        this.socialCoeff = socialCoeff;
        this.upperBoundVelocity = upperBoundVelocity;
        this.lowerBoundVelocity = lowerBoundVelocity;

        swarm = new Particle[swarmSize];
        globalBest = new float[dimension];
        globalBestFitness = Float.NEGATIVE_INFINITY;

        List<StateWithProbability> dataset = parseJSONDataset();
        List<String> stateStrings = new ArrayList<>();
        this.probabilities = new ArrayList<>();
        for (StateWithProbability item : dataset) {
            stateStrings.add(item.getState());
            this.probabilities.add(item.getProbability());
        }
        // this.probabilities.add(probabilities);
        this.states = stringsToStates(stateStrings);

        // Initialize swarm
        for (int i = 0; i < swarmSize; i++) {
            swarm[i] = new Particle(dimension, lowerBound, upperBound);
        }
    }

    private float evaluateFitness(float[] weights) {
        double wonGames = 0;
        for (int i = 0; i < 100; i++) {
            Turn res = State.Turn.BLACKWIN;
            try {
                res = runGame(weights);
            } catch (TimeoutException e) {
                // TODO Auto-generated catch block
            }
            if (res.equals(Turn.WHITEWIN)) {
                wonGames++;
            }
        }
        return (float) wonGames / 100;
    }

    private float evaluateFitnessEff(float[] weigths) {
        List<Float> results = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            int randomIndex = random.nextInt(states.size());
            State initialState = states.get(randomIndex);
            float initProb = probabilities.get(randomIndex);
            State obtainedState = null;
            try {
                obtainedState = makeTwoMoves(initialState.clone(), weigths);
            } catch (TimeoutException e) {
                // TODO Auto-generated catch block
            }
            float prob = 0;
            if (obtainedState.getTurn().equals(Turn.WHITEWIN)) {
                prob = 1;
            } else if (obtainedState.getTurn().equals(Turn.BLACKWIN)) {
                prob = 0;
            } else {
                try {
                    prob = runRandomGames(obtainedState.clone());
                } catch (TimeoutException e) {
                    // TODO Auto-generated catch block
                }
            }
            System.out.println(prob + " - For the " + i + "iteration.");
            results.add(prob - initProb);
        }
        return (float) results.stream()
                .reduce(0f, Float::sum) / results.size();
    }

    public float[] optimize() {
        for (int iter = 0; iter < maxIterations; iter++) {
            int j = 0;
            for (Particle particle : swarm) {

                float fitness = evaluateFitness(particle.getPosition());
                particle.updatePersonalBest(fitness);

                if (fitness > globalBestFitness) {
                    globalBestFitness = fitness;
                    System.arraycopy(particle.getPersonalBest(), 0, globalBest, 0, dimension);
                }
                j++;
                String str = "";
                for (float weight : particle.getPosition()) {
                    str += weight + " ";
                }
                System.out.println("The particle " + j + " - Iteration " + iter + " - Fitness " + fitness
                        + " - Weights " + str);
            }

            // Update velocity and position
            for (Particle particle : swarm) {
                float[] newVelocity = new float[dimension];
                float[] newPosition = new float[dimension];

                for (int i = 0; i < dimension; i++) {
                    float r1 = random.nextFloat();
                    float r2 = random.nextFloat();
                    newVelocity[i] = inertia * particle.getVelocity()[i]
                            + cognitiveCoeff * r1 * (particle.getPersonalBest()[i] - particle.getPosition()[i])
                            + socialCoeff * r2 * (globalBest[i] - particle.getPosition()[i]);
                    newVelocity[i] = Math.max(lowerBoundVelocity, Math.min(upperBoundVelocity, newVelocity[i]));

                    newPosition[i] = particle.getPosition()[i] + newVelocity[i];

                    // Clamp position within bounds
                    newPosition[i] = Math.max(lowerBound, Math.min(upperBound, newPosition[i]));
                }

                particle.setVelocity(newVelocity);
                particle.updatePosition(newPosition);
            }
            String str2 = "";
            for (float weight : globalBest) {
                str2 += weight + " ";
            }
            System.out.println(
                    "Iteration " + iter + " - Best Fitness: " + globalBestFitness + " - Best weights: " + str2);
        }

        return globalBest;
    }

    private State makeTwoMoves(State state, float[] weights) throws TimeoutException {
        int i = 0;
        Action move;
        while (i < 2) {

            move = whiteMove(state.clone(), weights);

            // if (timer.timeOutOccurred()) {
            // throw new TimeoutException("The move took too long and exceeded the allowed
            // time limit.");
            // }
            // time_tot += timer.getTimer();
            // System.out.println("Time white: "+timer.getTimer());

            try {
                if (TablutGame.checkMove(state, move)) {
                    TablutGame.makeMove(state, move);
                }
            } catch (Exception e) {
                System.out.println("State when NullPointerException: " + state + "Move: " + move);
                throw e;
            }

            if (TablutGame.isGameover(state))
                break;

            // black move
            // timer.start();
            move = blackMove(state.clone());
            // if (timer.timeOutOccurred()) {
            // throw new TimeoutException("The move took too long and exceeded the allowed
            // time limit.");
            // }
            // System.out.println("Time black: "+timer.getTimer());
            try {
                if (TablutGame.checkMove(state, move)) {
                    TablutGame.makeMove(state, move);
                }
            } catch (Exception e) {
                System.out.println("State when NullPointerException in black move: " + state + "Move: " + move);
                throw e;
            }

            if (TablutGame.isGameover(state))
                break;
            i++;
        }
        return state;
    }

    private float runRandomGames(State state) throws TimeoutException {
        Action move;
        State initialState = state.clone();
        int winCounter = 0;
        for (int i = 0; i < 200; i++) {
            // game loop
            state = initialState.clone();
            int turns = 0;
            while (true) {
                if (turns > 200) {
                    break; // draw if exceed MAX_TURNS
                }

                // white move
                // timer.start();
                move = randMove(state.clone());

                // if (timer.timeOutOccurred()) {
                // throw new TimeoutException("The move took too long and exceeded the allowed
                // time limit.");
                // }
                // time_tot += timer.getTimer();
                // System.out.println("Time white: "+timer.getTimer());

                try {
                    if (TablutGame.checkMove(state, move)) {
                        TablutGame.makeMove(state, move);
                    }
                } catch (Exception e) {
                    System.out.println("State when NullPointerException: " + state + "Move: " + move);
                    throw e;
                }

                if (TablutGame.isGameover(state))
                    break;

                // black move
                // timer.start();
                move = randMove(state.clone());
                // if (timer.timeOutOccurred()) {
                // throw new TimeoutException("The move took too long and exceeded the allowed
                // time limit.");
                // }
                // System.out.println("Time black: "+timer.getTimer());
                try {
                    if (TablutGame.checkMove(state, move)) {
                        TablutGame.makeMove(state, move);
                    }
                } catch (Exception e) {
                    System.out.println("State when NullPointerException in black move: " + state + "Move: " + move);
                    throw e;
                }

                if (TablutGame.isGameover(state))
                    break;

                turns++;
            }
            if (state.getTurn().equals(Turn.WHITEWIN)) {
                winCounter++;
            }
        }
        return (float) winCounter / 200;
    }

    private Turn runGame(float[] weights) throws TimeoutException {
        State state;
        Action move;
        int turns = 0;
        // int moveCache = -1;
        // int repeated = 0;

        // state & game setup
        state = new StateTablut();
        state.setTurn(State.Turn.WHITE);

        // game loop
        while (true) {
            if (turns > 200) {
                return Turn.DRAW; // draw if exceed MAX_TURNS
            }

            // white move
            timer.start();
            move = whiteMove(state.clone(), weights);

            if (timer.timeOutOccurred()) {
                throw new TimeoutException("The move took too long and exceeded the allowed time limit.");
            }
            time_tot += timer.getTimer();
            // System.out.println("Time white: "+timer.getTimer());

            try {
                if (TablutGame.checkMove(state, move)) {
                    TablutGame.makeMove(state, move);
                }
            } catch (Exception e) {
                System.out.println("State when NullPointerException: " + state + "Move: " + move);
                throw e;
            }

            if (TablutGame.isGameover(state))
                break;

            // black move
            timer.start();
            move = blackMove(state.clone());
            if (timer.timeOutOccurred()) {
                throw new TimeoutException("The move took too long and exceeded the allowed time limit.");
            }
            // System.out.println("Time black: "+timer.getTimer());
            try {
                if (TablutGame.checkMove(state, move)) {
                    TablutGame.makeMove(state, move);
                }
            } catch (Exception e) {
                System.out.println("State when NullPointerException in black move: " + state + "Move: " + move);
                throw e;
            }

            if (TablutGame.isGameover(state))
                break;

            turns++;
        }

        // System.out.println("Endgame state \n" + state.toString());
        // System.out.println("Turns: " + turns);

        return state.getTurn();
    }

    private Action whiteMove(State state, float[] weights) {
        // TreeSearch searchStrategy = new MinMaxNoLimit(2);
        TreeSearch searchStrategy = new MinMaxParametric(1, weights);
        // TreeSearch searchStrategy = new SemiRandTreeSearch();
        return searchStrategy.searchTree(state.clone());
    }

    private Action whiteMoveSemi(State state) {
        TreeSearch searchStrategy = new SemiRandTreeSearch();
        return searchStrategy.searchTree(state.clone());
    }

    private Action blackMove(State state) {
        TreeSearch searchStrategy = new MinMax(1);
        // TreeSearch searchStrategy = new SemiRandTreeSearch();
        return searchStrategy.searchTree(state.clone());
        // return randMove(state);
    }

    private Action randMove(State state) {
        List<Action> available_actions = GameHelper.availableMoves(state);
        if (available_actions.size() == 0)
            return null;

        Random rand = new Random();
        return available_actions.get(rand.nextInt(available_actions.size()));
    }

    private static List<StateWithProbability> parseJSONDataset() {
        Gson gson = new Gson();
        String jsonOutput = "";
        try {
            jsonOutput = new String(Files.readAllBytes(Paths.get(
                    "Tablut/src/it/unibo/ai/didattica/competition/tablut/ourClient/ML/dataset/balanced_sampled_states.json")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Type listType = new TypeToken<List<StateWithProbability>>() {
        }.getType();
        List<StateWithProbability> dataset = gson.fromJson(jsonOutput, listType);
        return dataset;
    }

    private static void printJSON(List<Double> list) {
        try {
            Writer writer = new FileWriter("dataset_y.json");
            Gson gson = new GsonBuilder().create();
            gson.toJson(list, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<State> stringsToStates(List<String> dataset) {
        List<State> states = new ArrayList<>();

        State tmp_state = new StateTablut();
        Pawn[][] tmp_board = new Pawn[9][9];

        for (String s : dataset) {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    tmp_board[i][j] = Pawn.fromString(Character.toString(s.charAt(i * 9 + j)));
                }
            }
            tmp_state.setBoard(tmp_board);
            states.add(tmp_state.clone());
        }

        return states;
    }

    public static class StateWithProbability {
        private String state;
        private float probability;

        // Constructor
        public StateWithProbability(String state, float probability) {
            this.state = state;
            this.probability = probability;
        }

        // Getters and setters
        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public float getProbability() {
            return probability;
        }

        public void setProbability(float probability) {
            this.probability = probability;
        }

        @Override
        public String toString() {
            return "StateWithProbability{state='" + state + "', probability=" + probability + '}';
        }
    }

    private static class Timer {
        private long duration;
        private long startTime;

        public Timer(int maxSeconds) {
            this.duration = (long) (1000 * maxSeconds);
        }

        public void start() {
            this.startTime = System.currentTimeMillis();
        }

        public double getTimer() {
            return (double) (System.currentTimeMillis() - this.startTime) / 1000;
        }

        public boolean timeOutOccurred() {
            boolean overTime = System.currentTimeMillis() > this.startTime + this.duration;
            return overTime;
        }
    }
}
