package org.remote.desktop.ui.game.snake;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends Application {
    public static final int TILE_SIZE = 20;
    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;
    private final ArrayList<SnakePart> snake = new ArrayList<>();
    private Position treat;
    private Direction direction = Direction.RIGHT;
    private boolean isGameOver = false;
    private final Random random = new Random();
    private long lastUpdate = 0;
    private static final long UPDATE_INTERVAL = 150_000_000; // 150ms
    private final boolean crashOnWalls = false;

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        scene.setFill(Color.TRANSPARENT);

        snake.add(new SnakePart(Position.builder()
                .x(WIDTH / 2)
                .y(HEIGHT / 2)
                .build(), direction));
        snake.add(new SnakePart(Position.builder()
                .x(WIDTH / 2 - 1)
                .y(HEIGHT / 2)
                .build(), direction));
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

            Position current = Position.builder()
                    .x(x)
                    .y(y)
                    .build();
            if (snake.stream().noneMatch(part -> part.position == current)) {
                treat = current;
                break;
            }
        }
    }

    private void updateGame(Pane root) {
        SnakePart head = new SnakePart(snake.getFirst().position.toBuilder().build(), direction);
        switch (direction) {
            case UP:
                head.position.decrementY();
                break;
            case DOWN:
                head.position.incrementY();
                break;
            case LEFT:
                head.position.decrementX();
                break;
            case RIGHT:
                head.position.incrementX();
                break;
        }

        if (crashOnWalls) {
            if (head.position.getX() < 0 || head.position.getX() >= WIDTH ||
                    head.position.getY() < 0 || head.position.getY() >= HEIGHT) {
                isGameOver = true;
                return;
            }
        } else {
            if (head.position.getX() < 0) head.position.setX(WIDTH - 1);
            else if (head.position.getX() >= WIDTH) head.position.setX(0);
            else if (head.position.getY() < 0) head.position.setY(HEIGHT - 1);
            else if (head.position.getY() >= HEIGHT) head.position.setY(0);
        }

        if (snake.stream().anyMatch(part -> part.collidesWith(head.position))) {
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