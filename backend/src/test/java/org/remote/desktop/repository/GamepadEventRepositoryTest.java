package org.remote.desktop.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.remote.desktop.db.entity.GamepadEvent;
import org.remote.desktop.db.repository.GamepadEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GamepadEventRepositoryTest {

    @Autowired
    private GamepadEventRepository gpadEventRepository;

    @Test
    void findAllKeyPressOnlyTest() {
        GamepadEvent x = createGPadEvent(false, "x");
        GamepadEvent y = createGPadEvent(false, "y");
        GamepadEvent ya = createGPadEvent(false, "y");
        GamepadEvent yl = createGPadEvent(true, "y");

        gpadEventRepository.save(x);
        gpadEventRepository.save(y);
        gpadEventRepository.save(ya);
        gpadEventRepository.save(yl);

        List<GamepadEvent> allKeyPressOnly = gpadEventRepository.findAllKeyPressOnly();
        gpadEventRepository.flush();

        Assertions.assertEquals(2, allKeyPressOnly.size());
    }

    @Test
    void findAllByModifiersNotEmptyTest() {
        GamepadEvent y = createGPadEvent(false, "y");
        GamepadEvent ya = createGPadEvent(false, "y");

        gpadEventRepository.save(y);
        gpadEventRepository.save(ya);

        List<GamepadEvent> modifiersNotEmpty = gpadEventRepository.findAllByModifiersNotEmpty();

        Assertions.assertEquals(1, modifiersNotEmpty.size());
    }

    @Test
    void findAllByLongPressTrue() {
        GamepadEvent ya = createGPadEvent(false, "y", "a");
        GamepadEvent yl = createGPadEvent(true,"y");

        gpadEventRepository.save(ya);
        gpadEventRepository.save(yl);

        List<GamepadEvent> allByLongPressTrue = gpadEventRepository.findAllByLongPressTrue();

        Assertions.assertEquals(1, allByLongPressTrue.size());
    }

    GamepadEvent createGPadEvent(boolean longpress, String trigger, String ...modifiers) {
        return GamepadEvent.builder()
                .modifiers(Arrays.asList(modifiers))
                .longPress(longpress)
                .trigger(trigger)
                .build();
    }
}
