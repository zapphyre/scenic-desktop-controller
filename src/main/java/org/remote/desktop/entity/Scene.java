package org.remote.desktop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @OneToOne(cascade = CascadeType.ALL)
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
