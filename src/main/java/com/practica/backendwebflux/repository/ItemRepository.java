package com.practica.backendwebflux.repository;

import com.practica.backendwebflux.model.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Repositorio reactivo para la entidad Item.
 *
 * ReactiveMongoRepository extiende ReactiveCrudRepository y ofrece
 * operaciones CRUD que devuelven tipos reactivos (Mono/Flux),
 * garantizando un modelo de E/S completamente no bloqueante.
 */
@Repository
public interface ItemRepository extends ReactiveMongoRepository<Item, String> {

    /**
     * Búsqueda reactiva por nombre (case-insensitive, coincidencia parcial).
     * Spring Data genera la query automáticamente desde el nombre del método.
     *
     * @param nombre término de búsqueda
     * @return Flux de Items que coincidan con el nombre
     */
    Flux<Item> findByNombreContainingIgnoreCase(String nombre);
}
