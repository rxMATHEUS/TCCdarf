package org.api.darf.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum que representa as Unidades Gestoras (UG) com seus respectivos números.
 * Cada UG é identificada por um número único, representado por um valor inteiro.
 */
@Getter
@AllArgsConstructor
public enum UG {

    // UG Primária com o número 160147
    PRIMARIA(160147),

    // UG Secundária com o número 167147
    SECUNDARIA(167147);

    // Atributo que armazena o número da UG
    private final Integer numeroUG;
}
