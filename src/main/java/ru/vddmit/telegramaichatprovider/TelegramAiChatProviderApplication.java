package ru.vddmit.telegramaichatprovider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.vddmit.telegramaichatprovider.config.AppConfig;

@SpringBootApplication
public class TelegramAiChatProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelegramAiChatProviderApplication.class, args);
    }

}
