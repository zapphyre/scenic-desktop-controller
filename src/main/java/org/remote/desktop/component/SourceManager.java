package org.remote.desktop.component;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.source.ConnectableSource;
import org.remote.desktop.source.impl.LocalSource;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SourceManager {

    @Getter
    private final List<ConnectableSource> connectableSources;
    private final List<ConnectableSource> connected = new LinkedList<>();

    private final LocalSource localSource;

    @PostConstruct
    void init() {
        toggleSourceConnection(localSource);
    }

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
