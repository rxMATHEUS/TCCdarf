package org.api.darf.services;

import lombok.RequiredArgsConstructor;
import org.api.darf.exceptions.notfound.EntidadeNaoEncontradaException;
import org.api.darf.exceptions.notfound.NenhumDocumentoEncontradoException;
import org.api.darf.exceptions.notfound.NenhumDocumentoPagoEncontradoException;
import org.api.darf.models.DocumentoHabil;
import org.api.darf.models.Retencao;
import org.api.darf.models.enums.UG;
import org.api.darf.repositories.DocumentoHabilRepository;
import org.api.darf.services.documento.DocumentoHabilStatusService;
import org.api.darf.services.documento.DocumentoHabilValidatorService;
import org.api.darf.services.retencao.RetencaoCalculatorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.YearMonth;

/**
 * Classe que centraliza a lógica de negócio relacionada ao Documento Hábil.
 * Atua como fachada para orquestrar validações, persistência e cálculos de retenções.
 */
@Service
@RequiredArgsConstructor
public class DocumentoHabilFacade {

    private final DocumentoHabilRepository repository;
    private final DocumentoHabilValidatorService validatorService;
    private final DocumentoHabilStatusService statusService;
    private final RetencaoCalculatorService retencaoCalculatorService;

    /**
     * Cria um novo Documento Hábil com validações e cálculo de retenções.
     */
    public DocumentoHabil criar(DocumentoHabil documento) {
        validatorService.validarRegrasDeNegocio(documento);
        prepararDocumento(documento);
        return repository.save(documento);
    }

    /**
     * Busca um Documento Hábil pelo ID e atualiza o status automaticamente, se necessário.
     */
    public DocumentoHabil buscarPorId(Long id) {
        DocumentoHabil documento = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Documento não encontrado: ID " + id));
        statusService.atualizarStatus(documento);
        return documento;
    }

    /**
     * Retorna uma página contendo todos os Documentos Hábil cadastrados.
     */
    public Page<DocumentoHabil> listarTodos(Pageable pageable) {
        return repository.findAll(pageable);
    }

    /**
     * Atualiza os dados de um Documento Hábil existente e recalcula retenções.
     */
    public DocumentoHabil atualizar(Long id, DocumentoHabil novoDocumento) {
        DocumentoHabil existente = buscarPorId(id);

        // Atualiza os campos principais
        existente.setUg(novoDocumento.getUg());
        existente.setNumeroDh(novoDocumento.getNumeroDh());
        existente.setCnpj(novoDocumento.getCnpj());
        existente.setNumeroNf(novoDocumento.getNumeroNf());
        existente.setDataNf(novoDocumento.getDataNf());
        existente.setDataPagamento(novoDocumento.getDataPagamento());
        existente.setNaturezaRendimento(novoDocumento.getNaturezaRendimento());
        existente.setRetencao(novoDocumento.getRetencao());

        // Revalida e recalcula
        validatorService.validarRegrasDeNegocio(existente);
        prepararDocumento(existente);

        return repository.save(existente);
    }

    /**
     * Exclui um Documento Hábil com base no ID informado.
     */
    public void deletar(Long id) {
        DocumentoHabil existente = buscarPorId(id);
        repository.delete(existente);
    }

    /**
     * Realiza as operações necessárias antes de persistir um Documento:
     * cálculo de retenções e atualização do status.
     */
    private void prepararDocumento(DocumentoHabil documento) {
        Retencao retencao = documento.getRetencao();
        retencaoCalculatorService.calcularValoresRetencao(retencao);
        statusService.atualizarStatus(documento);
    }

    /**
     * Busca todos os Documentos Hábil com determinado CNPJ e competência (ano-mês).
     * Lança exceção se nenhum for encontrado.
     */
    public Page<DocumentoHabil> buscarPorCnpjEMes(String cnpj, YearMonth competencia, Pageable pageable) {
        String competenciaStr = competencia.toString();
        Page<DocumentoHabil> pagina = repository.findByCnpjAndCompetencia(cnpj, competenciaStr, pageable);

        if (pagina.isEmpty()) {
            throw new NenhumDocumentoEncontradoException(
                    "Nenhum Documento Hábil encontrado para o CNPJ informado na competência " + competenciaStr + "."
            );
        }

        return pagina;
    }

    /**
     * Lista os CNPJs com documentos pagos em determinada competência e UG.
     * Lança exceção se nenhum resultado for encontrado.
     */
    public Page<String> listarCnpjsComPagamentoEmMesEUg(YearMonth competencia, UG ug, Pageable pageable) {
        String competenciaStr = competencia.toString();
        Page<String> resultado = repository.findDistinctCnpjsByUgAndCompetencia(ug, competenciaStr, pageable);

        if (resultado.isEmpty()) {
            throw new NenhumDocumentoPagoEncontradoException(
                    "Nenhum Documento Hábil com status PAGO foi encontrado para a UG " + ug.name()
                            + " na competência " + competenciaStr + "."
            );
        }

        return resultado;
    }
}
