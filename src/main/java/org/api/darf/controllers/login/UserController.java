package org.api.darf.controllers.login;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador responsável por operações relacionadas ao usuário autenticado.
 * Atualmente expõe endpoint para teste de autenticação.
 */
@RestController
@RequestMapping("/user")
public class UserController {
    /**
     * Endpoint para verificar se o usuário está autenticado.
     * @return mensagem de sucesso
     */
    @GetMapping
    public ResponseEntity<String> getUser(){
        return ResponseEntity.ok("sucesso!");
    }
}
