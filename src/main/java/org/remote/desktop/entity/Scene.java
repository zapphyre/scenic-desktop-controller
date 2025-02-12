package org.remote.desktop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Scene {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String windowName;

    @ManyToOne
//    @JoinColumn(name = "inherits_scene_id", nullable = true, unique = false)
//    @OneToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    private Scene inherits;

//    @OneToMany(mappedBy = "scene", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Action> actions;

//    @PreUpdate
//    @PrePersist
//    void relinkEntities() {
//        actions.forEach(q -> q.setScene(this));
//    }
}
