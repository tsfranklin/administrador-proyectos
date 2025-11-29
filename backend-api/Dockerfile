# --------------------------------------------------------------------------------
# ETAPA 1: BUILD (Maven + JDK 17)
# --------------------------------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /api

# Copia el archivo pom.xml desde la raíz del proyecto a la carpeta de trabajo
COPY pom.xml .

# Descarga las dependencias para que se cacheen
RUN mvn dependency:go-offline

# Copia el código fuente
COPY src ./src

# Compila el proyecto y crea el JAR (ignora los tests para una construcción más rápida)
RUN mvn package -DskipTests

# -----------------------------------------------------
# ETAPA 2: RUNTIME (imagen ligera JRE 17)
# -----------------------------------------------------
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /api/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]