package org.api.darf.controllers;

import org.api.darf.dtos.login.LoginRequestDTO;
import org.api.darf.dtos.login.LoginResponseDTO;
import org.api.darf.models.Usuario;
import org.api.darf.repositories.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginDto) {
        Usuario usuario = usuarioRepository.findById(loginDto.getCpf())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        if (!usuario.validarSenha(loginDto.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponseDTO("Senha inválida", null));
        }

        return ResponseEntity.ok(new LoginResponseDTO("Login bem-sucedido", usuario.getCpf()));
    }
}