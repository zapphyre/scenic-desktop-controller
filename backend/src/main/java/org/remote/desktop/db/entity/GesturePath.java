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
public class GesturePath {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ToString.Include
    private Long id;

    @ToString.Include
    @EqualsAndHashCode.Include
    private String path;

    @JoinColumn
    @ManyToOne(cascade = CascadeType.DETACH)
    @EqualsAndHashCode.Include
    private Gesture gesture;

    @PreUpdate
    @PrePersist
    public void relinkEntities() {
        Optional.ofNullable(gesture)
                .ifPresent(q -> q.getPaths().add(this));
    }

    @PreRemove
    public void detachEntity() {
        Optional.ofNullable(gesture)
                .ifPresent(q -> q.getPaths().remove(this));
    }
}
