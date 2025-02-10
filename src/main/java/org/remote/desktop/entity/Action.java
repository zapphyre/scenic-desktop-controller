package org.remote.desktop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String trigger;
    Boolean longPress;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "modifier", joinColumns = @JoinColumn(name = "modifier_id"))
    Set<String> modifiers;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    List<XdoAction> actions;
}
