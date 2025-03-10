package org.remote.desktop.db.entity;

import jakarta.persistence.*;
import lombok.*;
import org.remote.desktop.model.EAxisEvent;
import org.remote.desktop.model.GamepadEventContainer;
import org.remote.desktop.model.dto.GamepadEventDto;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Scene implements GamepadEventContainer<GamepadEvent, Scene> {

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    private String name;
    private String windowName;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private Scene inherits;

    @OneToMany(mappedBy = "scene", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, orphanRemoval = true)
    private List<GamepadEvent> gamepadEvents = new LinkedList<>();

    @Enumerated(EnumType.STRING)
    private EAxisEvent leftAxisEvent = EAxisEvent.MOUSE;

    @Enumerated(EnumType.STRING)
    private EAxisEvent rightAxisEvent = EAxisEvent.SCROLL;

    public EAxisEvent getLeftAxisEvent() {
        return Optional.ofNullable(leftAxisEvent).orElse(EAxisEvent.MOUSE);
    }

    public EAxisEvent getRightAxisEvent() {
        return Optional.ofNullable(rightAxisEvent).orElse(EAxisEvent.SCROLL);
    }

    @PreUpdate
    @PrePersist
    public void relinkEntities() {
        Optional.ofNullable(gamepadEvents)
                .ifPresent(q -> q.forEach(p -> p.setScene(this)));
    }
}
