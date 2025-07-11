package org.remote.desktop.model.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.remote.desktop.db.entity.GesturePath;

import java.util.List;

@Value
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class GestureDto {

    Long id;

    String name;
    List<GesturePath> paths;

//    GestureEventDto event;
}

