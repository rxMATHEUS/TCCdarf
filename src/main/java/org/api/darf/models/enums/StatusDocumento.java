package org.api.darf.models.enums;

/**
 * Enum que representa o status de um documento.
 * Ele pode estar em um dos dois estados possíveis: PAGO ou NÃO_PAGO.
 */
public enum StatusDocumento {

    // O status indica que o documento foi pago.
    PAGO,

    // O status indica que o documento não foi pago.
    LIQUIDADO
}
