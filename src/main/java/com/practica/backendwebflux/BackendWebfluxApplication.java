package com.practica.backendwebflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación Spring Boot WebFlux.
 * Arquitectura reactiva no bloqueante sobre Project Reactor.
 */
@SpringBootApplication
public class BackendWebfluxApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendWebfluxApplication.class, args);
    }
}
