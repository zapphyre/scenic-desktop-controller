package org.remote.desktop.db.repository;

import org.remote.desktop.db.entity.Mode;
import org.remote.desktop.model.EAdapterMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModeRepository extends JpaRepository<Mode, Long> {

    Mode findByAdapterMode(EAdapterMode adapterMode);
}
