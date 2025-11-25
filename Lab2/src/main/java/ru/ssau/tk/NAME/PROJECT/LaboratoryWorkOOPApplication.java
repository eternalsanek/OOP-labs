package ru.ssau.tk.NAME.PROJECT;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "ru.ssau.tk.NAME.PROJECT.repository")
public class LaboratoryWorkOOPApplication {

    public static void main(String[] args) {
        SpringApplication.run(LaboratoryWorkOOPApplication.class, args);
    }
}
