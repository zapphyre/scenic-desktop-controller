package org.remote.desktop.util;

import lombok.experimental.UtilityClass;
import org.remote.desktop.model.TrieGroupDef;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class KeyboardLayoutTrieUtil {

    public static final Map<String, String> trieDict;
    public static final Map<Integer, Map<String, List<String>>> buttonDict;

    static {
        List<TrieGroupDef> definitions = List.of(
                TrieGroupDef.builder().code("Y").group(0).elements(List.of("A", "B", "C")).build(),
                TrieGroupDef.builder().code("B").group(0).elements(List.of("D", "E", "F")).build(),
                TrieGroupDef.builder().code("A").group(0).elements(List.of("G", "H", "I")).build(),
                TrieGroupDef.builder().code("X").group(0).elements(List.of("J", "K", "L")).build(),
                TrieGroupDef.builder().code("Y").group(1).elements(List.of("M", "N", "O")).build(),
                TrieGroupDef.builder().code("B").group(1).elements(List.of("P", "Q", "R", "S")).build(),
                TrieGroupDef.builder().code("A").group(1).elements(List.of("T", "U", "V")).build(),
                TrieGroupDef.builder().code("X").group(1).elements(List.of("W", "X", "Y", "Z")).build()
        );

        trieDict = definitions.stream()
                .flatMap(def -> def.getElements().stream()
                        .map(letter -> Map.entry(letter, def.getCode() + def.getGroup())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> {
                            throw new IllegalStateException("Duplicate key found");
                        },
                        HashMap::new
                ));

        buttonDict = definitions.stream()
                .collect(Collectors.groupingBy(
                        TrieGroupDef::getGroup,
                        Collectors.toMap(
                                TrieGroupDef::getCode,
                                TrieGroupDef::getElements,
                                (list1, list2) -> {
                                    throw new IllegalStateException("Duplicate code in same group");
                                }
                        )
                ));
    }

}
