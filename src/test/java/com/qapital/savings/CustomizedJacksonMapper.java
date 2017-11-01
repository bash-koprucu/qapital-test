package com.qapital.savings;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Holds the ObjectMapper instance similarily configured as the application
 * For testing serialization / deserialization of the objects
 */
public class CustomizedJacksonMapper {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mapper.setDateFormat(new ISO8601DateFormat());
        mapper.registerModule(new JavaTimeModule());
    }
    public static ObjectMapper getMapper() {
        return mapper;
    }

}
