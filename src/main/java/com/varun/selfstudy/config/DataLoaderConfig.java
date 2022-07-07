package com.varun.selfstudy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.varun.selfstudy.util.JacksonJsonObjectMapper;

@Configuration
public class DataLoaderConfig {

    @Bean
    public ObjectMapper jsonObjectMapper() {
        JacksonJsonObjectMapper jacksonJsonObjectMapper = new JacksonJsonObjectMapper();
        jacksonJsonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return jacksonJsonObjectMapper;
    }
}
