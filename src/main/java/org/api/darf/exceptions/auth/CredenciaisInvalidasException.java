package org.api.darf.exceptions.auth;

/**
 * Exceção lançada quando as credenciais de login são inválidas.
 */
public class CredenciaisInvalidasException extends RuntimeException {

    public CredenciaisInvalidasException() {
        super("Credenciais inválidas. Verifique CPF e senha.");
    }

    public CredenciaisInvalidasException(String message) {
        super(message);
    }
}

