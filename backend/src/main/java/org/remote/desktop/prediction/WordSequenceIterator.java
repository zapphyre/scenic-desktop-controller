package org.remote.desktop.prediction;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

import java.util.List;

public class WordSequenceIterator implements DataSetIterator {
    private List<int[]> sequences;
    private int vocabSize;
    private int batchSize;
    private int cursor = 0;

    public WordSequenceIterator(List<int[]> sequences, int vocabSize, int batchSize) {
        this.sequences = sequences;
        this.vocabSize = vocabSize;
        this.batchSize = batchSize;
    }

    @Override
    public DataSet next(int num) {
        int currentBatchSize = Math.min(num, sequences.size() - cursor);
        int sequenceLength = sequences.get(0).length - 1; // Input length (target is the last index)
        INDArray features = Nd4j.zeros(currentBatchSize, vocabSize, sequenceLength);
        INDArray labels = Nd4j.zeros(currentBatchSize, vocabSize, sequenceLength);
        INDArray featureMask = Nd4j.ones(currentBatchSize, sequenceLength); // Mask all time steps
        INDArray labelMask = Nd4j.zeros(currentBatchSize, sequenceLength); // Mask only the last step

        for (int i = 0; i < currentBatchSize; i++) {
            int[] sequence = sequences.get(cursor + i);
            // Features: one-hot encode input sequence
            for (int j = 0; j < sequenceLength; j++) {
                features.putScalar(i, sequence[j], j, 1.0);
            }
            // Labels: one-hot encode the target word at the last time step
            labels.putScalar(i, sequence[sequenceLength], sequenceLength - 1, 1.0);
            labelMask.putScalar(i, sequenceLength - 1, 1.0); // Predict only at the last step
        }

        cursor += currentBatchSize;
        DataSet dataSet = new DataSet(features, labels, featureMask, labelMask);
        return dataSet;
    }

    @Override
    public int inputColumns() {
        return vocabSize;
    }

    @Override
    public int totalOutcomes() {
        return vocabSize;
    }

    @Override
    public boolean resetSupported() {
        return true;
    }

    @Override
    public boolean asyncSupported() {
        return false;
    }

    @Override
    public void reset() {
        cursor = 0;
    }

    @Override
    public int batch() {
        return batchSize;
    }

    @Override
    public boolean hasNext() {
        return cursor < sequences.size();
    }

    @Override
    public DataSet next() {
        return next(batchSize);
    }

    @Override
    public List<String> getLabels() {
        return null;
    }

    @Override
    public void setPreProcessor(DataSetPreProcessor preProcessor) {}

    @Override
    public DataSetPreProcessor getPreProcessor() {
        return null;
    }
}