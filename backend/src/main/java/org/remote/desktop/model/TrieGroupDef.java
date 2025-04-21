package org.remote.desktop.model;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import org.remote.desktop.ui.model.EActionButton;
import org.remote.desktop.util.IdxWordTx;
import org.remote.desktop.util.LetterIdxGetter;
import org.remote.desktop.util.WordGenFun;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Value
@Builder
public class TrieGroupDef {
    int group;
    EActionButton button;
    char trieCode;

    @Builder.Default
    List<String> elements = List.of();

//    @Builder.Default
    LetterIdxGetter letterIdxGetter = q -> getElements().get(q);

    @Builder.Default
    IdxWordTx transform; //= idx ->word -> word + getElements().get(idx);

    public IdxWordTx getTransfFx() {
        return i -> w -> transform == null ?
                w + getElements().get(i) : transform.transforIdxWord(i).transform(w);
    }
}
