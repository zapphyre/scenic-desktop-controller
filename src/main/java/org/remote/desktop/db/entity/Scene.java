package org.remote.desktop.db.entity;

import jakarta.persistence.*;
import lombok.*;

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
public class Scene {

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    private String name;
    private String windowName;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private Scene inherits;

    @OneToMany(mappedBy = "scene", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, orphanRemoval = true)
    private List<GPadEvent> gPadEvents = new LinkedList<>();

    @PreUpdate
    @PrePersist
    public void relinkEntities() {
        Optional.ofNullable(gPadEvents)
                .ifPresent(q -> q.forEach(p -> p.setScene(this)));
    }
}
