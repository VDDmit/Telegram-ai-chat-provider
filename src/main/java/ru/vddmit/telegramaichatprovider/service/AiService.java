package ru.vddmit.telegramaichatprovider.service;

import java.util.Optional;

public interface AiService {

    String generateResponse(String prompt, String apiKey, String modelName) throws Exception;

    boolean supportsModel(String modelName);

    Optional<Integer> countTokens(String prompt, String apiKey, String modelName) throws Exception;
}
