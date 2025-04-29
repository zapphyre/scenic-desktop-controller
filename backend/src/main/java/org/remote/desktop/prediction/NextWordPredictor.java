//package org.remote.desktop.prediction;
//
//import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
//import org.nd4j.linalg.api.ndarray.INDArray;
//import org.nd4j.linalg.factory.Nd4j;
//import org.nd4j.linalg.indexing.NDArrayIndex;
//
//import java.io.File;
//import java.util.Arrays;
//import java.util.List;
//
//public class NextWordPredictor {
//
//    public static void main(String[] args) throws Exception {
//        // Load model
//        MultiLayerNetwork model = MultiLayerNetwork.load(new File("next_word_model2.zip"), true);
//
//        // Load vocabulary
//        TextPreprocessor preprocessor = TextPreprocessor.loadVocabulary("vocab2.dat");
//        int sequenceLength = 4;
//        int vocabSize = preprocessor.getVocabSize();
//
//        // Prepare input
//        String inputText = "projekt";
//        List<String> inputTokens = Arrays.asList(inputText.toLowerCase().split("\\s+"));
//        INDArray input = Nd4j.zeros(1, vocabSize, sequenceLength);
//        for (int i = 0; i < Math.min(inputTokens.size(), sequenceLength); i++) {
//            String token = inputTokens.get(i);
//            int index = preprocessor.getWordToIndex().getOrDefault(token, preprocessor.getWordToIndex().get("<UNK>"));
//            input.putScalar(0, index, i, 1.0);
//        }
//
//        // Predict
//        INDArray output = model.output(input);
//        // Get the last time step's prediction
//        INDArray lastTimeStep = output.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(sequenceLength - 1));
//        int predictedIndex = Nd4j.argMax(lastTimeStep, 1).getInt(0);
//        String nextWord = preprocessor.getIndexToWord().get(predictedIndex);
//        System.out.println("Input: " + inputText);
//        System.out.println("Predicted next word: " + nextWord);
//    }
//}
