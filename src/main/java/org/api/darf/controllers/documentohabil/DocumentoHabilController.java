package org.api.darf.controllers.documentohabil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.api.darf.dtos.documento.DocumentoHabilRequestDTO;
import org.api.darf.dtos.documento.DocumentoHabilResponseDTO;
import org.api.darf.mappers.DocumentoHabilMapper;
import org.api.darf.models.DocumentoHabil;
import org.api.darf.models.enums.StatusDocumento;
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
import java.util.Map;

/**
 * Controlador REST para gerenciar os endpoints relacionados ao Documento Hábil.
 * Expõe operações de criação, consulta, atualização, exclusão e filtros com paginação.
 */
@RestController
@RequestMapping("/documentos")
@RequiredArgsConstructor
@CrossOrigin
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
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<DocumentoHabilResponseDTO> buscarPorId(@PathVariable Long id) {
        DocumentoHabil encontrado = service.buscarPorId(id);
        return ResponseEntity.ok(mapper.toResponseDTO(encontrado));
    }

    /**
     * Lista todos os documentos com suporte a paginação e filtro por status.
     * @param status filtro opcional por status do documento.
     * @param pageable informações de paginação (tamanho, ordenação).
     * @return página de documentos.
     */
    @GetMapping
    public ResponseEntity<Page<DocumentoHabilResponseDTO>> listarTodos(
            @RequestParam(required = false) StatusDocumento status,
            @PageableDefault(size = 10, sort = "dataNf", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<DocumentoHabil> pagina;
        if (status != null) {
            pagina = service.listarPorStatus(status, pageable);
        } else {
            pagina = service.listarTodos(pageable);
        }
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
     * Filtra documentos com múltiplos critérios opcionais.
     * Endpoint flexível que diferencia entre data da nota fiscal e data de pagamento.
     * @param cnpj CNPJ da empresa (opcional).
     * @param competencia competência no formato YYYY-MM (opcional) - usa data da NF.
     * @param status status do documento: PAGO ou LIQUIDADO (opcional).
     * @param ug unidade gestora: PRIMARIA ou SECUNDARIA (opcional).
     * @param anoNf ano da nota fiscal (opcional).
     * @param mesNf mês da nota fiscal 1-12 (opcional).
     * @param anoPagamento ano do pagamento (opcional).
     * @param mesPagamento mês do pagamento 1-12 (opcional).
     * @param pageable paginação.
     * @return documentos filtrados.
     */
    @GetMapping("/filtrar")
    public ResponseEntity<Page<DocumentoHabilResponseDTO>> filtrarDocumentos(
            @RequestParam(required = false) String cnpj,
            @RequestParam(required = false) YearMonth competencia,
            @RequestParam(required = false) StatusDocumento status,
            @RequestParam(required = false) UG ug,
            @RequestParam(required = false) Integer anoNf,
            @RequestParam(required = false) Integer mesNf,
            @RequestParam(required = false) Integer anoPagamento,
            @RequestParam(required = false) Integer mesPagamento,
            @PageableDefault(size = 10, sort = "dataNf", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        try {
            Page<DocumentoHabil> pagina = service.filtrarDocumentos(cnpj, competencia, status, ug,
                    anoNf, mesNf, anoPagamento, mesPagamento, pageable);
            Page<DocumentoHabilResponseDTO> resposta = pagina.map(mapper::toResponseDTO);
            return ResponseEntity.ok(resposta);
        } catch (IllegalArgumentException e) {
            // Retorna 400 Bad Request para parâmetros inválidos
            throw new IllegalArgumentException("Parâmetros inválidos: " + e.getMessage());
        }
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

    /**
     * Retorna dados agregados flexíveis - diferencia entre data da NF e data de pagamento.
     * @param anoNf ano da nota fiscal (opcional).
     * @param mesNf mês da nota fiscal (opcional).
     * @param anoPagamento ano do pagamento (opcional).
     * @param mesPagamento mês do pagamento (opcional).
     * @param ug tipo de UG específica (opcional - se não informado, retorna ambas).
     * @return dados agregados conforme parâmetros.
     */
    @GetMapping("/agregados")
    public ResponseEntity<Map<String, Object>> obterDadosAgregados(
            @RequestParam(required = false) Integer anoNf,
            @RequestParam(required = false) Integer mesNf,
            @RequestParam(required = false) Integer anoPagamento,
            @RequestParam(required = false) Integer mesPagamento,
            @RequestParam(required = false) UG ug
    ) {
        try {
            Map<String, Object> dadosAgregados = service.obterDadosAgregadosFlexivel(
                    anoNf, mesNf, anoPagamento, mesPagamento, ug);
            return ResponseEntity.ok(dadosAgregados);
        } catch (IllegalArgumentException e) {
            // Retorna 400 Bad Request para parâmetros inválidos
            throw new IllegalArgumentException("Parâmetros inválidos: " + e.getMessage());
        }
    }

    /**
     * Endpoint para pesquisar Documentos Hábeis (DH) com filtros opcionais.
     * Permite buscar por número DH, número NF, código RFB, natureza de rendimento, UG, CNPJ e status de pagamento.
     * @param numeroDh número do documento hábil (opcional).
     * @param numeroNf número da nota fiscal (opcional).
     * @param codigoRfb código da Receita Federal (opcional).
     * @param naturezaRendimento natureza de rendimento (opcional).
     * @param ug unidade gestora (opcional).
     * @param cnpj CNPJ do prestador (opcional).
     * @param status status do pagamento (opcional).
     * @param pageable informações de paginação.
     * @return página de documentos que atendem aos critérios de pesquisa.
     */
    @GetMapping("/pesquisar")
    public ResponseEntity<Page<DocumentoHabilResponseDTO>> pesquisarDH(
            @RequestParam(required = false) String numeroDh,
            @RequestParam(required = false) Integer numeroNf,
            @RequestParam(required = false) String codigoRfb,
            @RequestParam(required = false) String naturezaRendimento,
            @RequestParam(required = false) UG ug,
            @RequestParam(required = false) String cnpj,
            @RequestParam(required = false) StatusDocumento status,
            @PageableDefault(size = 10, sort = "dataNf", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        try {
            Page<DocumentoHabil> pagina = service.pesquisarDH(
                    numeroDh, numeroNf, codigoRfb, naturezaRendimento, ug, cnpj, status, pageable);
            Page<DocumentoHabilResponseDTO> resposta = pagina.map(mapper::toResponseDTO);
            return ResponseEntity.ok(resposta);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Parâmetros inválidos: " + e.getMessage());
        }
    }

    /**
     * Busca um documento pelo número DH.
     * @param numeroDh número do documento hábil
     * @return DTO do documento encontrado
     */
    @GetMapping("/numero-dh/{numeroDh}")
    public ResponseEntity<DocumentoHabilResponseDTO> buscarPorNumeroDh(@PathVariable String numeroDh) {
        DocumentoHabil encontrado = service.buscarPorNumeroDh(numeroDh);
        return ResponseEntity.ok(mapper.toResponseDTO(encontrado));
    }

    /**
     * Busca um documento pelo número DH via query parameter.
     * Ex: GET /documentos/buscar?numeroDh=2024AB123456
     * @param numeroDh número do documento hábil (obrigatório)
     * @return DTO do documento encontrado
     */
    @GetMapping("/buscar")
    public ResponseEntity<DocumentoHabilResponseDTO> buscarPorNumeroDhQuery(@RequestParam(required = false) String numeroDh) {
        if (numeroDh == null || numeroDh.isBlank()) {
            throw new IllegalArgumentException("Parâmetro 'numeroDh' é obrigatório e não pode estar vazio");
        }
        DocumentoHabil encontrado = service.buscarPorNumeroDh(numeroDh);
        return ResponseEntity.ok(mapper.toResponseDTO(encontrado));
    }

    /**
     * Autocomplete de Número DH.
     * Retorna lista (limitada) de documentos cujo número DH começa com o termo informado.
     * Ex: GET /documentos/autocomplete?term=2024A&limit=5
     * @param term prefixo a ser pesquisado (obrigatório)
     * @param ug filtro opcional por UG
     * @param limit quantidade máxima de registros (padrão 10, máx 50)
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<Page<DocumentoHabilResponseDTO>> autocompleteNumeroDh(
            @RequestParam String term,
            @RequestParam(required = false) UG ug,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Integer limit
    ) {
        Page<DocumentoHabil> pagina = service.autocompleteNumeroDh(term, ug, page, size, limit);
        Page<DocumentoHabilResponseDTO> resposta = pagina.map(mapper::toResponseDTO);
        return ResponseEntity.ok(resposta);
    }


}
