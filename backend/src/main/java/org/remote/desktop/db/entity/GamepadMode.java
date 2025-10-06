package org.remote.desktop.db.entity;

import jakarta.persistence.*;
import lombok.*;

@With
@Data
@Entity
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class GamepadMode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private Gamepad device;

    private String mode;
}
