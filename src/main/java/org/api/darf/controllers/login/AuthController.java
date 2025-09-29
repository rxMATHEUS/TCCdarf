package org.api.darf.controllers.login;

import lombok.RequiredArgsConstructor;
import org.api.darf.dtos.login.LoginRequestDTO;
import org.api.darf.dtos.login.RegisterRequestDTO;
import org.api.darf.dtos.login.ResponseDTO;
import org.api.darf.infra.security.TokenService;
import org.api.darf.models.User;
import org.api.darf.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controlador responsável pela autenticação e registro de usuários.
 * Expõe endpoints para login e cadastro, gerando tokens JWT para autenticação.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.PATCH}, allowCredentials = "true" )
public class AuthController {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    /**
     * Realiza autenticação do usuário via CPF e senha.
     * Se autenticado, retorna token JWT e nome do usuário.
     * @param body DTO contendo CPF e senha
     * @return ResponseDTO com nome e token, ou erro 400 se credenciais inválidas
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody LoginRequestDTO body){
        // Busca usuário pelo CPF
        User user = this.repository.findByCpf(body.cpf()).orElseThrow(() -> new RuntimeException("User not found"));
        // Verifica se a senha informada corresponde à senha armazenada
        if(passwordEncoder.matches(body.password(), user.getPassword())) {
            // Gera token JWT para autenticação
            String token = this.tokenService.generateToken(user);
            return ResponseEntity.ok(new ResponseDTO(user.getName(), token));
        }
        // Retorna erro caso senha não corresponda
        return ResponseEntity.badRequest().build();
    }

    /**
     * Realiza o cadastro de um novo usuário.
     * Se o CPF não estiver cadastrado, salva o usuário e retorna token JWT.
     * @param body DTO contendo nome, CPF e senha
     * @return ResponseDTO com nome e token, ou nada se CPF já cadastrado
     */
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@RequestBody RegisterRequestDTO body){
        Optional<User> user = this.repository.findByCpf(body.cpf());

        if(user.isEmpty()) {
            // Cria novo usuário e codifica a senha
            User newUser = new User();
            newUser.setPassword(passwordEncoder.encode(body.password()));
            newUser.setCpf(body.cpf());
            newUser.setName(body.name());
            this.repository.save(newUser);

            // Gera token JWT para o novo usuário
            String token = this.tokenService.generateToken(newUser);
            return ResponseEntity.ok(new ResponseDTO(newUser.getName(), token));
        }
        // Retorna erro 400 se CPF já estiver cadastrado
        return ResponseEntity.badRequest().build();
    }
}
