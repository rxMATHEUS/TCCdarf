package org.api.darf.mappers;

import org.api.darf.dtos.documento.DocumentoHabilRequestDTO;
import org.api.darf.dtos.documento.DocumentoHabilResponseDTO;
import org.api.darf.models.DocumentoHabil;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper responsável por converter entre as entidades DocumentoHabil e seus respectivos DTOs.
 * Utiliza o MapStruct para geração automática do código de mapeamento.
 */
@Mapper(componentModel = "spring", uses = { RetencaoMapper.class }) // Injeta automaticamente como bean Spring e delega mapeamento de Retencao ao RetencaoMapper
public interface DocumentoHabilMapper {

    // Instância padrão do mapper (gerada automaticamente pelo MapStruct para testes manuais ou uso direto)
    DocumentoHabilMapper INSTANCE = Mappers.getMapper(DocumentoHabilMapper.class);

    /**
     * Converte um DTO de requisição (input do usuário) em uma entidade DocumentoHabil.
     * @param dto objeto vindo do controller
     * @return entidade DocumentoHabil pronta para persistência
     */
    DocumentoHabil toEntity(DocumentoHabilRequestDTO dto);

    /**
     * Converte uma entidade DocumentoHabil para um DTO de resposta (output para o cliente).
     * @param entity entidade vinda do banco
     * @return DTO formatado para retorno em API
     */
    DocumentoHabilResponseDTO toResponseDTO(DocumentoHabil entity);
}
