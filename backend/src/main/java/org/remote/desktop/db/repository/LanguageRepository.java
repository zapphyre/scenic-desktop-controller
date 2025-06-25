package org.remote.desktop.db.repository;

import org.remote.desktop.db.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {

    Language findByCode(String code);
}
