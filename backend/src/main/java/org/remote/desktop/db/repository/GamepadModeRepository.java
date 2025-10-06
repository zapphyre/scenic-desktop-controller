package org.remote.desktop.db.repository;

import org.remote.desktop.db.entity.GamepadMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GamepadModeRepository extends JpaRepository<GamepadMode, Long> {

    Optional<GamepadMode> findByDeviceName(String deviceName);
}
