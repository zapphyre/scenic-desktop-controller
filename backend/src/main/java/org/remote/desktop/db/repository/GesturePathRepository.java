package org.remote.desktop.db.repository;

import org.remote.desktop.db.entity.GesturePath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GesturePathRepository extends JpaRepository<GesturePath, Long> {
}
