package org.api.darf.repositories;

import org.api.darf.models.DocumentoHabil;
import org.api.darf.models.enums.StatusDocumento;
import org.api.darf.models.enums.UG;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório para a entidade DocumentoHabil.
 * Estende JpaRepository para fornecer operações de CRUD e paginação.
 */
@Repository
public interface DocumentoHabilRepository extends JpaRepository<DocumentoHabil, Long> {

    /**
     * Retorna uma página com todos os DocumentosHabil.
     * Usa paginação para controlar a quantidade de dados retornados.
     *
     * @param pageable Objeto com informações de paginação e ordenação
     * @return Página de DocumentoHabil
     */
    Page<DocumentoHabil> findAll(Pageable pageable);

    /**
     * Consulta personalizada que retorna documentos pagos para um determinado CNPJ
     * e competência (mês/ano), utilizando a data de pagamento formatada.
     *
     * @param cnpj CNPJ do contribuinte
     * @param competencia Competência no formato 'YYYY-MM'
     * @param pageable Informações de paginação
     * @return Página com documentos que atendem aos critérios
     */
    @Query("SELECT d FROM DocumentoHabil d " +
            "WHERE d.cnpj = :cnpj " +
            "AND d.dataPagamento IS NOT NULL " +
            "AND FUNCTION('TO_CHAR', d.dataPagamento, 'YYYY-MM') = :competencia")
    Page<DocumentoHabil> findByCnpjAndCompetencia(
            @Param("cnpj") String cnpj,
            @Param("competencia") String competencia,
            Pageable pageable
    );

    /**
     * Retorna uma lista paginada com CNPJs distintos que possuem documentos
     * com status 'PAGO', para uma UG e competência específicas.
     *
     * @param ug Unidade Gestora (UG)
     * @param competencia Competência no formato 'YYYY-MM'
     * @param pageable Informações de paginação
     * @return Página contendo os CNPJs distintos
     */
    @Query("SELECT DISTINCT d.cnpj " +
            "FROM DocumentoHabil d " +
            "WHERE d.status = 'PAGO' " +
            "AND d.ug = :ug " +
            "AND FUNCTION('TO_CHAR', d.dataPagamento, 'YYYY-MM') = :competencia")
    Page<String> findDistinctCnpjsByUgAndCompetencia(
            @Param("ug") UG ug,
            @Param("competencia") String competencia,
            Pageable pageable
    );

    /**
     * Filtra documentos por status com paginação.
     *
     * @param status Status do documento
     * @param pageable Informações de paginação
     * @return Página de documentos com o status especificado
     */
    Page<DocumentoHabil> findByStatus(StatusDocumento status, Pageable pageable);

    /**
     * Busca documentos por ano, mês e UG específicos.
     *
     * @param ano Ano de referência
     * @param mes Mês de referência (1-12)
     * @param ug Unidade Gestora
     * @param pageable Informações de paginação
     * @return Página de documentos filtrados
     */
    @Query("SELECT d FROM DocumentoHabil d " +
            "WHERE YEAR(d.dataNf) = :ano " +
            "AND MONTH(d.dataNf) = :mes " +
            "AND d.ug = :ug")
    Page<DocumentoHabil> findByAnoMesEUG(
            @Param("ano") Integer ano,
            @Param("mes") Integer mes,
            @Param("ug") UG ug,
            Pageable pageable
    );

    /**
     * Calcula totais agregados por ano, mês e UG.
     *
     * @param ano Ano de referência
     * @param mes Mês de referência
     * @param ug Unidade Gestora
     * @return Array com [count, sum(retido), sum(bruto), sum(liquido)]
     */
    @Query("SELECT COUNT(d), " +
            "COALESCE(SUM(d.retencao.retidoIr + d.retencao.retidoCsll + d.retencao.retidoCofins + d.retencao.retidoPis), 0), " +
            "COALESCE(SUM(d.retencao.valorBruto), 0), " +
            "COALESCE(SUM(d.retencao.valorLiquido), 0) " +
            "FROM DocumentoHabil d " +
            "WHERE YEAR(d.dataNf) = :ano " +
            "AND MONTH(d.dataNf) = :mes " +
            "AND d.ug = :ug")
    Object[] findTotaisPorAnoMesEUG(
            @Param("ano") Integer ano,
            @Param("mes") Integer mes,
            @Param("ug") UG ug
    );

    /**
     * Query flexível para filtrar documentos com múltiplos critérios opcionais.
     * Diferencia entre data da nota fiscal e data de pagamento.
     * Quando filtrar por data de pagamento, só considera documentos que têm essa data preenchida.
     */
    @Query("SELECT d FROM DocumentoHabil d WHERE " +
            "(:cnpj IS NULL OR d.cnpj = :cnpj) AND " +
            "(:status IS NULL OR d.status = :status) AND " +
            "(:ug IS NULL OR d.ug = :ug) AND " +
            "(:anoNf IS NULL OR YEAR(d.dataNf) = :anoNf) AND " +
            "(:mesNf IS NULL OR MONTH(d.dataNf) = :mesNf) AND " +
            "(:anoPagamento IS NULL OR (d.dataPagamento IS NOT NULL AND YEAR(d.dataPagamento) = :anoPagamento)) AND " +
            "(:mesPagamento IS NULL OR (d.dataPagamento IS NOT NULL AND MONTH(d.dataPagamento) = :mesPagamento)) AND " +
            "(:competencia IS NULL OR (YEAR(d.dataNf) = :anoComp AND MONTH(d.dataNf) = :mesComp))")
    Page<DocumentoHabil> findByMultipleCriteria(
            @Param("cnpj") String cnpj,
            @Param("status") StatusDocumento status,
            @Param("ug") UG ug,
            @Param("anoNf") Integer anoNf,
            @Param("mesNf") Integer mesNf,
            @Param("anoPagamento") Integer anoPagamento,
            @Param("mesPagamento") Integer mesPagamento,
            @Param("competencia") String competencia,
            @Param("anoComp") Integer anoComp,
            @Param("mesComp") Integer mesComp,
            Pageable pageable
    );

    /**
     * Query flexível para totais agregados com parâmetros opcionais.
     * Diferencia entre data da nota fiscal e data de pagamento.
     * Quando filtrar por data de pagamento, só considera documentos que têm essa data preenchida.
     */
    @Query("SELECT COUNT(d), " +
            "COALESCE(SUM(d.retencao.retidoIr + d.retencao.retidoCsll + d.retencao.retidoCofins + d.retencao.retidoPis), 0), " +
            "COALESCE(SUM(d.retencao.valorBruto), 0), " +
            "COALESCE(SUM(d.retencao.valorLiquido), 0) " +
            "FROM DocumentoHabil d WHERE " +
            "(:anoNf IS NULL OR YEAR(d.dataNf) = :anoNf) AND " +
            "(:mesNf IS NULL OR MONTH(d.dataNf) = :mesNf) AND " +
            "(:anoPagamento IS NULL OR (d.dataPagamento IS NOT NULL AND YEAR(d.dataPagamento) = :anoPagamento)) AND " +
            "(:mesPagamento IS NULL OR (d.dataPagamento IS NOT NULL AND MONTH(d.dataPagamento) = :mesPagamento)) AND " +
            "(:ug IS NULL OR d.ug = :ug) AND " +
            "(:status IS NULL OR d.status = :status)")
    Object[] findTotaisFlexivel(
            @Param("anoNf") Integer anoNf,
            @Param("mesNf") Integer mesNf,
            @Param("anoPagamento") Integer anoPagamento,
            @Param("mesPagamento") Integer mesPagamento,
            @Param("ug") UG ug,
            @Param("status") StatusDocumento status
    );

    /**
     * Query para pesquisar Documentos Hábeis com filtros opcionais específicos.
     * Permite buscar por número DH, número NF, código RFB, natureza de rendimento, UG, CNPJ e status.
     */
    @Query("SELECT d FROM DocumentoHabil d WHERE " +
            "(:numeroDh IS NULL OR d.numeroDh = :numeroDh) AND " +
            "(:numeroNf IS NULL OR d.numeroNf = :numeroNf) AND " +
            "(:codigoRfb IS NULL OR d.retencao.codigoRfb = :codigoRfb) AND " +
            "(:naturezaRendimento IS NULL OR d.naturezaRendimento = :naturezaRendimento) AND " +
            "(:ug IS NULL OR d.ug = :ug) AND " +
            "(:cnpj IS NULL OR d.cnpj = :cnpj) AND " +
            "(:statusPagamento IS NULL OR d.status = :statusPagamento)")
    Page<DocumentoHabil> pesquisarDH(
            @Param("numeroDh") String numeroDh,
            @Param("numeroNf") Integer numeroNf,
            @Param("codigoRfb") String codigoRfb,
            @Param("naturezaRendimento") String naturezaRendimento,
            @Param("ug") UG ug,
            @Param("cnpj") String cnpj,
            @Param("statusPagamento") StatusDocumento statusPagamento,
            Pageable pageable
    );

    /**
     * Busca um DocumentoHabil pelo seu número DH.
     * @param numeroDh número do documento hábil
     * @return Optional contendo o DocumentoHabil, se encontrado
     */
    Optional<DocumentoHabil> findByNumeroDh(String numeroDh);

    /**
     * Busca um DocumentoHabil pelo seu número DH ignorando caixa (case-insensitive).
     * @param numeroDh número do documento hábil
     */
    Optional<DocumentoHabil> findByNumeroDhIgnoreCase(String numeroDh);

    /**
     * Retorna todos os documentos que possuem o número DH (case-insensitive).
     */
    java.util.List<DocumentoHabil> findAllByNumeroDhIgnoreCase(String numeroDh);

    /**
     * Verifica se existe algum DocumentoHabil com o mesmo número DH e UG.
     *
     * @param numeroDh número do documento hábil
     * @param ug Unidade Gestora
     * @return true se existir, false caso contrário
     */
    boolean existsByNumeroDhIgnoreCaseAndUg(String numeroDh, UG ug);

    /**
     * Verifica se existe algum DocumentoHabil com o mesmo número DH e UG,
     * excluindo um determinado ID (para evitar conflito ao atualizar o registro).
     *
     * @param numeroDh número do documento hábil
     * @param ug Unidade Gestora
     * @param id ID a ser excluído da verificação
     * @return true se existir, false caso contrário
     */
    boolean existsByNumeroDhIgnoreCaseAndUgAndIdNot(String numeroDh, UG ug, Long id);

    /**
     * Retorna todos os documentos que possuem o número DH e UG (case-insensitive).
     */
    java.util.List<DocumentoHabil> findAllByNumeroDhIgnoreCaseAndUg(String numeroDh, UG ug);

    /**
     * Autocomplete para número DH com filtro opcional de UG.
     * Retorna documentos cujo número DH começa com o termo pesquisado.
     *
     * @param term Termo para pesquisa (prefixo do número DH)
     * @param ug Filtro opcional por Unidade Gestora
     * @param pageable Informações de paginação
     * @return Lista de DocumentosHabil que atendem ao critério de pesquisa
     */
    @Query("SELECT d FROM DocumentoHabil d WHERE (:ug IS NULL OR d.ug = :ug) AND UPPER(d.numeroDh) LIKE CONCAT(UPPER(:term), '%') ORDER BY d.numeroDh ASC")
    org.springframework.data.domain.Page<DocumentoHabil> autocompleteNumeroDh(@Param("term") String term, @Param("ug") UG ug, org.springframework.data.domain.Pageable pageable);
}
