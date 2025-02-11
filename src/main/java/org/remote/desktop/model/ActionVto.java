package org.remote.desktop.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.asmus.model.EButtonAxisMapping;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionVto {

    Long id;

    EButtonAxisMapping trigger;
    boolean longPress;
    Set<EButtonAxisMapping> modifiers = new HashSet<>();

    List<XdoActionVto> actions = new LinkedList<>();
}
