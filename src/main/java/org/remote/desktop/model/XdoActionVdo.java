package org.remote.desktop.model;

import lombok.Data;

@Data
public class XdoActionVdo {
    Long id;

    EKeyEvt keyEvt;
    String keyPress;
}
