package org.remote.desktop.db.entity;

import jakarta.persistence.*;
import lombok.*;
import org.remote.desktop.model.EKeyEvt;

import java.util.Optional;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XdoAction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EKeyEvt keyEvt;

    private String keyPress;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private GamepadEvent gamepadEvent;

    @PreUpdate
    @PrePersist
    public void relinkEntities() {
        Optional.ofNullable(gamepadEvent)
                .map(GamepadEvent::getActions)
                .ifPresent(q -> q.add(this));
    }

    @PreRemove
    public void detachEntity() {
        Optional.ofNullable(gamepadEvent)
                .map(GamepadEvent::getActions)
                .ifPresent(p -> p.remove(this));
    }
}
