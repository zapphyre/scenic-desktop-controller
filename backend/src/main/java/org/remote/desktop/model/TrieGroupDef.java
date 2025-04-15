package org.remote.desktop.model;

import lombok.Builder;
import lombok.Value;
import org.remote.desktop.ui.model.EActionButton;

import java.util.List;
import java.util.Objects;

@Value
@Builder
public class TrieGroupDef {
    int group;
    EActionButton button;
    char trieCode;
    List<String> elements;

}
