package org.remote.desktop.db.entity;

import jakarta.persistence.*;
import lombok.*;
import org.remote.desktop.model.EAdapterMode;
import org.remote.desktop.model.modul.GpadOsActionModule;

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

    @Enumerated(EnumType.STRING)
    private String adapterMode;

}