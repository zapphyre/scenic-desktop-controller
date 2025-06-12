package org.remote.desktop.util;

import lombok.experimental.UtilityClass;
import org.remote.desktop.model.EKeyEvt;
import org.remote.desktop.model.ELogicalTrigger;
import org.remote.desktop.model.Segment;
import org.remote.desktop.model.Node;
import org.remote.desktop.model.dto.XdoActionDto;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@UtilityClass
public class GestureUtil {

    public static List<Segment> gestures = List.of(
            Segment.builder()
                    .triggers(List.of(ELogicalTrigger.LEFTX_DOWN, ELogicalTrigger.LEFTX_RIGHT, ELogicalTrigger.LEFTX_UP, ELogicalTrigger.LEFTX_LEFT))
                    .actions(List.of(XdoActionDto.builder()
                            .keyEvt(EKeyEvt.STROKE)
                            .keyStrokes(List.of("qwer"))
                            .build()))
                    .build(),
            Segment.builder()
                    .triggers(List.of(ELogicalTrigger.LEFTX_LEFT, ELogicalTrigger.LEFTX_CENTER, ELogicalTrigger.LEFTX_LEFT))
                    .actions(List.of(XdoActionDto.builder()
                            .keyStrokes(List.of("back"))
                            .build()))
                    .build(),
            Segment.builder()
                    .triggers(List.of(ELogicalTrigger.LEFTX_RIGHT, ELogicalTrigger.LEFTX_CENTER, ELogicalTrigger.LEFTX_RIGHT))
                    .actions(List.of(XdoActionDto.builder()
                            .keyStrokes(List.of("forward"))
                            .build()))
                    .build(),
            Segment.builder()
                    .triggers(List.of(ELogicalTrigger.LEFTX_RIGHT, ELogicalTrigger.RIGHTX_LEFT))
                    .actions(List.of(XdoActionDto.builder()
                            .keyStrokes(List.of("simulation crash"))
                            .build()))
                    .build(),
            Segment.builder()
                    .triggers(List.of(ELogicalTrigger.LEFTX_LEFT, ELogicalTrigger.RIGHTX_RIGHT))
                    .actions(List.of(XdoActionDto.builder()
                            .keyStrokes(List.of("counter"))
                            .build()))
                    .build()
    );

    public static Map<ELogicalTrigger, Node> buildNodeMap(List<Segment> gestures) {
        Map<ELogicalTrigger, Node> n = new HashMap<>();

        for (Segment gesture : gestures) {
            List<ELogicalTrigger> triggers = gesture.getTriggers();
            if (triggers.isEmpty()) continue; // Handle empty case if needed

            Iterator<ELogicalTrigger> iterator = triggers.iterator();
            ELogicalTrigger firstTrigger = iterator.next();

            // Root node: reuse if exists, create with firstTrigger if not
            Node root = n.computeIfAbsent(firstTrigger, k -> new Node(firstTrigger));
            Node current = root;

            // Build the chain
            while (iterator.hasNext()) {
                ELogicalTrigger trigger = iterator.next();
                // Reuse or create next node
                Node next = current.getConnections().computeIfAbsent(trigger, k -> new Node(trigger));
                current = next;
            }

            // Set actions on the last node
            current.setActions(gesture.getActions());
        }

        return n;
    }
}
