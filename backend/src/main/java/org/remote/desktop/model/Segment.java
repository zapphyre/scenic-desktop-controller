package org.remote.desktop.model;

import lombok.Builder;
import lombok.Value;
import org.remote.desktop.model.dto.XdoActionDto;

import java.util.List;

@Value
@Builder
public class Segment {
    List<ELogicalTrigger> triggers;
    List<XdoActionDto> actions;
}
