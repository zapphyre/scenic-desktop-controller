package org.remote.desktop.repository;

import org.remote.desktop.entity.XdoAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface XdoActionRepository extends JpaRepository<XdoAction, Long> {

}
