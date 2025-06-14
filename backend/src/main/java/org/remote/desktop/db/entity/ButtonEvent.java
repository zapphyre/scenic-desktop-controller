package org.remote.desktop.db.entity;

import jakarta.persistence.*;
import lombok.*;
import org.asmus.model.EMultiplicity;

import java.util.List;
import java.util.Optional;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ButtonEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @ToString.Include
    @EqualsAndHashCode.Include
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "button_modifier", joinColumns = @JoinColumn(name = "button_id"))
    private List<String> modifiers;

    @ManyToOne(fetch = FetchType.EAGER)
    private Event event;

    @PreUpdate
    @PrePersist
    public synchronized void relinkEntities() {
        Optional.ofNullable(event)
                .ifPresent(q -> q.setButtonEvent(this));
    }

    @PreRemove
    public void detachEntity() {
        Optional.ofNullable(event).ifPresent(q -> q.setButtonEvent(null));
    }
}
