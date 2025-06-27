package org.remote.desktop.repository;

import jakarta.transaction.Transactional;
import org.antlr.v4.runtime.Vocabulary;
import org.junit.jupiter.api.Test;
import org.remote.desktop.db.entity.VocabularyAdjustment;
import org.remote.desktop.db.repository.VocabularyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class VocabularyRepositoryTest {

    @Autowired
    private VocabularyRepository repository;

    @Test
    void canDeleteReturning() {
        VocabularyAdjustment adjustment = VocabularyAdjustment.builder()
                .word("word")
                .frequencyAdjustment(3)
                .build();

        VocabularyAdjustment adjustment2 = VocabularyAdjustment.builder()
                .word("word")
                .frequencyAdjustment(4)
                .build();

        VocabularyAdjustment adjustment3 = VocabularyAdjustment.builder()
                .word("word")
                .frequencyAdjustment(5)
                .build();

        VocabularyAdjustment saved1 = repository.save(adjustment);
        VocabularyAdjustment saved2 = repository.save(adjustment2);
        VocabularyAdjustment saved = repository.save(adjustment3);

//        Integer vocabularyAdjustment = repository.getAllForLanguageRemoving();
        List<VocabularyAdjustment> all = repository.findAll();
    }
}
