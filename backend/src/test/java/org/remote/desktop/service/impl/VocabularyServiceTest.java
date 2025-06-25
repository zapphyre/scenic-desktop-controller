package org.remote.desktop.service.impl;

import com.arun.trie.base.Trie;
import com.arun.trie.io.TrieIO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.remote.desktop.db.dao.LanguageDao;
import org.remote.desktop.db.entity.Language;
import org.remote.desktop.db.entity.Vocabulary;
import org.remote.desktop.db.repository.VocabularyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.io.*;
import java.util.List;

import static org.remote.desktop.config.TrieConfig.TRIE_SAVE_FILENAME;

@DataJpaTest
@Import({LanguageService.class, LanguageDao.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class VocabularyServiceTest {

    @Autowired
    private LanguageService vocabularyService;

    @Autowired
    private LanguageDao languageDao;

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Test
    void testAddAndIncrementWordFrequency() {
        long language = vocabularyService.createLanguage("en", "English");

        LanguageService.WordNote saver = vocabularyService.addToVocabulary("en");

        saver.createOrProp("word");
        saver.createOrProp("word");
        saver.createOrProp("needs");
        saver.createOrProp("needs");

        List<Vocabulary> all = vocabularyRepository.findAll();

        Assertions.assertEquals(2, all.size());
    }

    @Test
    void trieDbDump() throws IOException, ClassNotFoundException {

        Trie<String> stringTrie = TrieIO.loadTrie(TRIE_SAVE_FILENAME);
        byte[] bytes = serializeObject(stringTrie);
//        TrieIO.saveTrie(new G4Trie(trieDict), "test.trie");

        Language lang = Language.builder()
                .code("sk")
                .name("dump")
                .trieDump(bytes)
                .build();

        languageDao.saveLanguage(lang);

        Language byAbbreviation = languageDao.getLanguageByAbbreviation(lang.getAbbreviation());

        Trie<String> deserialized = (Trie<String>) deserializeObject(bytes);
        List<String> qrw = deserialized.getValueSuggestions("qrw");
    }

    public static byte[] serializeObject(Object obj) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj); // Serialize the object
            oos.flush(); // Ensure all data is written
            return bos.toByteArray(); // Get the byte array
        }
    }

    public static Object deserializeObject(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return ois.readObject(); // Deserialize the object
        }
    }
}
