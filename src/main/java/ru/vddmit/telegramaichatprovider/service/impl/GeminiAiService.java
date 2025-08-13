package ru.vddmit.telegramaichatprovider.service.impl;

import com.google.genai.Client;
import com.google.genai.types.CountTokensResponse;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Service;
import ru.vddmit.telegramaichatprovider.service.AiService;

import java.util.Optional;

@Service
public class GeminiAiService implements AiService {

    @Override
    public String generateResponse(String prompt, String apiKey, String modelName) throws Exception {
        Client client = Client.builder().apiKey(apiKey).build();

        GenerateContentResponse response =
                client.models.generateContent(modelName, prompt, null);

        return response.text();
    }

    @Override
    public boolean supportsModel(String modelName) {
        //TODO: добавить проверку моделей доступных по ключу
        return modelName != null && modelName.toLowerCase().contains("gemini");
    }

    @Override
    public Optional<Integer> countTokens(String prompt, String apiKey, String modelName) throws Exception {
        Client client = Client.builder().apiKey(apiKey).build();

        CountTokensResponse response = client.models.countTokens(modelName, prompt, null);

        return response.totalTokens();
    }
}
