package org.remote.desktop.mode.modul;

import lombok.RequiredArgsConstructor;
import org.desktop.remote.mode.GpadOsActionModule;
import org.remote.desktop.service.impl.StateService;
import org.remote.desktop.service.impl.XdoSceneService;
import org.remote.desktop.ui.CircleButtonsInputWidget;
import org.remote.desktop.ui.model.EKeyboardInputButton;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.Set;

import static org.remote.desktop.actuate.MouseAct.paste;

//@Component
@RequiredArgsConstructor
public class KeyboardModule implements GpadOsActionModule {

    private final CircleButtonsInputWidget widget;
    private final StateService stateService;

    @Override
    public String getName() {
        return "KEYBOARD";
    }

    @Override
    public List<String> getNouns() {
        return List.of("A", "X", "Y", "B");
    }

    @Override
    public List<String> getVerbs() {
        return List.of("SELECT_BOTTOM", "SELECT_TOP", "CURSOR_LEFT",
                "CURSOR_RIGHT", "CURSOR_WORD_LEFT", "CURSOR_WORD_RIGHT",
                "RESET_STATE", "ADD_WORD", "NEXT_PREDICTION_FRAME", "PREV_PREDICTION_FRAME",
                "END", "PASTE", "A", "X", "Y", "B");
    }

    @Override
    public boolean isScenic() {
        return false;
    }

    @Override
    public boolean handleEvent(String s, List<String> list) {
        return switch (s) {
            case "MODE_SELECT" -> {
                widget.render();
                yield  true;
            }
            case "SELECT_BOTTOM" -> {
                widget.selectBottomRow();
                yield true;
            }
            case "SELECT_TOP" -> {
                widget.selectTopRow();
                yield true;
            }
            case "CURSOR_LEFT" -> {
                widget.moveCursorLeft();
                yield true;
            }
            case "CURSOR_RIGHT" -> {
                widget.moveCursorRight();
                yield true;
            }
            case "CURSOR_WORD_LEFT" -> {
                widget.moveCursorWordLeft();
                yield true;
            }
            case "CURSOR_WORD_RIGHT" -> {
                widget.moveCursorWordRight();
                yield true;
            }
            case "RESET_STATE" -> {
                widget.resetStateClean();
                yield true;
            }
            case "ADD_WORD" -> {
                widget.addWordToSentence();
                yield true;
            }
            case "NEXT_PREDICTION_FRAME" -> {
                widget.nextPredictionsFrame();
                yield true;
            }
            case "PREV_PREDICTION_FRAME" -> {
                widget.prevPredictionsFrame();
                yield true;
            }
            case "END" -> {
                copyToClipboard(widget.getSentenceAndReset());
                stateService.recognizeScene();
                yield true;
            }
            case "PASTE" -> {
                paste();
                stateService.defaultMode();
                yield true;
            }
            case "A", "X", "Y", "B" -> {
                widget.setActiveAndType(EKeyboardInputButton.valueOf(s), Set.of());
                yield true;
            }
            default -> false;
        };
    }

    @Override
    public boolean activate() {
        widget.render();
        return true;
    }

    public static void copyToClipboard(String text) {
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
}
