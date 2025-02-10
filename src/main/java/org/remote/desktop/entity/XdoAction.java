package org.remote.desktop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.remote.desktop.model.EKeyEvt;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XdoAction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EKeyEvt keyEvt;

    private String keyPress;
}
