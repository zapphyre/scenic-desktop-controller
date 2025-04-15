package org.remote.desktop.config;

import com.arun.trie.base.Trie;
import com.arun.trie.io.TrieIO;
import org.remote.desktop.conditional.ConditionalFilePresence;
import org.remote.desktop.prediction.G4Trie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

import static org.remote.desktop.util.KeyboardLayoutTrieUtil.trieDict;

@Configuration
public class TrieConfig {

    private final String vocabulary = "vocab2.dat";
    private static final String TRIE_SAVE_FILENAME = "slovak.trie";

    @Bean
    @ConditionalFilePresence(TRIE_SAVE_FILENAME)
    public Trie<String> init() throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(vocabulary))) {
            Map<String, Integer> wordToIndex = (Map<String, Integer>) ois.readObject();

            G4Trie trie = new G4Trie(trieDict);
            wordToIndex.forEach((q, _) -> trie.insert(q, q));

            TrieIO.saveTrie(trie, TRIE_SAVE_FILENAME);

            return trie;
        }
    }

    @Bean
    @ConditionalFilePresence(value = TRIE_SAVE_FILENAME, present = true)
    public Trie<String> read() throws IOException, ClassNotFoundException {
        return TrieIO.loadTrie(TRIE_SAVE_FILENAME);
    }
}
