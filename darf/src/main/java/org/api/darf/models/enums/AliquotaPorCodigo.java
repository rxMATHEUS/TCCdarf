package org.api.darf.models.enums;

import lombok.Getter;
import org.api.darf.exceptions.business.CodigoRfbInvalidoException;

import java.math.BigDecimal;
import java.util.Arrays;

@Getter
public enum AliquotaPorCodigo {

    // Definindo as alíquotas para os códigos específicos, com valores de IR, CSLL, Cofins e PIS
    // Cada código é associado a alíquotas específicas para cada imposto.
    COD_6147("6147", new BigDecimal("1.20"), new BigDecimal("1.00"), new BigDecimal("3.00"), new BigDecimal("0.65")),
    COD_9060("9060", new BigDecimal("0.24"), new BigDecimal("1.00"), new BigDecimal("3.00"), new BigDecimal("0.65")),
    COD_8739("8739", new BigDecimal("0.24"), new BigDecimal("1.00"), BigDecimal.ZERO, BigDecimal.ZERO),
    COD_8767("8767", new BigDecimal("1.20"), new BigDecimal("1.00"), BigDecimal.ZERO, BigDecimal.ZERO),
    COD_8850("8850", new BigDecimal("2.40"), new BigDecimal("1.00"), new BigDecimal("3.00"), new BigDecimal("0.65")),
    COD_8863("8863", BigDecimal.ZERO, new BigDecimal("1.00"), new BigDecimal("3.00"), new BigDecimal("0.65")),
    COD_6188("6188", new BigDecimal("2.40"), new BigDecimal("1.00"), new BigDecimal("3.00"), new BigDecimal("0.65")),
    COD_6190("6190", new BigDecimal("4.80"), new BigDecimal("1.00"), new BigDecimal("3.00"), new BigDecimal("0.65"));

    // Atributos que armazenam as alíquotas para cada tipo de imposto (IR, CSLL, Cofins, PIS)
    private final String codigo;
    private final BigDecimal ir;
    private final BigDecimal csll;
    private final BigDecimal cofins;
    private final BigDecimal pis;

    // Construtor da enumeração para atribuir os valores das alíquotas.
    AliquotaPorCodigo(String codigo, BigDecimal ir, BigDecimal csll, BigDecimal cofins, BigDecimal pis) {
        this.codigo = codigo;
        this.ir = ir;
        this.csll = csll;
        this.cofins = cofins;
        this.pis = pis;
    }

    // Método que retorna a enumeração correspondente ao código ou um valor padrão se o código não for encontrado.
    public static AliquotaPorCodigo from(String codigo) {
        return Arrays.stream(values())
                .filter(a -> a.codigo.equals(codigo))
                .findFirst()
                .orElseThrow(() -> new CodigoRfbInvalidoException(codigo));
    }
}
