package org.api.darf.exceptions.business;

public class CodigoRfbInvalidoException extends RuntimeException {

    public CodigoRfbInvalidoException(String codigo) {
        super("O código RFB informado é inválido: " + codigo);
    }
}
