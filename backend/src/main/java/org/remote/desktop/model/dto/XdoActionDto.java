package org.remote.desktop.model.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.remote.desktop.model.EKeyEvt;

import java.util.List;

@Value
@Builder
@Jacksonized
@RequiredArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class XdoActionDto {
    Long id;
    EKeyEvt keyEvt;

    List<String> keyStrokes;

    String activator;

    @ToString.Exclude
    EventDto event;
}
