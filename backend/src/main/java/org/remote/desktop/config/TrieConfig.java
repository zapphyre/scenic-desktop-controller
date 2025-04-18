package org.remote.desktop.config;

import com.arun.trie.base.Trie;
import com.arun.trie.base.ValueFrequency;
import com.arun.trie.io.TrieIO;
import lombok.extern.slf4j.Slf4j;
import org.remote.desktop.prediction.G4Trie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.remote.desktop.util.KeyboardLayoutTrieUtil.trieDict;

@Slf4j
@Configuration
public class TrieConfig {

    private final String vocabulary = "words.lajf";
    private static final String TRIE_SAVE_FILENAME = "slovak.trie";

    @Bean
    public Trie init() throws Exception {
        if (Files.exists(Paths.get(TRIE_SAVE_FILENAME))) {
            Trie<String> stringTrie = TrieIO.loadTrie(TRIE_SAVE_FILENAME);
            return stringTrie;
        }

        G4Trie trie = new G4Trie(trieDict);
        readWordsFromFile(vocabulary).forEach((q) -> trie.insert(q, q));

//            trie.purgeBranchOfKeyQtty(1);
//        List<ValueFrequency<String>> q = trie.getValueFreqSuggestions("q");
        TrieIO.saveTrie(trie, TRIE_SAVE_FILENAME);

        return trie;
    }

    public static List<String> readWordsFromFile(String filePath) throws Exception {
        try {
            log.info("Reading words from file: {}", filePath);
            List<String> words = Files.readAllLines(Paths.get(filePath))
                    .stream()
                    .filter(line -> !line.trim().isEmpty()) // Skip empty lines
                    .toList();

            if (words.isEmpty()) {
                log.warn("No words found in file: {}", filePath);
            } else {
                log.info("Successfully read {} words from file: {}", words.size(), filePath);
            }
            return List.copyOf(words); // Return immutable list
        } catch (Exception e) {
            log.error("Error reading words from file {}: {}", filePath, e.getMessage(), e);
            throw new Exception("Failed to read words from file: " + filePath, e);
        }
    }
}
