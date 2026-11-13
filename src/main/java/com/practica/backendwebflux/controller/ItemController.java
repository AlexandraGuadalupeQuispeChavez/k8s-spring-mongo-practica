package com.practica.backendwebflux.controller;

import com.practica.backendwebflux.model.Item;
import com.practica.backendwebflux.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controlador REST reactivo para la API de Items.
 *
 * Todos los métodos devuelven tipos reactivos (Mono/Flux) que el runtime
 * de WebFlux suscribe de forma no bloqueante usando el event loop de Netty.
 */
@Slf4j
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;

    /**
     * GET /api/items
     * Devuelve todos los items almacenados en MongoDB como stream reactivo.
     *
     * @return Flux<Item> con todos los documentos de la colección
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Item> getAllItems() {
        log.info("GET /api/items - Obteniendo todos los items");
        return itemRepository.findAll()
                .doOnComplete(() -> log.info("GET /api/items - Consulta completada"))
                .doOnError(e -> log.error("GET /api/items - Error: {}", e.getMessage()));
    }

    /**
     * GET /api/items/{id}
     * Busca un item por su ID de MongoDB.
     *
     * @param id ObjectId del documento en MongoDB
     * @return Mono<Item> o 404 si no existe
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Item> getItemById(@PathVariable String id) {
        log.info("GET /api/items/{} - Buscando item por ID", id);
        return itemRepository.findById(id)
                .switchIfEmpty(Mono.error(new ItemNotFoundException("Item no encontrado con ID: " + id)));
    }

    /**
     * POST /api/items
     * Persiste un nuevo item en MongoDB de forma reactiva.
     *
     * @param item cuerpo de la solicitud con nombre y descripcion
     * @return Mono<Item> con el documento guardado (incluye el ID generado)
     */
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody Item item) {
        log.info("POST /api/items - Creando item: {}", item.getNombre());
        return itemRepository.save(item)
                .doOnSuccess(saved -> log.info("POST /api/items - Item guardado con ID: {}", saved.getId()))
                .doOnError(e -> log.error("POST /api/items - Error al guardar: {}", e.getMessage()));
    }

    /**
     * DELETE /api/items/{id}
     * Elimina un item por ID.
     *
     * @param id ObjectId del documento a eliminar
     * @return Mono<Void> vacío tras completar la eliminación
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteItem(@PathVariable String id) {
        log.info("DELETE /api/items/{} - Eliminando item", id);
        return itemRepository.deleteById(id);
    }

    // -------------------------------------------------------
    // Excepción interna para items no encontrados
    // -------------------------------------------------------
    @ResponseStatus(HttpStatus.NOT_FOUND)
    static class ItemNotFoundException extends RuntimeException {
        ItemNotFoundException(String message) {
            super(message);
        }
    }
}
