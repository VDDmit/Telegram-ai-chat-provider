package ru.vddmit.telegramaichatprovider.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JsonUtils {
    static ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public static String toJson(Object object) {
        if (object == null) {
            return "null";
        }
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "<error: " + e.getMessage() + ">";
        }
    }
}
