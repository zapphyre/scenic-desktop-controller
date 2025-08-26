package org.remote.desktop.ui.trigger;

import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class ColumnSelector<T extends UiSelectable<?>> extends VBox {
    private List<T> items = new ArrayList<>();
    private Function<T, String> labelExtractor;
    private List<Label> labels = new ArrayList<>();
    private BidirectionalSpliterator<T> spliterator;
    private final Border activeBorder = new Border(new BorderStroke(
            Color.rgb(255, 0, 0, 0.8),
            BorderStrokeStyle.SOLID,
            CornerRadii.EMPTY,
            new BorderWidths(2)
    ));
    private final Border inactiveBorder = new Border(new BorderStroke(
            Color.WHITE,
            BorderStrokeStyle.SOLID,
            CornerRadii.EMPTY,
            new BorderWidths(2)
    ));

    private static class BidirectionalSpliterator<T> implements Spliterator<T> {
        private final ListIterator<T> iterator;
        private final int size;
        private T currentElement;
        private boolean hasCurrent;

        public BidirectionalSpliterator(List<T> list) {
            this.iterator = list.listIterator();
            this.size = list.size();
            this.currentElement = null;
            this.hasCurrent = false;
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            if (iterator.hasNext()) {
                currentElement = iterator.next();
                hasCurrent = true;
                action.accept(currentElement);
                return true;
            }
            hasCurrent = false;
            return false;
        }

        public boolean tryReverse(Consumer<? super T> action) {
            if (iterator.hasPrevious()) {
                currentElement = iterator.previous();
                hasCurrent = true;
                action.accept(currentElement);
                return true;
            }
            hasCurrent = false;
            return false;
        }

        public T getCurrent() {
            if (hasCurrent) {
                return currentElement;
            }
            if (iterator.hasNext()) {
                currentElement = iterator.next();
                iterator.previous();
                hasCurrent = true;
                return currentElement;
            }
            return null;
        }

        @Override
        public Spliterator<T> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return size - iterator.nextIndex();
        }

        @Override
        public int characteristics() {
            return ORDERED | SIZED | SUBSIZED;
        }
    }

    public ColumnSelector() {
        setStyle("-fx-background-color: transparent;");
        setSpacing(20);
    }

    public <L extends UiSelectable<?>> void setItems(List<L> items, Function<L, String> labelExtractor) {
        this.items = new ArrayList<>();

        if (items != null) {
            this.items.addAll((Collection<? extends T>) items);
        }
        this.spliterator = new BidirectionalSpliterator<>(this.items);
        this.labels.clear();
        getChildren().clear();

        for (int i = 0; i < this.items.size(); i++) {
            T item = this.items.get(i);
            String text = this.labelExtractor.apply(item);

            Label label = new Label(text);
            label.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: limegreen; -fx-background-color: transparent;");
            label.setEffect(new Glow(0.8));

            final int index = i;
            label.setOnMouseClicked(event -> {
                while (spliterator.iterator.nextIndex() <= index && spliterator.tryAdvance(item2 -> {})) {}
                while (spliterator.iterator.nextIndex() > index + 1 && spliterator.tryReverse(item2 -> {})) {}
                updateSelection(true);
            });

            getChildren().add(label);
            labels.add(label);
        }

        if (!items.isEmpty()) {
            spliterator.tryAdvance(item -> {});
            updateSelection(true);
        }
    }

    public void selectNext() {
        spliterator.tryAdvance(item -> {});
        updateSelection(true);
    }

    public void selectPrevious() {
        spliterator.tryReverse(item -> {});
        updateSelection(true);
    }

    public T getSelected() {
        return spliterator.getCurrent();
    }

    public void setActive(boolean isActive) {
        updateSelection(isActive);
    }

    private void updateSelection(boolean isActive) {
        for (Label label : labels) {
            label.setBorder(null);
        }
        int currentIndex = spliterator.iterator.nextIndex() - 1;
        if (currentIndex >= 0 && currentIndex < labels.size()) {
            labels.get(currentIndex).setBorder(isActive ? activeBorder : inactiveBorder);
        }
    }
}