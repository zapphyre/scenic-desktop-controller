package org.remote.desktop.prediction;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextPreprocessor {
    private Map<String, Integer> wordToIndex = new HashMap<>();
    private Map<Integer, String> indexToWord = new HashMap<>();
    private List<String> tokens = new ArrayList<>();
    private int vocabSize;

    public TextPreprocessor(String text, int maxVocabSize) {
        // Simple tokenization: split on whitespace and clean
        String[] allTokensArray = text.toLowerCase()
                .replaceAll("[^a-zA-Z0-9\\s]", "") // Remove punctuation
                .split("\\s+");
        List<String> allTokens = new ArrayList<>();
        for (String token : allTokensArray) {
            if (!token.isEmpty()) {
                allTokens.add(token);
            }
        }

        // Build vocabulary
        Map<String, Integer> wordCounts = new HashMap<>();
        for (String token : allTokens) {
            wordCounts.put(token, wordCounts.getOrDefault(token, 0) + 1);
        }
        List<Map.Entry<String, Integer>> sortedWords = new ArrayList<>(wordCounts.entrySet());
        sortedWords.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        int index = 0;
        for (Map.Entry<String, Integer> entry : sortedWords) {
            if (index >= maxVocabSize - 1) break;
            wordToIndex.put(entry.getKey(), index);
            indexToWord.put(index, entry.getKey());
            index++;
        }
        wordToIndex.put("<UNK>", index);
        indexToWord.put(index, "<UNK>");
        vocabSize = index + 1;

        // Store tokens
        for (String token : allTokens) {
            tokens.add(wordToIndex.containsKey(token) ? token : "<UNK>");
        }
    }

    public List<String> getTokens() {
        return tokens;
    }

    public Map<String, Integer> getWordToIndex() {
        return wordToIndex;
    }

    public Map<Integer, String> getIndexToWord() {
        return indexToWord;
    }

    public int getVocabSize() {
        return vocabSize;
    }

    public List<int[]> createSequences(int sequenceLength) {
        List<int[]> sequences = new ArrayList<>();
        for (int i = 0; i < tokens.size() - sequenceLength; i++) {
            int[] sequence = new int[sequenceLength + 1];
            for (int j = 0; j < sequenceLength; j++) {
                sequence[j] = wordToIndex.getOrDefault(tokens.get(i + j), wordToIndex.get("<UNK>"));
            }
            sequence[sequenceLength] = wordToIndex.getOrDefault(tokens.get(i + sequenceLength), wordToIndex.get("<UNK>"));
            sequences.add(sequence);
        }
        return sequences;
    }

    public void saveVocabulary(String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(wordToIndex);
            oos.writeObject(indexToWord);
            oos.writeInt(vocabSize);
        }
    }

    public static TextPreprocessor loadVocabulary(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            Map<String, Integer> wordToIndex = (Map<String, Integer>) ois.readObject();
            Map<Integer, String> indexToWord = (Map<Integer, String>) ois.readObject();

//            MapTrie<String> trie = new MapTrie<>();

//            wordToIndex.forEach((q, _) -> trie.insert(q, q));

//            TrieIO.saveTrie(trie, "slovak.trie");
//            Trie<?> trie = TrieIO.loadTrie("slovak.trie");

            int vocabSize = ois.readInt();
            TextPreprocessor preprocessor = new TextPreprocessor("", 0);
            preprocessor.wordToIndex = wordToIndex;
            preprocessor.indexToWord = indexToWord;
            preprocessor.vocabSize = vocabSize;
            return preprocessor;
        }
    }
}