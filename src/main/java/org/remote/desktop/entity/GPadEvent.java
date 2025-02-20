package org.remote.desktop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.asmus.model.EMultiplicity;

import java.util.HashSet;
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
    private boolean longPress;

    @Enumerated(EnumType.STRING)
    private EMultiplicity multiplicity;

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

    @JoinColumn
    @ManyToOne(cascade = CascadeType.DETACH)
    private Scene scene;

    @PreUpdate
    @PrePersist
    public void relinkEntities() {
        Optional.ofNullable(actions).orElse(Set.of()).stream()
                .filter(q -> q.getGPadEvent() != null)
                .forEach(p -> p.setGPadEvent(this));

        Optional.ofNullable(scene)
                .map(Scene::getGPadEvents)
                .ifPresent(q -> q.add(this));
    }

    @PreRemove
    public void detachEntity() {
        actions.forEach(q -> q.setGPadEvent(null));
    }
}
