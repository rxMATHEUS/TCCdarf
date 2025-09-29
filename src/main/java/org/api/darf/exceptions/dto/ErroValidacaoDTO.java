package org.api.darf.exceptions.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErroValidacaoDTO {
    private String campo;
    private String mensagem;
}
