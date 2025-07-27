package org.api.darf.exceptions.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.api.darf.exceptions.business.CodigoRfbInvalidoException;
import org.api.darf.exceptions.business.NotaFiscalDuplicadaException;
import org.api.darf.exceptions.business.ValorBrutoInvalidoException;
import org.api.darf.exceptions.dto.ErroRespostaDTO;
import org.api.darf.exceptions.dto.ErroValidacaoDTO;
import org.api.darf.exceptions.notfound.EntidadeNaoEncontradaException;
import org.api.darf.exceptions.notfound.NenhumDocumentoEncontradoException;
import org.api.darf.exceptions.notfound.NenhumDocumentoPagoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<ErroRespostaDTO> tratarNaoEncontrado(EntidadeNaoEncontradaException ex, HttpServletRequest request) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Entidade não encontrada",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroRespostaDTO> tratarValidacao(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ErroValidacaoDTO> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new ErroValidacaoDTO(e.getField(), e.getDefaultMessage()))
                .collect(Collectors.toList());

        ErroRespostaDTO erro = new ErroRespostaDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação",
                "Um ou mais campos estão inválidos.",
                request.getRequestURI(),
                erros
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroRespostaDTO> tratarOutros(Exception ex, HttpServletRequest request) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno",
                "Ocorreu um erro inesperado. Tente novamente mais tarde.",
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErroRespostaDTO> tratarViolacaoConstraint(ConstraintViolationException ex, HttpServletRequest request) {
        List<ErroValidacaoDTO> erros = ex.getConstraintViolations().stream()
                .map(cv -> new ErroValidacaoDTO(
                        cv.getPropertyPath().toString(),
                        cv.getMessage()))
                .collect(Collectors.toList());

        ErroRespostaDTO erro = new ErroRespostaDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação",
                "Parâmetros inválidos na requisição.",
                request.getRequestURI(),
                erros
        );
        return ResponseEntity.badRequest().body(erro);
    }

    @ExceptionHandler(NenhumDocumentoEncontradoException.class)
    public ResponseEntity<ErroRespostaDTO> tratarNenhumDocumentoEncontrado(NenhumDocumentoEncontradoException ex, HttpServletRequest request) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Nenhum Documento Hábil encontrado",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(NenhumDocumentoPagoEncontradoException.class)
    public ResponseEntity<ErroRespostaDTO> tratarNenhumDocumentoPagoEncontrado(NenhumDocumentoPagoEncontradoException ex, HttpServletRequest request) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Nenhum Documento Hábil PAGO encontrado",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }


    @ExceptionHandler(CodigoRfbInvalidoException.class)
    public ResponseEntity<ErroRespostaDTO> tratarCodigoRfbInvalido(CodigoRfbInvalidoException ex, HttpServletRequest request) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Código RFB inválido",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(NotaFiscalDuplicadaException.class)
    public ResponseEntity<ErroRespostaDTO> tratarNotaFiscalDuplicada(NotaFiscalDuplicadaException ex, HttpServletRequest request) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(), // 409
                "Nota Fiscal já cadastrada",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }


    @ExceptionHandler(ValorBrutoInvalidoException.class)
    public ResponseEntity<ErroRespostaDTO> tratarValorBrutoInvalido(ValorBrutoInvalidoException ex, HttpServletRequest request) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Valor bruto inválido",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.badRequest().body(erro);
    }


}
