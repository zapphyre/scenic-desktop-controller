package org.remote.desktop.ui.game.snake;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;

import static org.remote.desktop.ui.game.snake.SnakeGame.TILE_SIZE;

public  class SnakePart {
    Position position;
    Direction direction;

    SnakePart(Position position, Direction direction) {
        this.position = position;
        this.direction = direction;
    }

    boolean collidesWith(Position other) {
        return this.position.equals(other);
    }

    private Polygon createBaseBodySegment(double size, double offset, boolean mirrored) {
        Polygon base = new Polygon();
        if (!mirrored) {
            base.getPoints().addAll(
                    0.0, offset,                    // Top-left
                    size, 0.0,                      // Top-right
                    size, size - offset,            // Bottom-right
                    0.0, size                      // Bottom-left
            );
        } else {
            base.getPoints().addAll(
                    0.0, size - offset,             // Bottom-left
                    size, size,                     // Bottom-right
                    size, offset,                   // Top-right
                    0.0, 0.0                       // Top-left
            );
        }
        base.setFill(Color.LIMEGREEN);
        return base;
    }

    private Polygon createHeadSegment(double size, double offset) {
        Polygon head = new Polygon();
        head.getPoints().addAll(
                0.0, 0.0,                      // Top-left
                size, size / 2,                // Right center
                0.0, size                     // Bottom-left
        );
        head.setFill(Color.LIMEGREEN);
        return head;
    }

    private Polygon createTailSegment(double size, double offset) {
        Polygon tail = new Polygon();
        tail.getPoints().addAll(
                0.0, size / 2,                 // Left center
                size, 0.0,                     // Top-right
                size, size                    // Bottom-right
        );
        tail.setFill(Color.LIMEGREEN);
        return tail;
    }

    private Direction calculateDirection(SnakePart from, SnakePart to) {
        if (from == null || to == null) return null;
        int dx = to.position.getX() - from.position.getX();
        int dy = to.position.getY() - from.position.getY();
        if (dx == 1) return Direction.RIGHT;
        if (dx == -1) return Direction.LEFT;
        if (dy == 1) return Direction.DOWN;

        return Direction.UP;
    }

    private Direction opposite(Direction d) {
        if (d == null) return null;
        return switch (d) {
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case LEFT -> Direction.RIGHT;
            case RIGHT -> Direction.LEFT;
        };
    }

    private double getAngle(Direction dir) {
        if (dir == null) return 0.0;
        return switch (dir) {
            case UP -> -90.0;
            case DOWN -> 90.0;
            case LEFT -> 180.0;
            case RIGHT -> 0.0;
        };
    }

    void render(Pane root, boolean isHead, boolean isTail, int index, SnakePart previous, SnakePart next) {
        double size = TILE_SIZE;
        double offset = size * 0.2;
        double baseX = position.getX() * TILE_SIZE;
        double baseY = position.getY() * TILE_SIZE;

        // Incoming: direction moving into this segment (from tail side)
        Direction incoming = next != null ? calculateDirection(next, this) : opposite(calculateDirection(previous, this));
        // Outgoing: direction moving out of this segment (to head side)
        Direction outgoing = previous != null ? calculateDirection(this, previous) : null;

        Polygon segment;
        double angle = getAngle(incoming);

        if (isHead) {
            segment = createHeadSegment(size, offset);
        } else if (isTail) {
            segment = createTailSegment(size, offset);
        } else {
            if (incoming == outgoing || outgoing == null) {
                // Straight segment
                boolean mirrored = (index % 2 == 1);
                segment = createBaseBodySegment(size, offset, mirrored);
            } else {
                // Turn segment with 45-degree rotated rectangle
                double diagSize = size / Math.sqrt(2); // Diagonal size for 45-degree rotation
                segment = new Polygon();
                segment.getPoints().addAll(
                        size / 2 - diagSize / 2, size / 2 - diagSize / 2, // Top-left
                        size / 2 + diagSize / 2, size / 2 - diagSize / 2, // Top-right
                        size / 2 + diagSize / 2, size / 2 + diagSize / 2, // Bottom-right
                        size / 2 - diagSize / 2, size / 2 + diagSize / 2  // Bottom-left
                );
                segment.setFill(Color.LIMEGREEN);
                angle = 45.0; // 45-degree rotation for all turns
            }
        }

        segment.getTransforms().add(new Rotate(angle, size / 2, size / 2));
        segment.setTranslateX(baseX);
        segment.setTranslateY(baseY);

        root.getChildren().add(segment);
    }
}
