package org.remote.desktop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.remote.desktop.model.EKeyEvt;

import java.util.Optional;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class XdoAction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ToString.Include
    @EqualsAndHashCode.Include
    private Long id;

    @ToString.Include
    @EqualsAndHashCode.Include
    @Enumerated(EnumType.STRING)
    private EKeyEvt keyEvt;

    @ToString.Include
    @EqualsAndHashCode.Include
    private String keyPress;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn
    private GPadEvent gPadEvent;

    @PreUpdate
    @PrePersist
    public void relinkEntities() {
        Optional.ofNullable(gPadEvent)
                .map(GPadEvent::getActions)
                .ifPresent(q -> q.add(this));
    }

    @PreRemove
    public void detachEntity() {
        Optional.ofNullable(gPadEvent)
                .ifPresent(p -> p.getActions().remove(this));
    }
}
