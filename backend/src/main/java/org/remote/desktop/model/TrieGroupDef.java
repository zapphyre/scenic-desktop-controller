package org.remote.desktop.model;

import lombok.Builder;
import lombok.Value;
import org.remote.desktop.ui.model.EActionButton;

import java.util.List;

@Value
@Builder
public class TrieGroupDef {
    int group;
    EActionButton button;
    char trieCode;

    @Builder.Default
    List<LF> elements = List.of();
}
