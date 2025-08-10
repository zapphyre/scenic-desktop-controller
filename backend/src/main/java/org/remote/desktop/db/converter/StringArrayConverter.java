package org.remote.desktop.db.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Converter(autoApply = false)
public class StringArrayConverter implements AttributeConverter<Set<String>, String> {

    private static final String DELIMITER = ",";

    @Override
    public String convertToDatabaseColumn(Set<String> attribute) {
        return convertToString(attribute);
    }

    @Override
    public Set<String> convertToEntityAttribute(String dbData) {
        return convertToList(dbData);
    }

    public static String convertToString(Set<String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        return String.join(DELIMITER, attribute);
    }

    public static Set<String> convertToList(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new HashSet<>();
        }
        return Arrays.stream(dbData.split(DELIMITER)).collect(Collectors.toCollection(HashSet::new));
    }
}