package org.remote.desktop.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.remote.desktop.util.IdxWordTx;

import java.util.Arrays;
import java.util.List;

@Value
public class LF {

    String label;
    IdxWordTx transform;

    public LF(String label) {
        this.label = label;
        this.transform = idx -> t -> t.appendText(label);
    }

    public LF(String label, IdxWordTx transform) {
        this.label = label;
        this.transform = transform;
    }

    public static List<LF> all(String ...labels) {
        return Arrays.stream(labels).map(LF::new).toList();
    }
}
