package org.remote.desktop.model.vto;

import lombok.Builder;
import lombok.Data;
import org.remote.desktop.source.ConnectableSource;

@Data
@Builder
public class SourceStateVto {
    private boolean available;
    private String sourceName;
    private ConnectableSource source;
    private boolean connected;
}
