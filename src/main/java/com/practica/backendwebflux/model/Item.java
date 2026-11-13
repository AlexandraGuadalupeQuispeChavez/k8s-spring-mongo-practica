package com.practica.backendwebflux.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Documento MongoDB que representa un Item almacenado en la colección "items".
 * Anotado con @Document para que Spring Data MongoDB lo gestione como colección.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "items")
public class Item {

    /**
     * Identificador único generado automáticamente por MongoDB (ObjectId).
     */
    @Id
    private String id;

    /**
     * Nombre del item.
     */
    private String nombre;

    /**
     * Descripción detallada del item.
     */
    private String descripcion;
}
