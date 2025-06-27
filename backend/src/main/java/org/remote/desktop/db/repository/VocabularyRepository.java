package org.remote.desktop.db.repository;

import org.remote.desktop.db.entity.VocabularyAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VocabularyRepository extends JpaRepository<VocabularyAdjustment, Long> {

    Optional<VocabularyAdjustment> findByLanguageIdAndWord(Long languageId, String word);

    List<VocabularyAdjustment> findByLanguageId(Long languageId);
}
