package org.remote.desktop.db.repository;

import org.remote.desktop.db.entity.Scene;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SceneRepository extends JpaRepository<Scene, Long> {

    List<Scene> findAllByMode_AdapterMode(String mode);

    @Query("SELECT S FROM Scene S WHERE :windowname LIKE CONCAT('%', S.windowName, '%') AND S.windowName <> '' AND S.mode.adapterMode = :mode")
    List<Scene> findBySceneContain(@Param("windowname") String windowname, @Param("mode") String mode);

    Optional<Scene> findByName(String name);
}
