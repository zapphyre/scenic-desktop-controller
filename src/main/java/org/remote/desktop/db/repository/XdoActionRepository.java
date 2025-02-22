package org.remote.desktop.db.repository;

import org.remote.desktop.db.entity.XdoAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface XdoActionRepository extends JpaRepository<XdoAction, Long> {

}
