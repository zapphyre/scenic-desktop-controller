package org.remote.desktop.db.repository;

import org.remote.desktop.db.entity.GamepadEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GPadEventRepository extends JpaRepository<GamepadEvent, Long> {

    @Query("select E from GamepadEvent E where E.modifiers is empty and E.longPress = false")
    List<GamepadEvent> findAllKeyPressOnly();

    List<GamepadEvent> findAllByLongPressTrue();

    List<GamepadEvent> findAllByModifiersNotEmpty();
}
