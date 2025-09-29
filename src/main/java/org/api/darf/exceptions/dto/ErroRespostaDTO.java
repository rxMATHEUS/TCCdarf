package org.api.darf.exceptions.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ErroRespostaDTO {
    private LocalDateTime timestamp;
    private int status;
    private String erro;
    private String mensagem;
    private String path;
    private List<ErroValidacaoDTO> validacoes; // pode ser null se não for erro de validação
}
