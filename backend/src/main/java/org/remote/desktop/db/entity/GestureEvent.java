package org.remote.desktop.db.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class GestureEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ToString.Include
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Event event;
}
