package com.githubactions.backend_project.service;

import com.githubactions.backend_project.db.BaseDatos;
import com.githubactions.backend_project.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);
    private final BaseDatos db = BaseDatos.getInstance();

    public Mono<Usuario> crear(Usuario usuario) {
        if (usuario.getFecha_registro() == null) {
            usuario.setFecha_registro(LocalDateTime.now());
        }
        log.info("Solicitud para crear usuario: {}", usuario.getCorreo());
        return db.crear(usuario)
                .doOnSuccess(u -> log.info("Usuario creado: {}", u.getCorreo()))
                .doOnError(e -> log.error("Error creando usuario {}: {}", usuario.getCorreo(), e.getMessage()));
    }

    public Mono<Usuario> obtener(String correo) {
        log.info("Obtener usuario por correo: {}", correo);
        return db.obtenerPorCorreo(correo)
                .doOnNext(u -> log.debug("Usuario encontrado: {}", u.getCorreo()))
                .switchIfEmpty(Mono.empty());
    }

    public Flux<Usuario> listar() {
        log.info("Listar usuarios");
        return db.listar();
    }

    public Mono<Usuario> actualizar(String correo, Usuario usuario) {
        log.info("Actualizar usuario: {}", correo);
        return db.actualizar(correo, usuario)
                .switchIfEmpty(Mono.error(new IllegalStateException("Usuario no existe")))
                .doOnError(e -> log.error("Error actualizando usuario {}: {}", correo, e.getMessage()))
                .doOnSuccess(u -> log.info("Usuario actualizado: {}", correo));
    }

    public Mono<Boolean> eliminar(String correo) {
        log.info("Eliminar usuario: {}", correo);
        return db.eliminar(correo)
                .doOnNext(res -> log.info("Usuario eliminado {}: {}", correo, res));
    }
}
