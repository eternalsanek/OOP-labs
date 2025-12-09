# Сборка
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Копируем только pom.xml
COPY pom.xml .

# Скачиваем зависимости (пропускаем ошибки сети)
RUN mvn dependency:go-offline -B || echo "Пропускаем ошибки загрузки зависимостей"

# Копируем исходники
COPY src ./src

# Собираем проект
RUN mvn clean package -DskipTests

# Запуск
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Создание директорий для логов
RUN mkdir -p /app/logs /app/reports

# Копирование JAR
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]