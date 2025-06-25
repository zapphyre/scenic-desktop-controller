package org.remote.desktop.service.impl;

import com.arun.trie.base.Trie;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.LanguageDao;
import org.remote.desktop.db.entity.Language;
import org.remote.desktop.prediction.G4Trie;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.function.Predicate.not;
import static org.remote.desktop.util.KeyboardButtonFunctionDefinition.trieDict;

@Service
@RequiredArgsConstructor
public class TrieService {

    private final LanguageDao languageDao;

    private final Map<Long, Trie<String>> trieMap = new HashMap<>();

    public Trie<String> getTrie(Long languageId) {
        return trieMap.computeIfAbsent(languageId, l -> {
            Language byAbbreviation = languageDao.getLanguageByAbbreviation(l);
            G4Trie g4Trie = new G4Trie(trieDict);

            insert(g4Trie).accept(byAbbreviation.getTrieDump());

            return g4Trie;
        });
    }

    public Function<byte[], Trie<String>> insertReturning(Long languageId) {
        return newWords -> {
            Trie<String> trie = getTrie(languageId);

            insert(trie).accept(newWords);

            return trie;
        };
    }

    Consumer<byte[]> insert(Trie<String> trie) {
        return bytes -> {
            new String(bytes, StandardCharsets.UTF_8).lines()
                    .map(String::trim)
                    .filter(not(String::isEmpty))
                    .forEach(q -> trie.insert(q, q));
        };
    }

}
