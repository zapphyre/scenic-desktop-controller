package org.remote.desktop.ui.trigger;

import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@RequiredArgsConstructor
public class UiTriggerSelectorModel {

    List<TriggerSelectable> leftItems;
    List<TriggerSelectable> rightItems;

}
