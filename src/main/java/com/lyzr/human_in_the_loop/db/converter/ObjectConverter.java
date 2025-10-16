package com.lyzr.human_in_the_loop.db.converter;

import com.lyzr.human_in_the_loop.utils.ApplicationUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ObjectConverter implements AttributeConverter<Object, String> {

    @Override
    public String convertToDatabaseColumn(Object attribute) {
        return ApplicationUtils.GSON.toJson(attribute);
    }

    @Override
    public Object convertToEntityAttribute(String dbData) {
        return ApplicationUtils.GSON.fromJson(dbData, Object.class);
    }
}
