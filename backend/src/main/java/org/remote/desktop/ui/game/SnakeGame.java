package org.remote.desktop.ui.game;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends Application {
    private static final int TILE_SIZE = 20;
    private static final int WIDTH = 20;
    private static final int HEIGHT = 20;
    private final ArrayList<SnakePart> snake = new ArrayList<>();
    private SnakePart treat;
    private Direction direction = Direction.RIGHT;
    private boolean isGameOver = false;
    private final Random random = new Random();
    private long lastUpdate = 0;
    private static final long UPDATE_INTERVAL = 150_000_000; // 150ms
    private final boolean crashOnWalls = false;

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private static class SnakePart {
        int x, y;
        Direction direction;

        SnakePart(int x, int y, Direction direction) {
            this.x = x;
            this.y = y;
            this.direction = direction;
        }

        boolean collidesWith(SnakePart other) {
            return this.x == other.x && this.y == other.y;
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
            int dx = to.x - from.x;
            int dy = to.y - from.y;
            if (dx == 1) return Direction.RIGHT;
            if (dx == -1) return Direction.LEFT;
            if (dy == 1) return Direction.DOWN;
            if (dy == -1) return Direction.UP;
            return null;
        }

        private Direction opposite(Direction d) {
            if (d == null) return null;
            switch (d) {
                case UP: return Direction.DOWN;
                case DOWN: return Direction.UP;
                case LEFT: return Direction.RIGHT;
                case RIGHT: return Direction.LEFT;
                default: return null;
            }
        }

        private double getAngle(Direction dir) {
            if (dir == null) return 0.0;
            switch (dir) {
                case UP: return -90.0;
                case DOWN: return 90.0;
                case LEFT: return 180.0;
                case RIGHT: return 0.0;
                default: return 0.0;
            }
        }

        void render(Pane root, boolean isHead, boolean isTail, int index, SnakePart previous, SnakePart next) {
            double size = TILE_SIZE;
            double offset = size * 0.2;
            double baseX = x * TILE_SIZE;
            double baseY = y * TILE_SIZE;

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
                    // Turn segment with cut corner on the inside
                    segment = new Polygon();
                    segment.setFill(Color.LIMEGREEN);
                    double s = size;
                    double o = offset;

                    if (incoming == Direction.RIGHT && outgoing == Direction.UP) {
                        // Cut bottom left
                        segment.getPoints().addAll(
                                0.0, 0.0,
                                s, 0.0,
                                s, s,
                                o, s,
                                0.0, s - o
                        );
                    } else if (incoming == Direction.RIGHT && outgoing == Direction.DOWN) {
                        // Cut top left
                        segment.getPoints().addAll(
                                o, 0.0,
                                s, 0.0,
                                s, s,
                                0.0, s,
                                0.0, o
                        );
                    } else if (incoming == Direction.LEFT && outgoing == Direction.UP) {
                        // Cut bottom right
                        segment.getPoints().addAll(
                                0.0, 0.0,
                                s, 0.0,
                                s, s - o,
                                s - o, s,
                                0.0, s
                        );
                    } else if (incoming == Direction.LEFT && outgoing == Direction.DOWN) {
                        // Cut top right
                        segment.getPoints().addAll(
                                0.0, 0.0,
                                s - o, 0.0,
                                s, o,
                                s, s,
                                0.0, s
                        );
                    } else if (incoming == Direction.UP && outgoing == Direction.LEFT) {
                        // Cut top right
                        segment.getPoints().addAll(
                                0.0, 0.0,
                                s - o, 0.0,
                                s, o,
                                s, s,
                                0.0, s
                        );
                    } else if (incoming == Direction.UP && outgoing == Direction.RIGHT) {
                        // Cut top left
                        segment.getPoints().addAll(
                                o, 0.0,
                                s, 0.0,
                                s, s,
                                0.0, s,
                                0.0, o
                        );
                    } else if (incoming == Direction.DOWN && outgoing == Direction.LEFT) {
                        // Cut bottom right
                        segment.getPoints().addAll(
                                0.0, 0.0,
                                s, 0.0,
                                s, s - o,
                                s - o, s,
                                0.0, s
                        );
                    } else if (incoming == Direction.DOWN && outgoing == Direction.RIGHT) {
                        // Cut bottom left
                        segment.getPoints().addAll(
                                0.0, 0.0,
                                s, 0.0,
                                s, s,
                                o, s,
                                0.0, s - o
                        );
                    }
                    angle = 0.0;
                }
            }

            segment.getTransforms().add(new Rotate(angle, size / 2, size / 2));
            segment.setTranslateX(baseX);
            segment.setTranslateY(baseY);

            root.getChildren().add(segment);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        scene.setFill(Color.TRANSPARENT);

        snake.add(new SnakePart(WIDTH / 2, HEIGHT / 2, direction));
        snake.add(new SnakePart(WIDTH / 2 - 1, HEIGHT / 2, direction));
        placeTreat();

        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.UP && direction != Direction.DOWN) direction = Direction.UP;
            else if (code == KeyCode.DOWN && direction != Direction.UP) direction = Direction.DOWN;
            else if (code == KeyCode.LEFT && direction != Direction.RIGHT) direction = Direction.LEFT;
            else if (code == KeyCode.RIGHT && direction != Direction.LEFT) direction = Direction.RIGHT;
        });

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        root.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= UPDATE_INTERVAL && !isGameOver) {
                    updateGame(root);
                    lastUpdate = now;
                }
            }
        };
        timer.start();

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void placeTreat() {
        while (true) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            final int finalX = x;
            final int finalY = y;
            if (snake.stream().noneMatch(part -> part.x == finalX && part.y == finalY)) {
                treat = new SnakePart(finalX, finalY, Direction.RIGHT);
                break;
            }
        }
    }

    private void updateGame(Pane root) {
        SnakePart head = new SnakePart(snake.get(0).x, snake.get(0).y, direction);
        switch (direction) {
            case UP: head.y -= 1; break;
            case DOWN: head.y += 1; break;
            case LEFT: head.x -= 1; break;
            case RIGHT: head.x += 1; break;
        }

        if (crashOnWalls) {
            if (head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT) {
                isGameOver = true;
                return;
            }
        } else {
            if (head.x < 0) head.x = WIDTH - 1;
            else if (head.x >= WIDTH) head.x = 0;
            else if (head.y < 0) head.y = HEIGHT - 1;
            else if (head.y >= HEIGHT) head.y = 0;
        }

        if (snake.stream().anyMatch(part -> part.collidesWith(head))) {
            isGameOver = true;
            return;
        }

        snake.addFirst(head);

        if (head.collidesWith(treat)) {
            placeTreat();
        } else {
            snake.removeLast();
        }

        root.getChildren().clear();

        for (int i = 0; i < snake.size(); i++) {
            SnakePart part = snake.get(i);
            boolean isHead = (i == 0);
            boolean isTail = (i == snake.size() - 1);
            SnakePart previous = i > 0 ? snake.get(i - 1) : null;
            SnakePart nextPart = i < snake.size() - 1 ? snake.get(i + 1) : null;
            part.render(root, isHead, isTail, i, previous, nextPart);
        }

        Rectangle treatRect = new Rectangle(
                treat.x * TILE_SIZE + (TILE_SIZE * 0.2),
                treat.y * TILE_SIZE + (TILE_SIZE * 0.2),
                TILE_SIZE * 0.6,
                TILE_SIZE * 0.6
        );
        treatRect.setFill(Color.RED);
        root.getChildren().add(treatRect);
    }

    public static void main(String[] args) {
        launch(args);
    }
}