package org.remote.desktop.model;

import lombok.*;
import lombok.experimental.NonFinal;
import org.remote.desktop.model.dto.XdoActionDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Value
@RequiredArgsConstructor
public class Node {
    ELogicalTrigger trigger; // Current node's trigger value
    @Setter
    @NonFinal List<XdoActionDto> actions; // Actions (only for the last node)
    Map<ELogicalTrigger, Node> connections = new HashMap<>(); // Connections keyed by ELogicalTrigger

    public void addConnection(ELogicalTrigger nextTrigger, Node nextNode) {
        connections.put(nextTrigger, nextNode); // Key is the next node's trigger
    }

    public Node getNext(ELogicalTrigger key) {
        return connections.get(key);
    }
}

