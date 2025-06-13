package org.remote.desktop.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ToString.Include
    @EqualsAndHashCode.Include
    private Long id;

    @JoinColumn
    @ManyToOne(cascade = CascadeType.ALL)
    private GestureEvent gestureEvent;

    @JoinColumn
    @ManyToOne(cascade = CascadeType.ALL)
    private ButtonEvent buttonEvent;

    @JoinColumn
    @ManyToOne(cascade = CascadeType.DETACH)
    private Scene scene;

    // has to be many-one otherwise hibernate creates unique constrain on this column
    @ManyToOne(cascade = {CascadeType.DETACH})
    private Scene nextScene;

    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER, orphanRemoval = true, cascade = {CascadeType.DETACH, CascadeType.REMOVE})
    private List<XdoAction> actions = new ArrayList<>();

    @PreUpdate
    @PrePersist
    public void relinkEntities() {
        Optional.ofNullable(actions).orElse(List.of())
                .forEach(p -> p.setEvent(this));

        Optional.ofNullable(scene)
                .map(Scene::getEvents)
                .ifPresent(q -> q.add(this));

        Optional.ofNullable(buttonEvent)
                .ifPresent(p -> p.setEvent(this));

        Optional.ofNullable(gestureEvent)
                .ifPresent(p -> p.setEvent(this));
    }

    @PreRemove
    public void detachEntity() {
        scene.getEvents().remove(this);
        Optional.ofNullable(actions).orElse(List.of()).forEach(q -> q.setEvent(null));
        Optional.ofNullable(gestureEvent).ifPresent(q -> q.setEvent(null));
        Optional.ofNullable(buttonEvent).ifPresent(q -> q.setEvent(null));
    }
}
