package org.api.darf.services.retencao;

import org.api.darf.models.Retencao;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Serviço responsável por calcular os valores de retenção de impostos
 * com base no valor bruto e nas alíquotas associadas ao código RFB informado.
 */
@Service
public class RetencaoCalculatorService {

    /**
     * Calcula os valores de retenção (IR, CSLL, Cofins, PIS) e o valor líquido da retenção,
     * com base no valor bruto e nas alíquotas definidas pelo código RFB.
     *
     * @param retencao Objeto Retencao com valor bruto e código RFB preenchidos
     */
    public void calcularValoresRetencao(Retencao retencao) {
        // Define as alíquotas apropriadas com base no código RFB fornecido
        retencao.setarAliquotasPorCodigoRfb(retencao.getCodigoRfb());

        // Calcula os valores retidos para cada imposto
        retencao.setRetidoIr(calcularValorRetido(retencao.getValorBruto(), retencao.getAliquotaIr()));
        retencao.setRetidoCsll(calcularValorRetido(retencao.getValorBruto(), retencao.getAliquotaCsll()));
        retencao.setRetidoCofins(calcularValorRetido(retencao.getValorBruto(), retencao.getAliquotaCofins()));
        retencao.setRetidoPis(calcularValorRetido(retencao.getValorBruto(), retencao.getAliquotaPisPasep()));

        // Soma todos os valores retidos para obter o total de impostos
        BigDecimal totalRetido = retencao.getRetidoIr()
                .add(retencao.getRetidoCsll())
                .add(retencao.getRetidoCofins())
                .add(retencao.getRetidoPis());

        // Calcula o valor líquido após as retenções
        retencao.setValorLiquido(retencao.getValorBruto().subtract(totalRetido));
    }

    /**
     * Calcula o valor a ser retido com base no valor bruto e na alíquota.
     *
     * @param valorBruto Valor total da nota fiscal
     * @param aliquota Percentual de retenção para o imposto
     * @return Valor retido correspondente, ou ZERO se valores nulos
     */
    private BigDecimal calcularValorRetido(BigDecimal valorBruto, BigDecimal aliquota) {
        if (valorBruto == null || aliquota == null) {
            return BigDecimal.ZERO;
        }
        return valorBruto.multiply(aliquota).divide(BigDecimal.valueOf(100));
    }
}
