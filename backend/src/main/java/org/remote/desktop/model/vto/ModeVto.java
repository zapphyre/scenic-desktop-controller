package org.remote.desktop.model.vto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
public class ModeVto {

    Long id;

    String adapterMode;

    List<String> nouns;
    List<String> keyEvtTypes;

    boolean scenic;
}
