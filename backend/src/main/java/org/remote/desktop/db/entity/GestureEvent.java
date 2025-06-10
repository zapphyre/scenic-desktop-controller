package org.remote.desktop.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Optional;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class GestureEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ToString.Include
    @EqualsAndHashCode.Include
    private Long id;

    @JoinColumn
    @ManyToOne(fetch = FetchType.EAGER)
    private Gesture leftStickGesture;

    @JoinColumn
    @ManyToOne(fetch = FetchType.EAGER)
    private Gesture rightStickGesture;

    @ManyToOne(fetch = FetchType.EAGER)
    private Event event;

    @PreUpdate
    @PrePersist
    public void relinkEntities() {
        Optional.ofNullable(event)
                .ifPresent(q -> q.setGestureEvent(this));
    }

    @PreRemove
    public void detachEntity() {
        Optional.ofNullable(event).ifPresent(q -> q.setScene(null));
    }
}
