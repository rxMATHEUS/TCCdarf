package org.api.darf.services;

import lombok.RequiredArgsConstructor;
import org.api.darf.exceptions.notfound.EntidadeNaoEncontradaException;
import org.api.darf.exceptions.notfound.NenhumDocumentoEncontradoException;
import org.api.darf.exceptions.notfound.NenhumDocumentoPagoEncontradoException;
import org.api.darf.models.DocumentoHabil;
import org.api.darf.models.enums.StatusDocumento;
import org.api.darf.models.enums.UG;
import org.api.darf.repositories.DocumentoHabilRepository;
import org.api.darf.services.documento.DocumentoHabilStatusService;
import org.api.darf.services.documento.DocumentoHabilValidatorService;
import org.api.darf.services.retencao.RetencaoCalculatorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe que centraliza a lógica de negócio relacionada ao Documento Hábil.
 * Atua como fachada para orquestrar validações, persistência e cálculos de retenções.
 */
@Service
@RequiredArgsConstructor
public class DocumentoHabilFacade {

    private final DocumentoHabilRepository repository;
    private final DocumentoHabilValidatorService validatorService;
    private final RetencaoCalculatorService retencaoCalculatorService;
    private final DocumentoHabilStatusService statusService;

    /**
     * Cria um novo documento hábil com validações e cálculo automático de retenções.
     */
    public DocumentoHabil criar(DocumentoHabil documento) {
        // Normaliza número DH antes de validar
        if (documento.getNumeroDh() != null) {
            documento.setNumeroDh(documento.getNumeroDh().trim().toUpperCase());
        }
        // Valida regras de negócio (inclui verificação de duplicidade numeroDh+UG)
        validatorService.validarRegrasDeNegocio(documento);
        prepararDocumento(documento);
        return repository.save(documento);
    }

    /**
     * Busca um documento por ID.
     */
    public DocumentoHabil buscarPorId(Long id) {
        DocumentoHabil documento = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Documento não encontrado com ID: " + id));
        statusService.atualizarStatus(documento);
        return documento;
    }

    /**
     * Lista todos os documentos com paginação.
     */
    public Page<DocumentoHabil> listarTodos(Pageable pageable) {
        return repository.findAll(pageable);
    }

    /**
     * Lista documentos filtrados por status.
     */
    public Page<DocumentoHabil> listarPorStatus(StatusDocumento status, Pageable pageable) {
        Page<DocumentoHabil> resultado = repository.findByStatus(status, pageable);

        if (resultado.isEmpty()) {
            throw new NenhumDocumentoEncontradoException("Nenhum documento encontrado com status: " + status);
        }

        return resultado;
    }

    /**
     * Atualiza um documento existente.
     */
    public DocumentoHabil atualizar(Long id, DocumentoHabil documentoAtualizado) {
        DocumentoHabil documentoExistente = buscarPorId(id);
        // Normaliza número DH antes de atualizar
        if (documentoAtualizado.getNumeroDh() != null) {
            documentoAtualizado.setNumeroDh(documentoAtualizado.getNumeroDh().trim().toUpperCase());
        }

        // Atualiza os campos permitidos
        documentoExistente.setUg(documentoAtualizado.getUg());
        documentoExistente.setNumeroDh(documentoAtualizado.getNumeroDh());
        documentoExistente.setCnpj(documentoAtualizado.getCnpj());
        documentoExistente.setNumeroNf(documentoAtualizado.getNumeroNf());
        documentoExistente.setDataNf(documentoAtualizado.getDataNf());
        documentoExistente.setDataPagamento(documentoAtualizado.getDataPagamento());
        documentoExistente.setNaturezaRendimento(documentoAtualizado.getNaturezaRendimento());
        documentoExistente.setRetencao(documentoAtualizado.getRetencao());

        // Valida e prepara o documento atualizado
        validatorService.validarRegrasDeNegocio(documentoExistente);
        prepararDocumento(documentoExistente);

        return repository.save(documentoExistente);
    }

    /**
     * Marca um documento como pago.
     */
    public DocumentoHabil marcarComoPago(Long id) {
        DocumentoHabil documento = buscarPorId(id);
        documento.setDataPagamento(java.time.LocalDate.now());
        statusService.atualizarStatus(documento);
        return repository.save(documento);
    }

    /**
     * Remove um documento.
     */
    public void deletar(Long id) {
        DocumentoHabil documento = buscarPorId(id);
        repository.delete(documento);
    }

    /**
     * Prepara o documento calculando retenções e definindo status.
     */
    private void prepararDocumento(DocumentoHabil documento) {
        // Calcula as retenções automaticamente
        retencaoCalculatorService.calcularValoresRetencao(documento.getRetencao());

        // Define status inicial como LIQUIDADO se não estiver definido
        if (documento.getStatus() == null) {
            documento.setStatus(StatusDocumento.LIQUIDADO);
        }
    }

    /**
     * Lista CNPJs que possuem documentos pagos em uma determinada competência e UG.
     */
    public Page<String> listarCnpjsComPagamentoEmMesEUg(YearMonth competencia, UG ug, Pageable pageable) {
        String competenciaStr = competencia.toString();

        Page<String> resultado = repository.findDistinctCnpjsByUgAndCompetencia(ug, competenciaStr, pageable);

        if (resultado.isEmpty()) {
            throw new NenhumDocumentoPagoEncontradoException(
                    String.format("Nenhum CNPJ com documentos pagos encontrado para %s na UG %s",
                            competencia, ug.name())
            );
        }

        return resultado;
    }

    /**
     * Busca documentos por ano, mês e UG.
     */
    public Page<DocumentoHabil> buscarPorAnoMesEUG(Integer ano, Integer mes, UG ug, Pageable pageable) {
        return repository.findByAnoMesEUG(ano, mes, ug, pageable);
    }

    /**
     * Filtra documentos com múltiplos critérios opcionais de forma flexível.
     * Diferencia entre data da nota fiscal e data de pagamento.
     */
    public Page<DocumentoHabil> filtrarDocumentos(String cnpj, YearMonth competencia, StatusDocumento status,
                                                  UG ug, Integer anoNf, Integer mesNf,
                                                  Integer anoPagamento, Integer mesPagamento, Pageable pageable) {
        // Validações de entrada
        if (mesNf != null && (mesNf < 1 || mesNf > 12)) {
            throw new IllegalArgumentException("Mês da nota fiscal deve estar entre 1 e 12");
        }

        if (mesPagamento != null && (mesPagamento < 1 || mesPagamento > 12)) {
            throw new IllegalArgumentException("Mês do pagamento deve estar entre 1 e 12");
        }

        if (anoNf != null && (anoNf < 2000 || anoNf > 2100)) {
            throw new IllegalArgumentException("Ano da nota fiscal deve estar entre 2000 e 2100");
        }

        if (anoPagamento != null && (anoPagamento < 2000 || anoPagamento > 2100)) {
            throw new IllegalArgumentException("Ano do pagamento deve estar entre 2000 e 2100");
        }

        // Processa competência se fornecida (usa data da NF)
        Integer anoComp = null;
        Integer mesComp = null;
        String competenciaStr = null;

        if (competencia != null) {
            anoComp = competencia.getYear();
            mesComp = competencia.getMonthValue();
            competenciaStr = competencia.toString();
        }

        // Se anoNf e mesNf foram fornecidos separadamente, eles têm prioridade sobre competencia
        if (anoNf != null && mesNf != null) {
            competenciaStr = null;
            anoComp = null;
            mesComp = null;
        }

        Page<DocumentoHabil> resultado = repository.findByMultipleCriteria(cnpj, status, ug,
                anoNf, mesNf, anoPagamento, mesPagamento, competenciaStr, anoComp, mesComp, pageable);

        // Verifica se encontrou resultados
        if (resultado.isEmpty()) {
            StringBuilder mensagem = new StringBuilder("Nenhum documento encontrado com os critérios: ");
            if (cnpj != null) mensagem.append("CNPJ=").append(cnpj).append(" ");
            if (status != null) mensagem.append("Status=").append(status).append(" ");
            if (ug != null) mensagem.append("UG=").append(ug).append(" ");
            if (anoNf != null) mensagem.append("Ano NF=").append(anoNf).append(" ");
            if (mesNf != null) mensagem.append("Mês NF=").append(mesNf).append(" ");
            if (anoPagamento != null) mensagem.append("Ano Pagamento=").append(anoPagamento).append(" ");
            if (mesPagamento != null) mensagem.append("Mês Pagamento=").append(mesPagamento).append(" ");
            if (competenciaStr != null) mensagem.append("Competência=").append(competenciaStr).append(" ");

            throw new NenhumDocumentoEncontradoException(mensagem.toString().trim());
        }

        return resultado;
    }

    /**
     * Obtém dados agregados de forma flexível.
     * Diferencia entre data da nota fiscal e data de pagamento.
     */
    public Map<String, Object> obterDadosAgregadosFlexivel(Integer anoNf, Integer mesNf,
                                                           Integer anoPagamento, Integer mesPagamento, UG ug) {
        // Validações de entrada
        if (mesNf != null && (mesNf < 1 || mesNf > 12)) {
            throw new IllegalArgumentException("Mês da nota fiscal deve estar entre 1 e 12");
        }

        if (mesPagamento != null && (mesPagamento < 1 || mesPagamento > 12)) {
            throw new IllegalArgumentException("Mês do pagamento deve estar entre 1 e 12");
        }

        if (anoNf != null && (anoNf < 2000 || anoNf > 2100)) {
            throw new IllegalArgumentException("Ano da nota fiscal deve estar entre 2000 e 2100");
        }

        if (anoPagamento != null && (anoPagamento < 2000 || anoPagamento > 2100)) {
            throw new IllegalArgumentException("Ano do pagamento deve estar entre 2000 e 2100");
        }

        Map<String, Object> resultado = new HashMap<>();

        // Adiciona informações sobre os filtros aplicados
        if (anoNf != null) resultado.put("anoNf", anoNf);
        if (mesNf != null) {
            resultado.put("mesNf", mesNf);
            resultado.put("nomeMesNf", obterNomeMes(mesNf));
        }
        if (anoPagamento != null) resultado.put("anoPagamento", anoPagamento);
        if (mesPagamento != null) {
            resultado.put("mesPagamento", mesPagamento);
            resultado.put("nomeMesPagamento", obterNomeMes(mesPagamento));
        }

        if (ug != null) {
            // Dados para UG específica
            Object[] totais = repository.findTotaisFlexivel(anoNf, mesNf, anoPagamento, mesPagamento, ug, null);
            Map<String, Object> dados = criarMapaTotais(ug, totais);

            // Verifica se há dados
            if (((Number) dados.get("totalDocumentos")).longValue() == 0) {
                StringBuilder mensagem = new StringBuilder("Nenhum documento encontrado para UG ").append(ug.name());
                if (anoNf != null || mesNf != null) {
                    mensagem.append(" com data NF");
                    if (anoNf != null) mensagem.append(" ano=").append(anoNf);
                    if (mesNf != null) mensagem.append(" mês=").append(obterNomeMes(mesNf));
                }
                if (anoPagamento != null || mesPagamento != null) {
                    mensagem.append(" com data pagamento");
                    if (anoPagamento != null) mensagem.append(" ano=").append(anoPagamento);
                    if (mesPagamento != null) mensagem.append(" mês=").append(obterNomeMes(mesPagamento));
                }

                throw new NenhumDocumentoEncontradoException(mensagem.toString());
            }

            resultado.put("dados", dados);
        } else {
            // Dados para ambas as UGs
            Object[] totaisPrimaria = repository.findTotaisFlexivel(anoNf, mesNf, anoPagamento, mesPagamento, UG.PRIMARIA, null);
            Object[] totaisSecundaria = repository.findTotaisFlexivel(anoNf, mesNf, anoPagamento, mesPagamento, UG.SECUNDARIA, null);

            Map<String, Object> primaria = criarMapaTotais(UG.PRIMARIA, totaisPrimaria);
            Map<String, Object> secundaria = criarMapaTotais(UG.SECUNDARIA, totaisSecundaria);

            // Verifica se há dados em pelo menos uma UG
            long totalPrimaria = ((Number) primaria.get("totalDocumentos")).longValue();
            long totalSecundaria = ((Number) secundaria.get("totalDocumentos")).longValue();

            if (totalPrimaria == 0 && totalSecundaria == 0) {
                StringBuilder mensagem = new StringBuilder("Nenhum documento encontrado");
                if (anoNf != null || mesNf != null) {
                    mensagem.append(" com data NF");
                    if (anoNf != null) mensagem.append(" ano=").append(anoNf);
                    if (mesNf != null) mensagem.append(" mês=").append(obterNomeMes(mesNf));
                }
                if (anoPagamento != null || mesPagamento != null) {
                    mensagem.append(" com data pagamento");
                    if (anoPagamento != null) mensagem.append(" ano=").append(anoPagamento);
                    if (mesPagamento != null) mensagem.append(" mês=").append(obterNomeMes(mesPagamento));
                }

                throw new NenhumDocumentoEncontradoException(mensagem.toString());
            }

            Map<String, Object> dados = new HashMap<>();
            dados.put("primaria", primaria);
            dados.put("secundaria", secundaria);
            resultado.put("dados", dados);
        }

        return resultado;
    }

    /**
     * Obtém dados agregados por ano, organizados por mês e UG.
     */
    public Map<String, Object> obterDadosAgregadosPorAno(Integer ano) {
        if (ano == null) {
            throw new IllegalArgumentException("Ano é obrigatório");
        }

        if (ano < 2000 || ano > 2100) {
            throw new IllegalArgumentException("Ano deve estar entre 2000 e 2100");
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("ano", ano);

        // Dados para todo o ano, organizados por mês
        List<Map<String, Object>> meses = new ArrayList<>();
        boolean temDados = false;

        for (int mes = 1; mes <= 12; mes++) {
            Object[] totaisPrimaria = repository.findTotaisFlexivel(ano, mes, null, null, UG.PRIMARIA, null);
            Object[] totaisSecundaria = repository.findTotaisFlexivel(ano, mes, null, null, UG.SECUNDARIA, null);

            Map<String, Object> primaria = criarMapaTotais(UG.PRIMARIA, totaisPrimaria);
            Map<String, Object> secundaria = criarMapaTotais(UG.SECUNDARIA, totaisSecundaria);

            long totalP = ((Number) primaria.get("totalDocumentos")).longValue();
            long totalS = ((Number) secundaria.get("totalDocumentos")).longValue();

            if (totalP > 0 || totalS > 0) {
                temDados = true;
                Map<String, Object> dadosMes = new HashMap<>();
                dadosMes.put("mes", mes);
                dadosMes.put("nomeMes", obterNomeMes(mes));
                dadosMes.put("primaria", primaria);
                dadosMes.put("secundaria", secundaria);
                meses.add(dadosMes);
            }
        }

        if (!temDados) {
            throw new NenhumDocumentoEncontradoException(
                    String.format("Nenhum documento encontrado para o ano %d", ano)
            );
        }

        resultado.put("meses", meses);
        return resultado;
    }

    /**
     * Calcula o total retido para um período específico.
     */
    public Map<String, Object> calcularTotalRetido(Integer ano, Integer mes, UG ug) {
        Object[] totais = repository.findTotaisFlexivel(ano, mes, null, null, ug, StatusDocumento.PAGO);
        return criarMapaTotais(ug, totais);
    }

    /**
     * Método auxiliar para criar mapa de totais a partir do resultado da query.
     */
    private Map<String, Object> criarMapaTotais(UG ug, Object[] totais) {
        Map<String, Object> resultado = new HashMap<>();

        if (totais != null && totais.length >= 4) {
            resultado.put("ug", ug.name());
            resultado.put("totalDocumentos", totais[0] != null ? ((Number) totais[0]).longValue() : 0L);
            resultado.put("totalRetido", totais[1] != null ? new BigDecimal(totais[1].toString()) : BigDecimal.ZERO);
            resultado.put("valorBruto", totais[2] != null ? new BigDecimal(totais[2].toString()) : BigDecimal.ZERO);
            resultado.put("valorLiquido", totais[3] != null ? new BigDecimal(totais[3].toString()) : BigDecimal.ZERO);
        } else {
            resultado.put("ug", ug.name());
            resultado.put("totalDocumentos", 0L);
            resultado.put("totalRetido", BigDecimal.ZERO);
            resultado.put("valorBruto", BigDecimal.ZERO);
            resultado.put("valorLiquido", BigDecimal.ZERO);
        }

        return resultado;
    }

    /**
     * Método auxiliar para obter nome do mês.
     */
    private String obterNomeMes(Integer mes) {
        String[] nomes = {
                "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        };
        return (mes >= 1 && mes <= 12) ? nomes[mes - 1] : "Mês inválido";
    }

    /**
     * Pesquisa Documentos Hábeis com filtros opcionais específicos.
     * Permite buscar por número DH, número NF, código RFB, natureza de rendimento, UG, CNPJ e status.
     */
    public Page<DocumentoHabil> pesquisarDH(String numeroDh, Integer numeroNf, String codigoRfb,
                                            String naturezaRendimento, UG ug, String cnpj,
                                            StatusDocumento statusPagamento, Pageable pageable) {
        // Validações de entrada
        if (numeroNf != null && numeroNf <= 0) {
            throw new IllegalArgumentException("Número da nota fiscal deve ser maior que zero");
        }

        if (cnpj != null && cnpj.trim().isEmpty()) {
            throw new IllegalArgumentException("CNPJ não pode estar vazio");
        }

        if (numeroDh != null && numeroDh.trim().isEmpty()) {
            throw new IllegalArgumentException("Número DH não pode estar vazio");
        }

        if (codigoRfb != null && codigoRfb.trim().isEmpty()) {
            throw new IllegalArgumentException("Código RFB não pode estar vazio");
        }

        if (naturezaRendimento != null && naturezaRendimento.trim().isEmpty()) {
            throw new IllegalArgumentException("Natureza de rendimento não pode estar vazia");
        }

        Page<DocumentoHabil> resultado = repository.pesquisarDH(
                numeroDh, numeroNf, codigoRfb, naturezaRendimento, ug, cnpj, statusPagamento, pageable);

        // Verifica se encontrou resultados
        if (resultado.isEmpty()) {
            StringBuilder mensagem = new StringBuilder("Nenhum documento encontrado com os critérios de pesquisa: ");
            if (numeroDh != null) mensagem.append("Número DH=").append(numeroDh).append(" ");
            if (numeroNf != null) mensagem.append("Número NF=").append(numeroNf).append(" ");
            if (codigoRfb != null) mensagem.append("Código RFB=").append(codigoRfb).append(" ");
            if (naturezaRendimento != null) mensagem.append("Natureza Rendimento=").append(naturezaRendimento).append(" ");
            if (ug != null) mensagem.append("UG=").append(ug).append(" ");
            if (cnpj != null) mensagem.append("CNPJ=").append(cnpj).append(" ");
            if (statusPagamento != null) mensagem.append("Status=").append(statusPagamento).append(" ");

            throw new NenhumDocumentoEncontradoException(mensagem.toString().trim());
        }

        return resultado;
    }

    /**
     * Busca um DocumentoHabil pelo número DH.
     * @param numeroDh número do documento hábil
     * @return DocumentoHabil encontrado
     * @throws EntidadeNaoEncontradaException se não encontrar
     */
    public DocumentoHabil buscarPorNumeroDh(String numeroDh) {
        return buscarPorNumeroDh(numeroDh, null);
    }

    // Sobrecarga nova permitindo informar UG para desambiguar duplicidades (já existente, mantida)
    public DocumentoHabil buscarPorNumeroDh(String numeroDh, UG ug) {
        if (numeroDh == null || numeroDh.isBlank()) {
            throw new IllegalArgumentException("Número DH é obrigatório");
        }
        String normalizado = numeroDh.trim().toUpperCase();
        java.util.List<DocumentoHabil> lista = repository.findAllByNumeroDhIgnoreCase(normalizado);
        if (lista.isEmpty()) {
            throw new EntidadeNaoEncontradaException("Documento Hábil com número DH '" + normalizado + "' não encontrado.");
        }
        if (lista.size() == 1) {
            return lista.get(0);
        }
        // Mais de um registro com mesmo numeroDh (provável em UGs diferentes ou dados inválidos)
        if (ug != null) {
            java.util.List<DocumentoHabil> filtrados = lista.stream().filter(d -> d.getUg() == ug).toList();
            if (filtrados.size() == 1) {
                return filtrados.get(0);
            }
            if (filtrados.isEmpty()) {
                throw new org.api.darf.exceptions.business.NumeroDhDuplicadoException(normalizado, null, lista.stream().map(d -> d.getUg().name()).distinct().toList());
            }
            throw new org.api.darf.exceptions.business.NumeroDhDuplicadoException(normalizado, ug.name(), lista.stream().map(d -> d.getUg().name()).distinct().toList());
        }
        // UG não informada, não podemos decidir qual retornar
        throw new org.api.darf.exceptions.business.NumeroDhDuplicadoException(normalizado, null, lista.stream().map(d -> d.getUg().name()).distinct().toList());
    }

    public org.springframework.data.domain.Page<DocumentoHabil> autocompleteNumeroDh(String term, UG ug, Integer page, Integer size, Integer limit) {
        if (term == null || term.trim().isEmpty()) {
            throw new IllegalArgumentException("Parâmetro 'term' é obrigatório");
        }
        String normalizado = term.trim().toUpperCase();
        // Compatibilidade: se 'limit' for passado e 'size' não for, usa limit
        if (size == null && limit != null) size = limit;
        if (size == null) size = 10;
        if (page == null) page = 0;
        if (size <= 0) size = 10;
        if (size > 50) size = 50; // hard cap
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by("numeroDh").ascending());
        return repository.autocompleteNumeroDh(normalizado, ug, pageable);
    }
}
