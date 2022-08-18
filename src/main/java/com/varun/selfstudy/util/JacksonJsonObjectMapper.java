package com.varun.selfstudy.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JacksonJsonObjectMapper extends ObjectMapper {

    private static final long serialVersionUID = 1L;

    public JacksonJsonObjectMapper() {
        this.updateFeatureConfigurations();
    }

    private void updateFeatureConfigurations() {
        this.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        this.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
        this.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
