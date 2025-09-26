package com.githubactions.backend_project.controller;

import com.githubactions.backend_project.model.Usuario;
import com.githubactions.backend_project.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/api/usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
public class UsuarioController {
    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);
    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Usuario> crear(@RequestBody Mono<Usuario> usuarioMono) {
    return usuarioMono
        .flatMap(service::crear)
        .onErrorMap(IllegalStateException.class,
            e -> new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e))
        .doOnSubscribe(s -> log.debug("POST /api/usuarios"));
    }

    @GetMapping("/{correo}")
    public Mono<Usuario> obtener(@PathVariable String correo) {
        return service.obtener(correo)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado")))
                .doOnSubscribe(s -> log.debug("GET /api/usuarios/{}", correo));
    }

    @GetMapping
    public Flux<Usuario> listar() {
        return service.listar().doOnSubscribe(s -> log.debug("GET /api/usuarios"));
    }

    @PutMapping(path = "/{correo}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Usuario> actualizar(@PathVariable String correo, @RequestBody Mono<Usuario> usuarioMono) {
    return usuarioMono
        .flatMap(u -> service.actualizar(correo, u))
        .onErrorMap(IllegalStateException.class,
            e -> new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e))
        .doOnSubscribe(s -> log.debug("PUT /api/usuarios/{}", correo));
    }

    @DeleteMapping("/{correo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> eliminar(@PathVariable String correo) {
        return service.eliminar(correo)
                .doOnSubscribe(s -> log.debug("DELETE /api/usuarios/{}", correo))
                .then();
    }
}
