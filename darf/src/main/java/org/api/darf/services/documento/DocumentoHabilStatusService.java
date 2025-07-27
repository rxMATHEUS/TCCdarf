package org.api.darf.services.documento;

import org.api.darf.models.DocumentoHabil;
import org.api.darf.models.enums.StatusDocumento;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável por atualizar o status de um Documento Hábil
 * com base na presença ou ausência da data de pagamento.
 */
@Service
public class DocumentoHabilStatusService {

    /**
     * Atualiza o status do documento:
     * - Se possuir data de pagamento, será marcado como PAGO.
     * - Caso contrário, será marcado como LIQUIDADO (valor reconhecido, mas não pago).
     *
     * @param documento DocumentoHabil a ser avaliado e atualizado
     */
    public void atualizarStatus(DocumentoHabil documento) {
        if (documento.getDataPagamento() != null) {
            documento.setStatus(StatusDocumento.PAGO);
        } else {
            documento.setStatus(StatusDocumento.LIQUIDADO);
        }
    }
}
