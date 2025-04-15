package org.remote.desktop.conditional;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.io.File;
import java.util.Map;

public class FileNotExistsCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(ConditionalFilePresence.class.getName());
        if (attributes == null) {
            return false;
        }
        String fileName = (String) attributes.get("value"); // Get property name from annotation
        boolean present = (boolean) attributes.get("present");

        if (fileName == null) {
            return false; // Or handle as needed
        }
        return present ^ !new File(fileName).exists();
    }
}