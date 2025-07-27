package org.api.darf.services.documento;

import org.api.darf.exceptions.business.NotaFiscalDuplicadaException;
import org.api.darf.exceptions.business.ValorBrutoInvalidoException;
import org.api.darf.models.DocumentoHabil;
import org.api.darf.repositories.DocumentoHabilRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Serviço responsável por validar as regras de negócio para criação ou atualização
 * de um Documento Hábil antes de sua persistência no sistema.
 */
@Service
public class DocumentoHabilValidatorService {

    private final DocumentoHabilRepository repository;

    public DocumentoHabilValidatorService(DocumentoHabilRepository repository) {
        this.repository = repository;
    }

    /**
     * Executa todas as validações de negócio aplicáveis a um Documento Hábil.
     *
     * Regras:
     * - O valor bruto informado deve ser maior que zero.
     * - Não pode haver duplicidade de NF para o mesmo CNPJ e UG.
     *
     * @param documento o DocumentoHabil que será validado
     * @throws ValorBrutoInvalidoException se o valor bruto for zero ou negativo
     * @throws NotaFiscalDuplicadaException se já existir documento com mesmo CNPJ, NF e UG
     */
    public void validarRegrasDeNegocio(DocumentoHabil documento) {
        // Regra 1: valor bruto deve ser maior que zero
        if (documento.getRetencao().getValorBruto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValorBrutoInvalidoException();
        }

        // Regra 2: não deve existir outra NF com o mesmo número, CNPJ e UG
        boolean duplicado = repository.findAll().stream()
                .anyMatch(dh ->
                        dh.getCnpj().equals(documento.getCnpj()) &&
                                dh.getNumeroNf().equals(documento.getNumeroNf()) &&
                                dh.getUg() == documento.getUg() &&
                                (documento.getId() == null || !dh.getId().equals(documento.getId())) // ignora a própria entidade se for atualização
                );

        if (duplicado) {
            throw new NotaFiscalDuplicadaException(
                    documento.getCnpj(),
                    documento.getNumeroNf(),
                    documento.getUg().name()
            );
        }
    }
}
