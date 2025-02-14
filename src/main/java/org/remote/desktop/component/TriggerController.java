package org.remote.desktop.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.asmus.facade.TimedButtonGamepadFactory;
import org.asmus.model.EButtonAxisMapping;
import org.springframework.stereotype.Component;

import java.awt.*;

import static org.remote.desktop.actuate.MouseCtrl.mapVal;
import static org.remote.desktop.manager.DesktopSceneManager.filterTrigger;

@Component
@RequiredArgsConstructor
public class TriggerController {

    private final TimedButtonGamepadFactory timedButtonGamepadFactory;
    private int previousPosition = -32767;

    @SneakyThrows
    @PostConstruct
    void init() {
        Robot robot = new Robot();
        timedButtonGamepadFactory.getButtonStream()
                .filter(q -> q.getType() == EButtonAxisMapping.B)
                .subscribe(q -> robot.mouseWheel(10));

        timedButtonGamepadFactory.getTriggerStream()
                .filter(filterTrigger(EButtonAxisMapping.TRIGGER_LEFT))
                .subscribe(q -> {
                    int jump = Math.abs(previousPosition - q.getPosition());

                    int mapped = (int) mapVal(jump, 0, 5_000, 1, 6);
                    System.out.println(mapped);

                    if (previousPosition < q.getPosition())
                        robot.mouseWheel(mapped);

                    previousPosition = q.getPosition();
                });

    }
}
