package org.remote.desktop.prediction;

import com.arun.trie.VariTrie;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Map;

@Value
@RequiredArgsConstructor
public class G4Trie extends VariTrie<String> {

    Map<Character, Character> vocabulary;

    public boolean canTranslate(char c) {
        boolean can = vocabulary.containsKey(Character.toUpperCase(c));

        if (!can)
            System.out.println("Can't translate: " + c);

        return can;
    }

    @Override
    protected char getTrieCodeForChar(char character) {
        return vocabulary.get(Character.toUpperCase(character));
    }
}
