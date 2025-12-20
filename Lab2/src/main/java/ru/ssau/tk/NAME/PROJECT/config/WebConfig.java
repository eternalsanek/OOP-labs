package ru.ssau.tk.NAME.PROJECT.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // Аннотация, указывающая, что это класс конфигурации Spring
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Применяется ко всем эндпоинтам API
                .allowedOrigins("http://localhost:3000") // Разрешаем запросы с порта 3000 (React dev server)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Разрешаем стандартные методы
                .allowCredentials(true); // Разрешаем отправку credentials (например, JWT токена в заголовке Authorization)
    }
}
