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
public class Gesture {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ToString.Include
    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "gesture_path", joinColumns = @JoinColumn(name = "gesture_id"))
    @Column(name = "path", nullable = false)
    private List<String> paths = new ArrayList<>();
}
