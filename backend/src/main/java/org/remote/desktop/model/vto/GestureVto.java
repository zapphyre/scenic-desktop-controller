package org.remote.desktop.model.vto;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder
@Jacksonized
public class GestureVto {

    Long id;

    String name;

    @ToString.Exclude
    List<GesturePathVto> paths;

}
