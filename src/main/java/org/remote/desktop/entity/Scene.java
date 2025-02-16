package org.remote.desktop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Optional;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Scene {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    private String name;
    private String windowName;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private Scene inherits;

    @OneToMany(mappedBy = "scene", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Action> actions;

    @PreUpdate
    @PrePersist
    public void relinkEntities() {
        Optional.ofNullable(actions)
                .ifPresent(q -> q.forEach(p -> p.setScene(this)));
    }
}
