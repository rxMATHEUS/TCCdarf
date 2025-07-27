package org.api.darf.dtos.retencao;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO que representa os dados relacionados à retenção tributária de um Documento Hábil.
 * Este objeto é usado tanto para entrada (Request) quanto saída (Response) da API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RetencaoDTO {

    /**
     * Código da Receita Federal (RFB) que determina as alíquotas a serem aplicadas.
     * Exemplo: "6190"
     */
    @NotBlank
    private String codigoRfb;

    /**
     * Valor bruto sobre o qual as retenções serão calculadas.
     * Deve ser positivo e não nulo.
     */
    @NotNull
    private BigDecimal valorBruto;

    // Alíquotas aplicadas (em percentual) conforme o código RFB

    /**
     * Alíquota de Imposto de Renda (IR) correspondente ao código RFB.
     */
    private BigDecimal aliquotaIr;

    /**
     * Alíquota de Contribuição Social sobre o Lucro Líquido (CSLL).
     */
    private BigDecimal aliquotaCsll;

    /**
     * Alíquota de Cofins.
     */
    private BigDecimal aliquotaCofins;

    /**
     * Alíquota de PIS/Pasep.
     */
    private BigDecimal aliquotaPisPasep;

    // Valores efetivamente retidos

    /**
     * Valor retido de IR com base no valor bruto e na alíquota.
     */
    private BigDecimal retidoIr;

    /**
     * Valor retido de CSLL.
     */
    private BigDecimal retidoCsll;

    /**
     * Valor retido de Cofins.
     */
    private BigDecimal retidoCofins;

    /**
     * Valor retido de PIS/Pasep.
     */
    private BigDecimal retidoPis;

    /**
     * Valor líquido a receber, calculado como valor bruto menos todas as retenções.
     */
    private BigDecimal valorLiquido;
}
