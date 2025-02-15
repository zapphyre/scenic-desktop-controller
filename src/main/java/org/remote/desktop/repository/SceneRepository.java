package org.remote.desktop.repository;

import org.remote.desktop.entity.Scene;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SceneRepository extends JpaRepository<Scene, String> {
}
