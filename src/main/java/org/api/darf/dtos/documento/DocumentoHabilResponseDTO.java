package org.api.darf.dtos.documento;

import org.api.darf.dtos.retencao.RetencaoDTO;
import org.api.darf.models.enums.NaturezaRendimento;
import org.api.darf.models.enums.StatusDocumento;
import org.api.darf.models.enums.UG;

import java.time.LocalDate;

/**
 * DTO utilizado para retornar informações de um Documento Hábil para o cliente.
 * Esse objeto é usado como resposta da API, ocultando detalhes internos da entidade.
 */

public record DocumentoHabilResponseDTO (Long id, UG ug, String cnpj, String numeroDh, Integer numeroNf, LocalDate dataNf, LocalDate dataPagamento,StatusDocumento status, NaturezaRendimento naturezaRendimento, RetencaoDTO retencao){


}
