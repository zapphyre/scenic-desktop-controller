package org.remote.desktop.component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.source.ConnectableSource;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SourceManager {

    @Getter
    private final List<ConnectableSource> connectableSources;
    private final List<ConnectableSource> connected = new LinkedList<>();

    public boolean toggleSourceConnection(ConnectableSource connectableSource) {
        if (connected.contains(connectableSource) && connectableSource.disconnect())
            return connected.remove(connectableSource);

        if (connectableSource.connect())
            return connected.add(connectableSource);

        return false;
    }

    public boolean isConnected(ConnectableSource connectableSource) {
        return connected.contains(connectableSource);
    }
}
