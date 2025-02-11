package org.remote.desktop.model;

import lombok.Data;

@Data
public class XdoActionVto {
    Long id;

    EKeyEvt keyEvt;
    String keyPress;
}
