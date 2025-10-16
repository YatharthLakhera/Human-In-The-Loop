package com.lyzr.human_in_the_loop.db.converter;

import com.lyzr.human_in_the_loop.db.models.TemplateData;
import com.lyzr.human_in_the_loop.utils.ApplicationUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TemplateDataConverter implements AttributeConverter<TemplateData, String> {

    @Override
    public String convertToDatabaseColumn(TemplateData attribute) {
        return ApplicationUtils.GSON.toJson(attribute);
    }

    @Override
    public TemplateData convertToEntityAttribute(String dbData) {
        return ApplicationUtils.GSON.fromJson(dbData, TemplateData.class);
    }
}
