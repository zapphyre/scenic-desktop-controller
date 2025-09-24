package org.remote.desktop.db.repository;

import org.remote.desktop.db.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findEventByNextSceneId(Long nextSceneId);
}
