package org.remote.desktop.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.remote.desktop.entity.GPadEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GPadEventRepositoryTest {

    @Autowired
    private GPadEventRepository gpadEventRepository;

    @Test
    void findAllKeyPressOnlyTest() {
        GPadEvent x = createGPadEvent(false, "x");
        GPadEvent y = createGPadEvent(false, "y");
        GPadEvent ya = createGPadEvent(false, "y");
        GPadEvent yl = createGPadEvent(true, "y");

        gpadEventRepository.save(x);
        gpadEventRepository.save(y);
        gpadEventRepository.save(ya);
        gpadEventRepository.save(yl);

        List<GPadEvent> allKeyPressOnly = gpadEventRepository.findAllKeyPressOnly();
        gpadEventRepository.flush();

        Assertions.assertEquals(2, allKeyPressOnly.size());
    }

    @Test
    void findAllByModifiersNotEmptyTest() {
        GPadEvent y = createGPadEvent(false, "y");
        GPadEvent ya = createGPadEvent(false, "y");

        gpadEventRepository.save(y);
        gpadEventRepository.save(ya);

        List<GPadEvent> modifiersNotEmpty = gpadEventRepository.findAllByModifiersNotEmpty();

        Assertions.assertEquals(1, modifiersNotEmpty.size());
    }

    @Test
    void findAllByLongPressTrue() {
        GPadEvent ya = createGPadEvent(false, "y", "a");
        GPadEvent yl = createGPadEvent(true,"y");

        gpadEventRepository.save(ya);
        gpadEventRepository.save(yl);

        List<GPadEvent> allByLongPressTrue = gpadEventRepository.findAllByLongPressTrue();

        Assertions.assertEquals(1, allByLongPressTrue.size());
    }

    GPadEvent createGPadEvent(boolean longpress, String trigger, String ...modifiers) {
        return GPadEvent.builder()
                .modifiers(Arrays.asList(modifiers))
                .longPress(longpress)
                .trigger(trigger)
                .build();
    }
}
