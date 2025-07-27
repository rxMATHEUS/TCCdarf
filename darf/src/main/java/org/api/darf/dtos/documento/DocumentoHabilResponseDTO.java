package org.api.darf.dtos.documento;

import lombok.*;
import org.api.darf.dtos.retencao.RetencaoDTO;
import org.api.darf.models.enums.NaturezaRendimento;
import org.api.darf.models.enums.StatusDocumento;
import org.api.darf.models.enums.UG;

import java.time.LocalDate;

/**
 * DTO utilizado para retornar informações de um Documento Hábil para o cliente.
 * Esse objeto é usado como resposta da API, ocultando detalhes internos da entidade.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoHabilResponseDTO {

    /**
     * Identificador único do documento.
     */
    private Long id;

    /**
     * Unidade Gestora responsável pelo documento.
     */
    private UG ug;

    /**
     * Número do Documento Hábil no padrão validado (ex: 2025AB123456).
     */
    private String numeroDh;

    /**
     * CNPJ da empresa relacionada ao documento.
     */
    private String cnpj;

    /**
     * Número da Nota Fiscal associada ao documento.
     */
    private Integer numeroNf;

    /**
     * Data de emissão da Nota Fiscal.
     */
    private LocalDate dataNf;

    /**
     * Data de pagamento do documento, se houver.
     */
    private LocalDate dataPagamento;

    /**
     * Status atual do documento (ex: LIQUIDADO ou PAGO).
     */
    private StatusDocumento status;

    /**
     * Natureza do rendimento associada ao tipo de serviço/produto.
     */
    private NaturezaRendimento naturezaRendimento;

    /**
     * Informações de retenção fiscal calculadas ou atribuídas ao documento.
     */
    private RetencaoDTO retencao;
}
