package org.remote.desktop.prediction;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class NextWordTrainer {
    public static String readText(InputStream inputStream) throws Exception {
        byte[] bytes = inputStream.readAllBytes();
        return new String(bytes, StandardCharsets.UTF_8).trim();
    }

    public static void main(String[] args) throws Exception {
        // Read text from InputStream
        String txtFilePath = "lajftxt.txt";
        try (InputStream inputStream = new FileInputStream(txtFilePath)) {
            String text = readText(inputStream);
            if (text.isEmpty()) {
                System.err.println("Error: Text is empty.");
                return;
            }

            // Preprocess text
            int maxVocabSize = 20000;
            int sequenceLength = 6;
            TextPreprocessor preprocessor = new TextPreprocessor(text, maxVocabSize);
            preprocessor.saveVocabulary("vocab2.dat"); // Save vocab for prediction
            List<int[]> sequences = preprocessor.createSequences(sequenceLength);
            if (sequences.isEmpty()) {
                System.err.println("Error: No sequences generated.");
                return;
            }

            // Create iterator
            int batchSize = 32;
            int vocabSize = preprocessor.getVocabSize();
            WordSequenceIterator iterator = new WordSequenceIterator(sequences, vocabSize, batchSize);

            // Define model
            int hiddenSize = 200;
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                    .updater(new Adam(0.001))
                    .list()
                    .layer(new LSTM.Builder()
                            .nIn(vocabSize)
                            .nOut(hiddenSize)
                            .activation(Activation.TANH)
                            .build())
                    .layer(new RnnOutputLayer.Builder()
                            .nIn(hiddenSize)
                            .nOut(vocabSize)
                            .activation(Activation.SOFTMAX)
                            .lossFunction(LossFunctions.LossFunction.MCXENT)
                            .build())
                    .build();

            MultiLayerNetwork model = new MultiLayerNetwork(conf);
            model.init();
            model.setListeners(new ScoreIterationListener(100));

            // Train
            int epochs = 10;
            for (int i = 0; i < epochs; i++) {
                model.fit(iterator);
                iterator.reset();
                System.out.println("Epoch " + (i + 1) + " complete");
            }

            // Save model
            model.save(new File("next_word_model2.zip"));
        }
    }

    static void saveVocabulary(String filename) throws IOException {


    }
}
