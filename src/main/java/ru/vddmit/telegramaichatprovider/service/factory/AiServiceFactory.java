package ru.vddmit.telegramaichatprovider.service.factory;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.vddmit.telegramaichatprovider.service.AiService;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AiServiceFactory {
    List<AiService> aiServices;

    public AiService getAiServices(String modelName) {
        return aiServices.stream()
                .filter(aiService -> aiService.supportsModel(modelName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported AI model:" + modelName));
    }
}
