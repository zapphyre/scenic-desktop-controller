package org.remote.desktop.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Optional;

@With
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode
public class GestureEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ToString.Include
    private Long id;

    @JoinColumn
    @ManyToOne(fetch = FetchType.EAGER)
    private Gesture leftStickGesture;

    @JoinColumn
    @ManyToOne(fetch = FetchType.EAGER)
    private Gesture rightStickGesture;

    @ManyToOne(fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    private Event event;

    @PreUpdate
    @PrePersist
    public void relinkEntities() {
        Optional.ofNullable(event)
                .ifPresent(q -> q.setGestureEvent(this));
    }

    @PreRemove
    public void detachEntity() {
        Optional.ofNullable(event).ifPresent(q -> q.setGestureEvent(null));
    }
}
