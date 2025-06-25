package org.remote.desktop.db.entity;

import jakarta.persistence.*;
import lombok.*;

@With
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String code;

    private String name;

    private int size;

    @Lob
    private byte[] trieDump;
}
