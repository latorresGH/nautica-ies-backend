# ---------- Etapa 1: Build (Maven) ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos solo pom para bajar dependencias primero (cacheable)
COPY pom.xml .
RUN mvn -q dependency:go-offline

# Ahora copiamos el código y compilamos
COPY src ./src
RUN mvn -q package -DskipTests


# ---------- Etapa 2: Runtime (ejecución) ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiamos el .jar construido en la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Render expone la app por la variable de entorno $PORT
# Si no está definida, usamos 8080 por defecto (para correrlo local si querés)
ENV JAVA_OPTS=""
EXPOSE 8080
CMD ["sh","-c","java $JAVA_OPTS -jar app.jar --server.port=${PORT:-8080}"]
