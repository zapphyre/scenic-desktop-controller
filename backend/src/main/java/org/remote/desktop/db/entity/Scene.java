package org.remote.desktop.db.entity;

import jakarta.persistence.*;
import lombok.*;
import org.remote.desktop.model.EAxisEaser;
import org.remote.desktop.model.EAxisEvent;
import org.remote.desktop.model.ETriggerEvent;
import org.remote.desktop.model.GamepadEventContainer;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Data
@With
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Scene implements GamepadEventContainer<Event, Scene>, Serializable {

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
    private Set<Scene> inheritsFrom; // Set b/c for some reason (prolly mapstruct context) i had duplicate objects in gamepadEvent/nextScene

    @OneToMany(mappedBy = "scene", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Event> events = new LinkedList<>();

    @JoinColumn
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH})
    private Mode mode;

    @Enumerated(EnumType.STRING)
    private EAxisEvent leftAxisEvent;
    @Enumerated(EnumType.STRING)
    private EAxisEaser leftAxisEaser;

    @Enumerated(EnumType.STRING)
    private EAxisEvent rightAxisEvent;
    @Enumerated(EnumType.STRING)
    private EAxisEaser rightAxisEaser;

    @Enumerated(EnumType.STRING)
    private EAxisEaser rightTriggerEaser;
    @Enumerated(EnumType.STRING)
    private ETriggerEvent rightTriggerEvent;

    @Enumerated(EnumType.STRING)
    private EAxisEaser leftTriggerEaser;
    @Enumerated(EnumType.STRING)
    private ETriggerEvent leftTriggerEvent;

    @PreUpdate
    @PrePersist
    public void relinkEntities() {
        Optional.ofNullable(events)
                .ifPresent(q -> q.forEach(p -> p.setScene(this)));
    }

    @PreRemove
    public void preremove() {
        Optional.ofNullable(events).orElseGet(LinkedList::new).forEach(p -> p.setScene(null));
    }
}
