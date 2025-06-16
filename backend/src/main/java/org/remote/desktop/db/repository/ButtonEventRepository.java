package org.remote.desktop.db.repository;

import org.remote.desktop.db.entity.ButtonEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ButtonEventRepository extends JpaRepository<ButtonEvent, Long> {
}
