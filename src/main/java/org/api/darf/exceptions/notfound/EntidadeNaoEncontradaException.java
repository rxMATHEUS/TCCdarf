package org.api.darf.exceptions.notfound;

public class EntidadeNaoEncontradaException extends RuntimeException {

    public EntidadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
