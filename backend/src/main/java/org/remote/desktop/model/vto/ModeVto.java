package org.remote.desktop.model.vto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ModeVto {

    Long id;

    String adapterMode;
}
