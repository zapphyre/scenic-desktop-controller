package org.remote.desktop.db.entity;

import jakarta.persistence.*;
import lombok.*;
import org.remote.desktop.model.EAxisEvent;
import org.remote.desktop.model.GamepadEventContainer;
import org.remote.desktop.model.dto.GamepadEventDto;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Scene implements GamepadEventContainer<GamepadEvent, Scene>, Serializable {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @EqualsAndHashCode.Include
    @ToString.Include
    private String name;
    private String windowName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "scene_inherits_from",
            joinColumns = @JoinColumn(name = "scene_id"),
            inverseJoinColumns = @JoinColumn(name = "parent_scene_id")
    )
    private List<Scene> inheritsFrom;

    @OneToMany(mappedBy = "scene", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<GamepadEvent> gamepadEvents = new LinkedList<>();

    @Enumerated(EnumType.STRING)
    private EAxisEvent leftAxisEvent = EAxisEvent.DEFAULT;

    @Enumerated(EnumType.STRING)
    private EAxisEvent rightAxisEvent = EAxisEvent.DEFAULT;

    @PreUpdate
    @PrePersist
    public void relinkEntities() {
        Optional.ofNullable(gamepadEvents)
                .ifPresent(q -> q.forEach(p -> p.setScene(this)));
    }

    @PreRemove
    public void preremove() {
        gamepadEvents.forEach(p -> p.setScene(null));
    }
}
