package org.remote.desktop.config;

import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.ELogicalEventType;
import org.remote.desktop.model.ExpandedLogicalNaming;
import org.remote.desktop.model.LogicalName;
import org.remote.desktop.model.NamedEnum;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Configuration
public class LogicalNamingConfig {

    @Bean
    public List<String> allLogicalTriggerNames() {
        return Stream.of(new NamedEnum(EButtonAxisMapping.class),
                        new ExpandedLogicalNaming(ELogicalEventType.class, EButtonAxisMapping.TRIGGER_LEFT.getMapping()),
                        new ExpandedLogicalNaming(ELogicalEventType.class, EButtonAxisMapping.TRIGGER_RIGHT.getMapping()))
                .map(LogicalName::names)
                .flatMap(Collection::stream)
                .toList();
    }
}
