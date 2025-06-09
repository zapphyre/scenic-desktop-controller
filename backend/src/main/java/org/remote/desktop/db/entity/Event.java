package org.remote.desktop.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ToString.Include
    @EqualsAndHashCode.Include
    private Long id;

    @JoinColumn
    @ManyToOne(cascade = CascadeType.ALL)
    private GestureEvent gestureEvent;

    @JoinColumn
    @ManyToOne(cascade = CascadeType.ALL)
    private ButtonEvent buttonEvent;

    @JoinColumn
    @ManyToOne(cascade = CascadeType.DETACH)
    private Scene scene;

    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER, orphanRemoval = true, cascade = {CascadeType.DETACH, CascadeType.REMOVE})
    private List<XdoAction> actions = new ArrayList<>();
}
