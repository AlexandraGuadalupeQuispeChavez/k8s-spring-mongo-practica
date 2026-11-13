# =============================================================================
# Dockerfile Multi-Stage para Spring WebFlux Backend
# Imagen final: backend-webflux:1.0
#
# Stage 1 (builder): Compila el proyecto con Maven y genera el JAR ejecutable.
# Stage 2 (runtime): Copia solo el JAR al runtime mínimo de JRE para reducir
#                    el tamaño de la imagen final (~100MB vs ~400MB con JDK).
# =============================================================================

# ---------------------------
# Stage 1: BUILD
# ---------------------------
# Imagen oficial de Maven con JDK 17 — incluye mvn listo para usar
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

LABEL stage="builder"

WORKDIR /app

# Copiamos primero el pom.xml para aprovechar el cache de capas de Docker.
# Si solo cambia el código fuente (no dependencias), Maven no re-descarga nada.
COPY pom.xml .

# Descarga de dependencias en modo offline para pre-popular el caché local
RUN mvn dependency:go-offline -B

# Copiamos el código fuente completo
COPY src ./src

# Compilación y empaquetado (saltamos tests en la imagen; úsalos en CI)
RUN mvn clean package -DskipTests -B

# ---------------------------
# Stage 2: RUNTIME
# ---------------------------
FROM eclipse-temurin:17-jre-alpine AS runtime

LABEL maintainer="practica-s11"
LABEL version="1.0"
LABEL description="Backend Spring WebFlux - Práctica Kubernetes S11"

# Usuario no-root por seguridad (buenas prácticas en contenedores de producción)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copiamos únicamente el JAR generado en el stage anterior
COPY --from=builder /app/target/*.jar app.jar

# Ajustamos los permisos al usuario no-root
RUN chown appuser:appgroup app.jar

USER appuser

# Puerto expuesto (documentativo; el binding real lo hace K8s via Service)
EXPOSE 8080

# Opciones JVM optimizadas para contenedores:
#   -XX:+UseContainerSupport      -> respeta los límites de CPU/MEM del cgroup
#   -XX:MaxRAMPercentage=75.0     -> usa máximo el 75% de la RAM del contenedor
#   -Djava.security.egd=...       -> acelera el arranque de Tomcat/Netty
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
