package org.api.darf.exceptions.auth;

/**
 * Exceção lançada quando um usuário não é encontrado no sistema.
 */
public class UsuarioNaoEncontradoException extends RuntimeException {

    public UsuarioNaoEncontradoException(String cpf) {
        super("Usuário não encontrado com CPF: " + cpf);
    }

    public UsuarioNaoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}

