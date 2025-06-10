package org.remote.desktop.db.entity;

import jakarta.persistence.*;
import lombok.*;
import org.remote.desktop.model.EKeyEvt;

import java.util.List;
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

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Event event;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "key_stroked", joinColumns = @JoinColumn(name = "xdo_action_id"))
    @Column(name = "key_stroke", nullable = false)
    private List<String> keyStrokes;

    @PreUpdate
    @PrePersist
    public void relinkEntities() {
        // produces concurrent modification exception idk
//        Optional.ofNullable(event)
//                .map(Event::getActions)
//                .ifPresent(q -> q.add(this));
    }

    @PreRemove
    public void detachEntity() {
        Optional.ofNullable(event)
                .map(Event::getActions)
                .ifPresent(p -> p.remove(this));
    }
}
