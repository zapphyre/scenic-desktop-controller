package org.remote.desktop.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@With
@Data
@Entity
@Builder(toBuilder = true)
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

    @Singular("vocabulary")
    @OneToMany(mappedBy = "language", orphanRemoval = true,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    private List<VocabularyAdjustment> vocabularyAdjustments;

    @PreUpdate
    @PrePersist
    public void relinkEntities() {
        Optional.ofNullable(vocabularyAdjustments)
                .orElseGet(ArrayList::new)
                .forEach(q -> q.setLanguage(this));
    }
}
