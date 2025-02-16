package org.remote.desktop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Include
    private String trigger;
    @ToString.Include
    private Boolean longPress;

    // has to me many-one otherwise hibernate creates unique constrain on this column
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private Scene nextScene;

    @ToString.Include
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "modifier", joinColumns = @JoinColumn(name = "modifier_id"))
    private Set<String> modifiers;

    @OneToMany(mappedBy = "action", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    List<XdoAction> actions;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn
    private Scene scene;

    @PreUpdate
    @PrePersist
    public void relinkEntities() {
        Optional.ofNullable(actions)
                .ifPresent(q -> q.forEach(p -> p.setAction(this)));
    }

    @PreRemove
    public void detachEntity() {
        Optional.ofNullable(actions)
                .orElse(List.of())
                .forEach(pos -> pos.setAction(null));
    }
}
