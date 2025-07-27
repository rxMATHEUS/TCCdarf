package org.api.darf.dtos.documento;

import jakarta.validation.constraints.*;
import lombok.*;
import org.api.darf.dtos.retencao.RetencaoDTO;
import org.api.darf.models.enums.NaturezaRendimento;
import org.api.darf.models.enums.StatusDocumento;
import org.api.darf.models.enums.UG;
import org.hibernate.validator.constraints.br.CNPJ;

import java.time.LocalDate;

/**
 * DTO utilizado para receber os dados de entrada da API referentes ao Documento Hábil.
 * Esse objeto é validado antes de ser transformado em uma entidade para persistência.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoHabilRequestDTO {

    /**
     * Unidade Gestora responsável pelo documento.
     * Não pode ser nulo.
     */
    @NotNull
    private UG ug;

    /**
     * Número do Documento Hábil no padrão "202X YY XXXXXX".
     * Validado por expressão regular.
     */
    @Pattern(regexp = "^202\\d[A-Za-z]{2}\\d{6}$", message = "O número do documento hábil deve estar no formato: 202X YY XXXXXX")
    private String numeroDh;

    /**
     * CNPJ da empresa relacionada ao documento.
     * Validação específica para formato de CNPJ.
     */
    @CNPJ(message = "CNPJ inválido")
    private String cnpj;

    /**
     * Número da Nota Fiscal associada ao documento.
     * Campo obrigatório.
     */
    @NotNull
    private Integer numeroNf;

    /**
     * Data de emissão da nota fiscal.
     * Campo obrigatório.
     */
    @NotNull
    private LocalDate dataNf;

    /**
     * Data de pagamento do documento.
     * Pode ser nula no momento da criação.
     */
    private LocalDate dataPagamento;

    /**
     * Status atual do documento (ex: LIQUIDADO ou PAGO).
     * Opcional no momento da criação; pode ser atribuído automaticamente.
     */
    private StatusDocumento status;

    /**
     * Natureza do rendimento conforme tabela da Receita Federal.
     * Campo obrigatório.
     */
    @NotNull
    private NaturezaRendimento naturezaRendimento;

    /**
     * Informações de retenção fiscal associadas ao documento.
     * Campo obrigatório e validado como objeto aninhado.
     */
    @NotNull
    private RetencaoDTO retencao;
}
