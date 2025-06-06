package org.remote.desktop.model;

import lombok.Value;
import org.remote.desktop.util.WordGenFun;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Value
public class LF {

    String label;
    WordGenFun transform; //not being used currently; might come later might not (different textField addition function per button char)

    public LF(String label) {
        this.label = label;
        this.transform = t -> Optional.of(label)
                .map(String::toLowerCase)
                .ifPresent(t::appendText);
    }

    public LF(String label, WordGenFun transform) {
        this.label = label;
        this.transform = transform;
    }

    public static List<LF> all(String ...labels) {
        return Arrays.stream(labels).map(LF::new).toList();
    }
}
