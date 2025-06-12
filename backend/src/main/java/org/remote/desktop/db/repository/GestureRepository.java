package org.remote.desktop.db.repository;

import org.remote.desktop.db.entity.Gesture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GestureRepository extends JpaRepository<Gesture, Long> {
}
