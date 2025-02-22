package org.remote.desktop.db.repository;

import org.remote.desktop.db.entity.GPadEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GPadEventRepository extends JpaRepository<GPadEvent, Long> {

    @Query("select E from GPadEvent E where E.modifiers is empty and E.longPress = false")
    List<GPadEvent> findAllKeyPressOnly();

    List<GPadEvent> findAllByLongPressTrue();

    List<GPadEvent> findAllByModifiersNotEmpty();
}
