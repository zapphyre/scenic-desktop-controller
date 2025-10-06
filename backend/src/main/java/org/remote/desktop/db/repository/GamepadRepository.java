package org.remote.desktop.db.repository;

import org.remote.desktop.db.entity.Gamepad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GamepadRepository extends JpaRepository<Gamepad, Long> {
}
