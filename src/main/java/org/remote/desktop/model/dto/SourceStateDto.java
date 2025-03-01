package org.remote.desktop.model.dto;

import lombok.Builder;
import lombok.Data;
import org.remote.desktop.source.ConnectableSource;

@Data
@Builder
public class SourceStateDto {
    private boolean available;
    private String sourceName;
    private ConnectableSource source;
    private boolean connected;
}
