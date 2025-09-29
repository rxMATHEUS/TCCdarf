package org.api.darf.dtos.documento;

import jakarta.validation.Valid;
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

public record DocumentoHabilRequestDTO(

        /**
         * Unidade Gestora responsável pelo documento.
         * Não pode ser nulo.
         */
        @NotNull UG ug,

        /**
         * Número do Documento Hábil no padrão "202XYYXXXXXX".
         * Validado por expressão regular.
         */

        @Pattern(regexp = "^202\\d[A-Za-z]{2}\\d{6}$",
                message = "O número do documento hábil deve estar no formato: 202XYYXXXXXX")
        String numeroDh,

        /**
         * CNPJ da empresa relacionada ao documento.
         * Validação específica para formato de CNPJ.
         */
        @CNPJ(message = "CNPJ inválido")
        String cnpj,

        /**
         * Número da Nota Fiscal associada ao documento.
         * Campo obrigatório.
         */
        @NotNull Integer numeroNf,

        /**
         * Data de emissão da nota fiscal.
         * Campo obrigatório.
         */
        @NotNull LocalDate dataNf,

        /**
         * Data de pagamento do documento.
         * Pode ser nula no momento da criação.
         */
        LocalDate dataPagamento,

        /**
         * Status atual do documento (ex: LIQUIDADO ou PAGO).
         * Opcional no momento da criação; pode ser atribuído automaticamente.
         */
        StatusDocumento status,

        /**
         * Natureza do rendimento conforme tabela da Receita Federal.
         * Campo obrigatório.
         */
        @NotNull NaturezaRendimento naturezaRendimento,

        /**
         * Informações de retenção fiscal associadas ao documento.
         * Campo obrigatório e validado como objeto aninhado.
         */
        @NotNull @Valid RetencaoDTO retencao
) {
}
