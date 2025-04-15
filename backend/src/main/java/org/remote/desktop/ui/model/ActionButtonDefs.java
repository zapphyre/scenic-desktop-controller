package org.remote.desktop.ui.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ActionButtonDefs {
    ButtonsSettings y;
    ButtonsSettings b;
    ButtonsSettings a;
    ButtonsSettings x;
}
