package org.remote.desktop.db.repository;

import org.remote.desktop.db.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingsRepository extends JpaRepository<Setting, Long> {
    Optional<Setting> findBySettingsInstance(String settingsInstance);
}
