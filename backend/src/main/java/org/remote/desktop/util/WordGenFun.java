package org.remote.desktop.util;

import javafx.scene.control.TextField;

@FunctionalInterface
public interface WordGenFun {
    void transform(TextField word);
}
