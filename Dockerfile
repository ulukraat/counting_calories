# Используем официальный легковесный образ OpenJDK 21
FROM eclipse-temurin:21-jdk-alpine

# Указываем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем JAR-файл приложения
COPY target/counting_calories-0.0.1-SNAPSHOT.jar app.jar

# Создаем пользователя для безопасности
RUN addgroup -g 1001 -S spring && \
    adduser -S spring -u 1001 -G spring

# Меняем владельца файлов
RUN chown -R spring:spring /app
USER spring:spring

# Открываем порт 8080
EXPOSE 8080

# Запускаем приложение с оптимизированными JVM параметрами
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]