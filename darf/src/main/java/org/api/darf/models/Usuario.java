// Define o pacote da aplicação
package org.api.darf.models;

// Importações para validação e persistência
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

// Define que esta classe representa uma entidade do banco de dados
@Entity
// Lombok - Gera automaticamente os métodos get
@Getter
// Lombok - Gera automaticamente os métodos set
@Setter
// Lombok - Gera um construtor sem argumentos
@NoArgsConstructor
public class Usuario {

    // CPF será a chave primária do usuário
    @Id
    // Validação: deve conter exatamente 11 dígitos numéricos
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter exatamente 11 dígitos")
    // Define as características da coluna: tamanho 11, não pode ser nula e deve ser única
    @Column(length = 11, nullable = false, unique = true)
    private String cpf;

    // A senha não pode estar em branco (validação de formulário)
    @NotBlank(message = "Senha não pode estar em branco")
    // Define que a coluna não pode ser nula no banco de dados
    @Column(nullable = false)
    private String senha;

    // Construtor customizado: recebe CPF e senha em texto plano,
    // criptografa a senha antes de armazenar no objeto
    public Usuario(String cpf, String senha) {
        this.cpf = cpf;
        this.senha = new BCryptPasswordEncoder().encode(senha);  // Criptografia da senha com BCrypt
    }

    // Método para validar se uma senha em texto plano confere com a senha criptografada armazenada
    public boolean validarSenha(String senha) {
        return new BCryptPasswordEncoder().matches(senha, this.senha);
    }
}
