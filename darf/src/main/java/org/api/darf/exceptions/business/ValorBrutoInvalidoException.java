package org.api.darf.exceptions.business;

public class ValorBrutoInvalidoException extends RuntimeException {

    public ValorBrutoInvalidoException() {
        super("O valor bruto deve ser maior que zero.");
    }
}
