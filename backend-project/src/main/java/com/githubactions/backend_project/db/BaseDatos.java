package com.githubactions.backend_project.db;

import com.githubactions.backend_project.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory database using Singleton pattern.
 */
public class BaseDatos {
    private static final Logger log = LoggerFactory.getLogger(BaseDatos.class);
    private static final BaseDatos INSTANCE = new BaseDatos();

    private final Map<String, Usuario> data = new ConcurrentHashMap<>(); // key = correo

    private BaseDatos() {
        log.info("BaseDatos inicializada (en memoria)");
    }

    public static BaseDatos getInstance() {
        return INSTANCE;
    }

    public Mono<Usuario> crear(Usuario usuario) {
        if (usuario == null || usuario.getCorreo() == null) {
            return Mono.error(new IllegalArgumentException("Usuario o correo no puede ser nulo"));
        }
        log.debug("Creando usuario con correo: {}", usuario.getCorreo());
        Usuario prev = data.putIfAbsent(usuario.getCorreo(), usuario);
        if (prev != null) {
            return Mono.error(new IllegalStateException("El usuario ya existe"));
        }
        return Mono.just(usuario);
    }

    public Mono<Usuario> obtenerPorCorreo(String correo) {
        return Mono.justOrEmpty(data.get(correo));
    }

    public Flux<Usuario> listar() {
        return Flux.fromIterable(data.values());
    }

    public Mono<Usuario> actualizar(String correo, Usuario usuario) {
        return Mono.justOrEmpty(data.computeIfPresent(correo, (k, v) -> {
            if (usuario.getNombre() != null) v.setNombre(usuario.getNombre());
            if (usuario.getApellido() != null) v.setApellido(usuario.getApellido());
            if (usuario.getEdad() != null) v.setEdad(usuario.getEdad());
            if (usuario.getFecha_registro() != null) v.setFecha_registro(usuario.getFecha_registro());
            if (usuario.getContrasena() != null) v.setContrasena(usuario.getContrasena());
            return v;
        }));
    }

    public Mono<Boolean> eliminar(String correo) {
        return Mono.just(data.remove(correo) != null);
    }

    // Visible for testing
    public void clear() {
        data.clear();
    }
}
