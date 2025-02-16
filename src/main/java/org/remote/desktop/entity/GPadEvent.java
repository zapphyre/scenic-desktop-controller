package org.remote.desktop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Optional;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class GPadEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    @EqualsAndHashCode.Include
    private Long id;

    @ToString.Include
    @EqualsAndHashCode.Include
    private String trigger;

    @ToString.Include
    @EqualsAndHashCode.Include
    private Boolean longPress;

    // has to me many-one otherwise hibernate creates unique constrain on this column
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private Scene nextScene;

    @ToString.Include
    @EqualsAndHashCode.Include
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "modifier", joinColumns = @JoinColumn(name = "modifier_id"))
    private Set<String> modifiers;

    @OneToMany(mappedBy = "gPadEvent", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, orphanRemoval = true)
    private Set<XdoAction> actions;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn
    private Scene scene;

    @PreUpdate
    @PrePersist
    public void relinkEntities() {
        Optional.ofNullable(actions)
                .ifPresent(q -> q.forEach(p -> p.setGPadEvent(this)));

        Optional.ofNullable(scene)
                .ifPresent(q -> q.getGPadEvents().add(this));
    }

    @PreRemove
    public void detachEntity() {
        Optional.ofNullable(actions)
                .orElse(Set.of())
                .forEach(pos -> pos.setGPadEvent(null));
    }
}
