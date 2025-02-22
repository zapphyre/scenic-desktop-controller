package org.remote.desktop.repository;

import org.remote.desktop.entity.Scene;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SceneRepository extends JpaRepository<Scene, String> {

    @Query("SELECT S FROM Scene S WHERE :windowname LIKE CONCAT('%', S.windowName, '%') AND S.windowName <> ''")
    Optional<Scene> findBySceneContain(@Param("windowname") String windowname);
}
