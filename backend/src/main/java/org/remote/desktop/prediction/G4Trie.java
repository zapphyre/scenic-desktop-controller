package org.remote.desktop.prediction;

import com.arun.trie.VariTrie;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Map;

@Value
@RequiredArgsConstructor
public class G4Trie extends VariTrie<String> {

    Map<Character, Character> vocabulary;

    @Override
    protected char getTrieCodeForChar(char character) {
        return vocabulary.get(character);
    }
}
