package org.remote.desktop.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Optional;

@With
@Data
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class VocabularyAdjustment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @EqualsAndHashCode.Include
    private String word;

    @EqualsAndHashCode.Include
    private Integer frequencyAdjustment;

    @ManyToOne
    @JoinColumn
    @ToString.Exclude
    private Language language;

    @PreUpdate
    @PrePersist
    public void relinkEntities() {
        Optional.ofNullable(language)
                .map(Language::getVocabularyAdjustments)
                .ifPresent(q -> q.add(this));
    }
}
