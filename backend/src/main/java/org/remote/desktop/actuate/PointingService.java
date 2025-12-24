package org.remote.desktop.actuate;

import org.asmus.model.PolarCoords;

public interface PointingService {

    void paste();

    void click();

    void moveMouse(PolarCoords polarCoords);

    void scrollWithStick(PolarCoords coords);
}
