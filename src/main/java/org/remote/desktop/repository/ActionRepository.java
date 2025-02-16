package org.remote.desktop.repository;

import org.remote.desktop.entity.GPadEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionRepository extends JpaRepository<GPadEvent, Long> {
}
