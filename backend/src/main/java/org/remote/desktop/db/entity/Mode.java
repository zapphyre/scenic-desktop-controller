package org.remote.desktop.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@With
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Mode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ToString.Include
    @EqualsAndHashCode.Exclude
    private Long id;

    private String adapterMode;

    private List<String> keyEvtTypes;

    private List<String> nouns;

    private Boolean scenic;
}