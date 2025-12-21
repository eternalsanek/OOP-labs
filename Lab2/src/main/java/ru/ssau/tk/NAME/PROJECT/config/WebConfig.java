package ru.ssau.tk.NAME.PROJECT.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // Аннотация, указывающая, что это класс конфигурации Spring
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Применяется ко всем эндпоинтам
                .allowedOrigins("http://localhost:3000") // Разрешаем origin фронтенда
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Убедитесь, что OPTIONS разрешён
                .allowedHeaders("*") // Разрешаем все заголовки
                .allowCredentials(true); // Разрешаем отправку куки/авторизационных заголовков
    }
}
