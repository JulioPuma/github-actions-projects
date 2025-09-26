package com.githubactions.backend_project;

import com.githubactions.backend_project.db.BaseDatos;
import com.githubactions.backend_project.model.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsuarioControllerTests {

    @LocalServerPort
    int port;

    @Autowired
    private WebTestClient webClient;

    @AfterEach
    void cleanup() {
        BaseDatos.getInstance().clear();
    }

    @Test
    void crearYObtenerUsuario() {
        Usuario u = new Usuario("Juan", "Perez", 30, LocalDateTime.now(), "juan@example.com", "secreta");

        // Create
        webClient.post().uri("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(u)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.correo").isEqualTo("juan@example.com")
                .jsonPath("$.contrasena").doesNotExist();

        // Get
        webClient.get().uri("/api/usuarios/{correo}", "juan@example.com")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.nombre").isEqualTo("Juan")
                .jsonPath("$.contrasena").doesNotExist();
    }

    @Test
    void listarUsuarios() {
        Usuario u1 = new Usuario("A", "A", 20, LocalDateTime.now(), "a@a.com", "x");
        Usuario u2 = new Usuario("B", "B", 21, LocalDateTime.now(), "b@b.com", "y");

        webClient.post().uri("/api/usuarios").contentType(MediaType.APPLICATION_JSON).bodyValue(u1).exchange().expectStatus().isCreated();
        webClient.post().uri("/api/usuarios").contentType(MediaType.APPLICATION_JSON).bodyValue(u2).exchange().expectStatus().isCreated();

        webClient.get().uri("/api/usuarios")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Usuario.class)
                .hasSize(2);
    }

    @Test
    void actualizarUsuarioNoExistenteDa404() {
        Usuario update = new Usuario();
        update.setNombre("Nuevo");
        webClient.put().uri("/api/usuarios/{correo}", "no@existe.com")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void eliminarUsuario() {
        Usuario u = new Usuario("C", "C", 22, LocalDateTime.now(), "c@c.com", "z");
        webClient.post().uri("/api/usuarios").contentType(MediaType.APPLICATION_JSON).bodyValue(u).exchange().expectStatus().isCreated();

        webClient.delete().uri("/api/usuarios/{correo}", "c@c.com")
                .exchange()
                .expectStatus().isNoContent();

        webClient.get().uri("/api/usuarios/{correo}", "c@c.com")
                .exchange()
                .expectStatus().isNotFound();
    }
}
