package org.api.darf.services.documento;

import org.api.darf.exceptions.business.DataPagamentoInvalidaException;
import org.api.darf.exceptions.business.NotaFiscalDuplicadaException;
import org.api.darf.exceptions.business.ValorBrutoInvalidoException;
import org.api.darf.exceptions.business.NumeroDhDuplicadoException;
import org.api.darf.models.DocumentoHabil;
import org.api.darf.models.enums.StatusDocumento;
import org.api.darf.repositories.DocumentoHabilRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

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
     * - Valor bruto informado deve ser maior que zero.
     * - Não pode haver duplicidade de NF para o mesmo CNPJ e UG.
     * - Não pode existir o mesmo numeroDh para a mesma UG.
     * - A data de pagamento não pode ser anterior à data da Nota Fiscal.
     * - Se possui data de pagamento, status é forçado para PAGO.
     */
    public void validarRegrasDeNegocio(DocumentoHabil documento) {
        // Regra 1: valor bruto deve ser maior que zero
        if (documento.getRetencao().getValorBruto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValorBrutoInvalidoException();
        }

        // Regra 2: não deve existir outra NF com o mesmo número, CNPJ e UG
        boolean nfDuplicada = repository.findAll().stream()
                .anyMatch(dh ->
                        dh.getCnpj().equals(documento.getCnpj()) &&
                                dh.getNumeroNf().equals(documento.getNumeroNf()) &&
                                dh.getUg() == documento.getUg() &&
                                (documento.getId() == null || !dh.getId().equals(documento.getId()))
                );
        if (nfDuplicada) {
            throw new NotaFiscalDuplicadaException(
                    documento.getCnpj(),
                    documento.getNumeroNf(),
                    documento.getUg().name()
            );
        }

        // Regra 3: numeroDh único por UG
        if (documento.getNumeroDh() != null && documento.getUg() != null) {
            String normalizado = documento.getNumeroDh().trim().toUpperCase();
            boolean existe;
            if (documento.getId() == null) {
                existe = repository.existsByNumeroDhIgnoreCaseAndUg(normalizado, documento.getUg());
            } else {
                existe = repository.existsByNumeroDhIgnoreCaseAndUgAndIdNot(normalizado, documento.getUg(), documento.getId());
            }
            if (existe) {
                throw new NumeroDhDuplicadoException(normalizado, documento.getUg().name(), List.of(documento.getUg().name()));
            }
            // aplica normalização na entidade
            documento.setNumeroDh(normalizado);
        }

        // Regra 4: data de pagamento não pode ser anterior à data da NF
        if (documento.getDataPagamento() != null &&
                documento.getDataPagamento().isBefore(documento.getDataNf())) {
            throw new DataPagamentoInvalidaException();
        }

        // Regra 5: se possui data de pagamento, o status é PAGO
        if (documento.getDataPagamento() != null) {
            documento.setStatus(StatusDocumento.PAGO);
        }
    }
}
