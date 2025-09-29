package org.api.darf.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.api.darf.models.enums.AliquotaPorCodigo;

import java.math.BigDecimal;

@Entity // Entidade JPA representando os dados de retenção fiscal associados a um Documento Hábil
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Retencao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Geração automática do ID
    private Long id;

    @NotNull
    @Column(nullable = false, length = 4)
    private String codigoRfb; // Código da Receita Federal utilizado para identificar o tipo de retenção

    // Alíquotas aplicáveis ao valor bruto, extraídas com base no código RFB
    @Column(precision = 5, scale = 2)
    private BigDecimal aliquotaIr = BigDecimal.ZERO;

    @Column(precision = 5, scale = 2)
    private BigDecimal aliquotaCsll = BigDecimal.ZERO;

    @Column(precision = 5, scale = 2)
    private BigDecimal aliquotaCofins = BigDecimal.ZERO;

    @Column(precision = 5, scale = 2)
    private BigDecimal aliquotaPisPasep = BigDecimal.ZERO;

    @NotNull
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorBruto; // Valor total sobre o qual incidirão as retenções

    // Valores efetivamente retidos com base nas alíquotas e no valor bruto
    @Column(precision = 15, scale = 2)
    private BigDecimal retidoIr = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal retidoCsll = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal retidoCofins = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal retidoPis = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal valorLiquido = BigDecimal.ZERO; // Valor líquido após descontos de retenções

    /**
     * Define as alíquotas dos tributos com base no código RFB informado.
     * Lança exceção se o código for inválido (validação tratada no enum).
     */
    public void setarAliquotasPorCodigoRfb(String codigoRfb) {
        this.codigoRfb = codigoRfb;
        AliquotaPorCodigo aliquota = AliquotaPorCodigo.from(codigoRfb);

        this.aliquotaIr = aliquota.getIr();
        this.aliquotaCsll = aliquota.getCsll();
        this.aliquotaCofins = aliquota.getCofins();
        this.aliquotaPisPasep = aliquota.getPis();
    }
}
