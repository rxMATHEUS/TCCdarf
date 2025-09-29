package org.api.darf.exceptions.auth;

/**
 * Exceção lançada quando se tenta registrar um usuário que já existe.
 */
public class UsuarioJaExisteException extends RuntimeException {

    public UsuarioJaExisteException(String cpf) {
        super("Já existe um usuário cadastrado com o CPF: " + cpf);
    }

    public UsuarioJaExisteException(String message, Throwable cause) {
        super(message, cause);
    }
}

