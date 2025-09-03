package org.remote.desktop.db.repository;

import org.remote.desktop.db.entity.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface XdoActionRepository extends JpaRepository<Action, Long> {

}
