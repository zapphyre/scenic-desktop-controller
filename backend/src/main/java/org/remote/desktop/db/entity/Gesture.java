package org.remote.desktop.db.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Gesture {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ToString.Include
    private Long id;

    @ToString.Include
    @EqualsAndHashCode.Include
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "gesture", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    @EqualsAndHashCode.Include
    private List<GesturePath> paths = new ArrayList<>();

    @PreUpdate
    @PrePersist
    public void relinkEntities() {
        Optional.ofNullable(paths).orElseGet(Collections::emptyList)
                .forEach(q -> q.setGesture(this));
    }

    @PreRemove
    public void detachEntity() {
        Optional.ofNullable(paths).orElseGet(Collections::emptyList)
                .forEach(path -> path.setGesture(null));
    }
}
