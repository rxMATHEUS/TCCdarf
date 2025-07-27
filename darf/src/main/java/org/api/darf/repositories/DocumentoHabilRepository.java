package org.api.darf.repositories;

import org.api.darf.models.DocumentoHabil;
import org.api.darf.models.enums.UG;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
