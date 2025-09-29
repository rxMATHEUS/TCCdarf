package org.api.darf.exceptions.business;

public class DataPagamentoInvalidaException extends RuntimeException {

    public DataPagamentoInvalidaException() {
        super("A data de pagamento não pode ser anterior à data da Nota Fiscal.");
    }
}