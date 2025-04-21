package org.remote.desktop.util;

import lombok.experimental.UtilityClass;
import org.remote.desktop.model.TrieGroupDef;
import org.remote.desktop.ui.model.EActionButton;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.remote.desktop.ui.model.EActionButton.*;

@UtilityClass
public class KeyboardLayoutTrieUtil {

    // internal trie dictionary
    public static final Map<Character, Character> trieDict;

    //set labels
    public static final Map<Integer, Map<EActionButton, TrieGroupDef>> buttonDict;

    static IdxWordTx stripLast = q ->p -> p.substring(0, p.length() - 1);

    static {
        List<TrieGroupDef> definitions = List.of(
                TrieGroupDef.builder().button(Y).trieCode('q').group(0).elements(List.of("A", "B", "C")).build(),
                TrieGroupDef.builder().button(B).trieCode('w').group(0).elements(List.of("D", "E", "F")).build(),
                TrieGroupDef.builder().button(A).trieCode('e').group(0).elements(List.of("G", "H", "I")).build(),
                TrieGroupDef.builder().button(X).trieCode('r').group(0).elements(List.of("J", "K", "L")).build(),
                TrieGroupDef.builder().button(Y).trieCode('t').group(1).elements(List.of("M", "N", "O")).build(),
                TrieGroupDef.builder().button(B).trieCode('y').group(1).elements(List.of("P", "Q", "R", "S")).build(),
                TrieGroupDef.builder().button(A).trieCode('u').group(1).elements(List.of("T", "U", "V")).build(),
                TrieGroupDef.builder().button(X).trieCode('i').group(1).elements(List.of("W", "X", "Y", "Z")).build()
        );

        LinkedList<TrieGroupDef> behavioralDefins = new LinkedList<>(definitions);
        behavioralDefins
                .add(TrieGroupDef.builder().button(X).group(2).elements(List.of("Del")).transform(stripLast).build());

        trieDict = definitions.stream()
                .flatMap(def -> def.getElements().stream()
                        .map(letter -> Map.entry(letter.toCharArray()[0], def.getTrieCode())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> {
                            throw new IllegalStateException("Duplicate key found");
                        },
                        HashMap::new
                ));

        buttonDict = behavioralDefins.stream()
                .collect(Collectors.groupingBy(
                        TrieGroupDef::getGroup,
                        Collectors.toMap(
                                TrieGroupDef::getButton,
                                Function.identity(),
                                (list1, list2) -> {
                                    throw new IllegalStateException("Duplicate code in same group");
                                }
                        )
                ));
    }

}
