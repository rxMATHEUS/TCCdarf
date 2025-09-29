package org.api.darf.mappers;

import org.api.darf.dtos.retencao.RetencaoDTO;
import org.api.darf.models.Retencao;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper responsável por converter entre a entidade Retencao e seu DTO.
 * Utiliza o MapStruct para gerar automaticamente os métodos de mapeamento.
 */
@Mapper(componentModel = "spring") // Permite que o Spring injete este mapper como bean
public interface RetencaoMapper {

    // Instância padrão do mapper (útil para testes ou uso fora do Spring)
    RetencaoMapper INSTANCE = Mappers.getMapper(RetencaoMapper.class);

    /**
     * Converte um DTO de Retencao (input do cliente) para a entidade Retencao.
     * @param dto Objeto recebido na requisição
     * @return Entidade pronta para uso interno ou persistência
     */
    Retencao toEntity(RetencaoDTO dto);

    /**
     * Converte a entidade Retencao em um DTO (output da API).
     * @param entity Entidade do domínio
     * @return DTO formatado para resposta ao cliente
     */
    RetencaoDTO toDTO(Retencao entity);
}
