package org.remote.desktop.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XdoActionVto {
    private Long id;

    private EKeyEvt keyEvt;
    private String keyPress;

    private ActionVto action;
}
