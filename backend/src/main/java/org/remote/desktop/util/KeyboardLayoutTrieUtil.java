package org.remote.desktop.util;

import lombok.experimental.UtilityClass;
import org.remote.desktop.model.LF;
import org.remote.desktop.model.TrieGroupDef;
import org.remote.desktop.ui.model.EActionButton;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.remote.desktop.model.LF.all;
import static org.remote.desktop.ui.model.EActionButton.*;
import static org.remote.desktop.util.TextFieldTransformations.*;

@UtilityClass
public class KeyboardLayoutTrieUtil {

    // internal trie dictionary
    public static final Map<Character, Character> trieDict;
    public static int FUNCTION_GROUP_IDX = 3;
    public static int PUNCTUATION_GROUP_IDX = 1;

    //set labels
    public static final Map<Integer, Map<EActionButton, TrieGroupDef>> buttonDict;

    static {
        List<TrieGroupDef> definitions = List.of(
                TrieGroupDef.builder().button(Y).trieCode('q').group(0).elements(all("A", "B", "C")).build(),
                TrieGroupDef.builder().button(B).trieCode('w').group(0).elements(all("D", "E", "F")).build(),
                TrieGroupDef.builder().button(A).trieCode('e').group(0).elements(all("G", "H", "I")).build(),
                TrieGroupDef.builder().button(X).trieCode('r').group(0).elements(all("J", "K", "L")).build(),

                TrieGroupDef.builder().button(Y).trieCode('t').group(2).elements(all("M", "N", "O")).build(),
                TrieGroupDef.builder().button(B).trieCode('y').group(2).elements(all("P", "Q", "R", "S")).build(),
                TrieGroupDef.builder().button(A).trieCode('u').group(2).elements(all("T", "U", "V")).build(),
                TrieGroupDef.builder().button(X).trieCode('i').group(2).elements(all("W", "X", "Y", "Z")).build()
        );

        LinkedList<TrieGroupDef> behavioralDefins = new LinkedList<>(definitions);
        behavioralDefins.add(TrieGroupDef.builder()
                .button(X).group(FUNCTION_GROUP_IDX).elements(List.of(new LF("🠐", deleteOn))).build());
        behavioralDefins.add(TrieGroupDef.builder()
                .button(B).group(FUNCTION_GROUP_IDX).elements(List.of(new LF("⮁", toggleCase))).build());
        behavioralDefins.add(TrieGroupDef.builder()
                .button(A).group(FUNCTION_GROUP_IDX).elements(List.of(new LF("⬳", alternateCase))).build());
        behavioralDefins.add(TrieGroupDef.builder()
                .button(Y).group(FUNCTION_GROUP_IDX).elements(List.of(new LF("ᴁ", camelize))).build());

        behavioralDefins.add(TrieGroupDef.builder()
                .button(A).group(PUNCTUATION_GROUP_IDX).elements(List.of(new LF(","))).build());
        behavioralDefins.add(TrieGroupDef.builder()
                .button(B).group(PUNCTUATION_GROUP_IDX).elements(List.of(new LF("."))).build());
        behavioralDefins.add(TrieGroupDef.builder()
                .button(X).group(PUNCTUATION_GROUP_IDX).elements(List.of(new LF(";"))).build());
        behavioralDefins.add(TrieGroupDef.builder()
                .button(Y).group(PUNCTUATION_GROUP_IDX).elements(all("?", "!")).build());

        trieDict = definitions.stream()
                .flatMap(def -> def.getElements().stream()
                        .map(l -> Map.entry(l.getLabel().charAt(0), def.getTrieCode())))
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
