package org.remote.desktop.ui.trigger;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.remote.desktop.model.EAxisEaser;
import org.remote.desktop.model.EAxisEvent;

@Value
@RequiredArgsConstructor
public class EaserSelectable implements UiSelectable<EAxisEaser> {

    EAxisEaser  easer;

    @Override
    public EAxisEaser getSectableItem() {
        return easer;
    }

    @Override
    public String getItemName() {
        return easer.name();
    }
}
