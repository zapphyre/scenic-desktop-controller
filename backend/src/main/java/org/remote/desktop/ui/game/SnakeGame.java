package org.remote.desktop.ui.game;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends Application {
    private static final int TILE_SIZE = 20;
    private static final int WIDTH = 20;
    private static final int HEIGHT = 20;
    private ArrayList<SnakePart> snake = new ArrayList<>();
    private SnakePart treat;
    private Direction direction = Direction.RIGHT;
    private boolean isGameOver = false;
    private Random random = new Random();
    private long lastUpdate = 0;
    private static final long UPDATE_INTERVAL = 150_000_000; // 150ms
    private boolean crashOnWalls = true;

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

        void render(Pane root, boolean isHead, boolean isTail, int index) {
            double size = TILE_SIZE;
            double offset = size * 0.2;

            double baseX = x * TILE_SIZE;
            double baseY = y * TILE_SIZE;

            boolean mirrored = (index % 2 == 1);

            Polygon shape = new Polygon();

            if (!mirrored) {
                shape.getPoints().addAll(new Double[]{
                        baseX + offset, baseY,
                        baseX + size, baseY,
                        baseX + size - offset, baseY + size,
                        baseX, baseY + size
                });
            } else {
                shape.getPoints().addAll(new Double[]{
                        baseX, baseY,
                        baseX + size - offset, baseY,
                        baseX + size, baseY + size,
                        baseX + offset, baseY + size
                });
            }

            shape.setFill(Color.LIMEGREEN);
            root.getChildren().add(shape);
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

        snake.add(0, head);

        if (head.collidesWith(treat)) {
            placeTreat();
        } else {
            snake.remove(snake.size() - 1);
        }

        root.getChildren().clear();

        for (int i = 0; i < snake.size(); i++) {
            SnakePart part = snake.get(i);
            boolean isHead = (i == 0);
            boolean isTail = (i == snake.size() - 1);
            part.render(root, isHead, isTail, i);
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
