package org.remote.desktop.db.repository;

import org.remote.desktop.db.entity.GestureEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GestureEventRepository extends JpaRepository<GestureEvent, Long> {
}
