package it.unibo.ai.didattica.competition.tablut.ourClient.ML;

import org.tensorflow.*;

import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

public class NNEval {

    private Session model;

    public static void main(String[] args) {
        float[] vector_state = {
                0, 0, -1, 0, 0, -1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, -1, 0, 0, 0, -1,
                0, 0, 0, -1, -1, 0, 0, -1, 2, -1, -1, 0, 0, -1, 0, 0, 0, 1, 0, 0, 0, -1, 0, 0, 0, 0, 1, 0, 0, 0, 0,
                0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0
        };

        try (Graph graph = new Graph()) {
            // byte[] graphBytes =
            // Files.readAllBytes(Paths.get("Tablut/src/it/unibo/ai/didattica/competition/tablut/ourClient/ML/model/model.pb"));
            byte[] graphBytes = Files.readAllBytes(Paths.get(
                    "Tablut/src/it/unibo/ai/didattica/competition/tablut/ourClient/ML/evaluation_model/frozen_model.pb"));
            graph.importGraphDef(graphBytes);

            try (Session session = new Session(graph)) {
                Tensor<Float> input = Tensor.create(new long[] { 1, 81 }, FloatBuffer.wrap(vector_state));
                // Tensor<Float> input = (Tensor<Float>) Tensor.create(vector_state);
                Tensor<Float> output = session.runner()
                        .feed("input_1", input)
                        .fetch("dense_2/Sigmoid")
                        .run().get(0).expect(Float.class);

                float[] outputArray = output.copyTo(new float[1][1])[0]; // Assuming the output is a 1x1 array
                float outputValue = outputArray[0]; // If the output is scalar

                // Use the output as needed
                System.out.println("Output: " + outputValue);

                input.close();
                output.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public NNEval() {
        try (Graph graph = new Graph()) {
            byte[] graphBytes = Files.readAllBytes(Paths.get(
                    "Tablut/src/it/unibo/ai/didattica/competition/tablut/ourClient/ML/evaluation_model/frozen_model.pb"));
            graph.importGraphDef(graphBytes);

            try (Session session = new Session(graph)) {
                this.model = session;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float inference(String state) {
        float[] vector_state = strToVector(state);
        float outputValue = 0;
        try {
            Tensor<Float> input = Tensor.create(new long[] { 1, 81 }, FloatBuffer.wrap(vector_state));
                Tensor<Float> output = this.model.runner()
                        .feed("input_1", input)
                        .fetch("dense_2/Sigmoid")
                        .run().get(0).expect(Float.class);

                float[] outputArray = output.copyTo(new float[1][1])[0];
                outputValue = outputArray[0];

                input.close();
                output.close();
        } catch (Exception e) {}

        return outputValue;
    }

    private float[] strToVector(String str) {
        float[] vector = new float[81];

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            if (c == 'O' || c == 'T') {
                vector[i] = 0;
            } else if (c == 'W') {
                vector[i] = 1;
            } else if (c == 'K') {
                vector[i] = 2;
            } else if (c == 'B') {
                vector[i] = -1;
            }
        }
        return vector;
    }
}
