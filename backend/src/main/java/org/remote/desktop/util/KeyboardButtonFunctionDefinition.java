package org.remote.desktop.util;

import lombok.experimental.UtilityClass;
import org.remote.desktop.model.*;
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
public class KeyboardButtonFunctionDefinition {

    // internal trie dictionary
    public static final Map<Character, Character> trieDict;
    public static int FUNCTION_GROUP_IDX = 3;
    public static int PUNCTUATION_GROUP_IDX = 1;

    // set labels
    public static final Map<Integer, Map<EActionButton, UiButtonBase>> buttonDict;

    static {
        List<TrieButtonTouch> trieDefs = List.of(
                TrieButtonTouch.builder().button(Y).trieCode('q').group(0).lettersOnButton(all("A", "B", "C")).build(),
                TrieButtonTouch.builder().button(B).trieCode('w').group(0).lettersOnButton(all("D", "E", "F")).build(),
                TrieButtonTouch.builder().button(A).trieCode('e').group(0).lettersOnButton(all("G", "H", "I")).build(),
                TrieButtonTouch.builder().button(X).trieCode('r').group(0).lettersOnButton(all("J", "K", "L")).build(),

                TrieButtonTouch.builder().button(Y).trieCode('t').group(2).lettersOnButton(all("M", "N", "O")).build(),
                TrieButtonTouch.builder().button(B).trieCode('y').group(2).lettersOnButton(all("P", "Q", "R", "S")).build(),
                TrieButtonTouch.builder().button(A).trieCode('u').group(2).lettersOnButton(all("T", "U", "V")).build(),
                TrieButtonTouch.builder().button(X).trieCode('i').group(2).lettersOnButton(all("W", "X", "Y", "Z")).build()
        );

        LinkedList<UiButtonBase> behavioralDefins = new LinkedList<>(trieDefs);
        behavioralDefins.add(FunctionalButtonTouch.builder()
                .button(X).group(FUNCTION_GROUP_IDX).transform(deleteOn).lettersOnButton(all("🠐")).build());
        behavioralDefins.add(FunctionalButtonTouch.builder()
                .button(B).group(FUNCTION_GROUP_IDX).transform(toggleCase).lettersOnButton(all("⮁")).build());
        behavioralDefins.add(FunctionalButtonTouch.builder()
                .button(A).group(FUNCTION_GROUP_IDX).transform(alternateCase).lettersOnButton(all("⬳")).build());
        behavioralDefins.add(FunctionalButtonTouch.builder()
                .button(Y).group(FUNCTION_GROUP_IDX).transform(camelize).lettersOnButton(all("ᴁ")).build());

        behavioralDefins.add(CharAddButtonTouch.builder()
                .button(A).group(PUNCTUATION_GROUP_IDX).lettersOnButton(all(",")).build());
        behavioralDefins.add(CharAddButtonTouch.builder()
                .button(B).group(PUNCTUATION_GROUP_IDX).lettersOnButton(all(".")).build());
        behavioralDefins.add(CharAddButtonTouch.builder()
                .button(X).group(PUNCTUATION_GROUP_IDX).lettersOnButton(all(";")).build());
        behavioralDefins.add(CharAddButtonTouch.builder()
                .button(Y).group(PUNCTUATION_GROUP_IDX).lettersOnButton(all("?", "!", "~")).build());

        trieDict = trieDefs.stream()
                .flatMap(def -> def.getLettersOnButton().stream()
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
                        UiButtonBase::getGroup,
                        Collectors.toMap(
                                UiButtonBase::getButton,
                                Function.identity(),
                                (list1, list2) -> {
                                    throw new IllegalStateException("Duplicate code in same group");
                                }
                        )
                ));
    }

}
