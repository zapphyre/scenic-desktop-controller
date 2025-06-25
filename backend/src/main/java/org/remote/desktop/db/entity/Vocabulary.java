package org.remote.desktop.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Optional;

@Data
//@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Vocabulary {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ToString.Include
    private Long id;

    private String word;

    private int frequency;

    @ManyToOne
    @JoinColumn
    private Language language;

    @PreUpdate
    @PrePersist
    public void relinkEntities() {
//        Optional.ofNullable(language)
//                .map(Language::getVocabulary)
//                .ifPresent(q -> q.add(this));
    }
}
