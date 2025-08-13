package org.remote.desktop.ui.game.snake;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode
public class Position {
    int x;
    int y;

    public void decrementX() {
        x -= 1;
    }

    public void incrementX() {
        x += 1;
    }
    public void decrementY() {
        y -= 1;
    }
    public void incrementY() {
        y += 1;
    }
}
