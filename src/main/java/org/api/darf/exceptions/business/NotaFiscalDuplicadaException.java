package org.api.darf.exceptions.business;

public class NotaFiscalDuplicadaException extends RuntimeException {
    public NotaFiscalDuplicadaException(String cnpj, Integer numeroNf, String ug) {
        super("Já existe um Documento Hábil cadastrado para o CNPJ " + cnpj + " com a nota fiscal nº " + numeroNf + " na UG " + ug + ".");
    }
}
