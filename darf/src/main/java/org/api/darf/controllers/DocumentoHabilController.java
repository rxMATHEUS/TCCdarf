package org.api.darf.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.api.darf.dtos.documento.DocumentoHabilRequestDTO;
import org.api.darf.dtos.documento.DocumentoHabilResponseDTO;
import org.api.darf.mappers.DocumentoHabilMapper;
import org.api.darf.models.DocumentoHabil;
import org.api.darf.models.enums.UG;
import org.api.darf.services.DocumentoHabilFacade;
import org.api.darf.services.documento.DocumentoHabilStatusService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Controlador REST para gerenciar os endpoints relacionados ao Documento Hábil.
 * Expõe operações de criação, consulta, atualização, exclusão e filtros com paginação.
 */
@RestController
@RequestMapping("/documentos")
@RequiredArgsConstructor
public class DocumentoHabilController {

    private final DocumentoHabilFacade service;
    private final DocumentoHabilMapper mapper;
    private final DocumentoHabilStatusService statusService;

    /**
     * Cria um novo Documento Hábil.
     * @param dto dados de entrada validados.
     * @return resposta com DTO da entidade criada e URI do novo recurso.
     */
    @PostMapping
    public ResponseEntity<DocumentoHabilResponseDTO> criar(@Valid @RequestBody DocumentoHabilRequestDTO dto) {
        DocumentoHabil entidade = mapper.toEntity(dto);
        DocumentoHabil salvo = service.criar(entidade);
        DocumentoHabilResponseDTO resposta = mapper.toResponseDTO(salvo);

        return ResponseEntity.created(URI.create("/documentos/" + salvo.getId())).body(resposta);
    }

    /**
     * Busca um documento pelo seu ID.
     * @param id identificador do documento.
     * @return DTO do documento encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DocumentoHabilResponseDTO> buscarPorId(@PathVariable Long id) {
        DocumentoHabil encontrado = service.buscarPorId(id);
        return ResponseEntity.ok(mapper.toResponseDTO(encontrado));
    }

    /**
     * Lista todos os documentos com suporte a paginação.
     * @param pageable informações de paginação (tamanho, ordenação).
     * @return página de documentos.
     */
    @GetMapping
    public ResponseEntity<Page<DocumentoHabilResponseDTO>> listarTodos(
            @PageableDefault(size = 10, sort = "dataNf", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<DocumentoHabil> pagina = service.listarTodos(pageable);
        Page<DocumentoHabilResponseDTO> resposta = pagina.map(mapper::toResponseDTO);
        return ResponseEntity.ok(resposta);
    }

    /**
     * Atualiza um documento existente com base no ID.
     * @param id identificador do documento a ser atualizado.
     * @param dto dados novos para o documento.
     * @return DTO atualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DocumentoHabilResponseDTO> atualizar(@PathVariable Long id,
                                                               @Valid @RequestBody DocumentoHabilRequestDTO dto) {
        DocumentoHabil atualizado = service.atualizar(id, mapper.toEntity(dto));
        return ResponseEntity.ok(mapper.toResponseDTO(atualizado));
    }

    /**
     * Deleta um documento com base no ID.
     * @param id identificador do documento.
     * @return resposta sem conteúdo.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Marca um documento como "Pago" atualizando a data de pagamento e o status.
     * @param id identificador do documento.
     * @return documento atualizado com status "Pago".
     */
    @PatchMapping("/{id}/pagar")
    public ResponseEntity<DocumentoHabilResponseDTO> marcarComoPago(@PathVariable Long id) {
        DocumentoHabil documento = service.buscarPorId(id);
        documento.setDataPagamento(LocalDate.now());
        statusService.atualizarStatus(documento);
        DocumentoHabil atualizado = service.atualizar(id, documento);

        return ResponseEntity.ok(mapper.toResponseDTO(atualizado));
    }

    /**
     * Filtra documentos por CNPJ e competência (mês/ano).
     * @param cnpj CNPJ da empresa.
     * @param competencia competência no formato YYYY-MM.
     * @param pageable paginação.
     * @return documentos filtrados.
     */
    @GetMapping("/filtrar")
    public ResponseEntity<Page<DocumentoHabilResponseDTO>> filtrarPorCnpjECompetencia(
            @RequestParam String cnpj,
            @RequestParam YearMonth competencia,
            @PageableDefault(size = 10, sort = "dataPagamento", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<DocumentoHabil> pagina = service.buscarPorCnpjEMes(cnpj, competencia, pageable);
        Page<DocumentoHabilResponseDTO> resposta = pagina.map(mapper::toResponseDTO);
        return ResponseEntity.ok(resposta);
    }

    /**
     * Lista os CNPJs que possuem documentos pagos em uma determinada competência e UG.
     * @param competencia competência no formato YYYY-MM.
     * @param ug unidade gestora.
     * @param pageable paginação.
     * @return lista paginada de CNPJs únicos.
     */
    @GetMapping("/cnpjs-pagos")
    public ResponseEntity<Page<String>> listarCnpjsComDocumentosPagosNoMesEUg(
            @RequestParam YearMonth competencia,
            @RequestParam UG ug,
            @PageableDefault(size = 10, sort = "cnpj", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<String> paginaCnpjs = service.listarCnpjsComPagamentoEmMesEUg(competencia, ug, pageable);
        return ResponseEntity.ok(paginaCnpjs);
    }
}
