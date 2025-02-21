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
@ToString
@EqualsAndHashCode
public class XdoAction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EKeyEvt keyEvt;

    private String keyPress;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
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
                .map(GPadEvent::getActions)
                .ifPresent(p -> p.remove(this));
    }
}
