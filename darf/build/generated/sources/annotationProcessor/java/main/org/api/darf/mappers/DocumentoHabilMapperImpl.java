package org.api.darf.mappers;

import javax.annotation.processing.Generated;
import org.api.darf.dtos.documento.DocumentoHabilRequestDTO;
import org.api.darf.dtos.documento.DocumentoHabilResponseDTO;
import org.api.darf.models.DocumentoHabil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-26T21:08:34-0400",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.3.jar, environment: Java 21.0.7 (Microsoft)"
)
@Component
public class DocumentoHabilMapperImpl implements DocumentoHabilMapper {

    @Autowired
    private RetencaoMapper retencaoMapper;

    @Override
    public DocumentoHabil toEntity(DocumentoHabilRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        DocumentoHabil documentoHabil = new DocumentoHabil();

        documentoHabil.setUg( dto.getUg() );
        documentoHabil.setNumeroDh( dto.getNumeroDh() );
        documentoHabil.setCnpj( dto.getCnpj() );
        documentoHabil.setNumeroNf( dto.getNumeroNf() );
        documentoHabil.setDataNf( dto.getDataNf() );
        documentoHabil.setDataPagamento( dto.getDataPagamento() );
        documentoHabil.setStatus( dto.getStatus() );
        documentoHabil.setNaturezaRendimento( dto.getNaturezaRendimento() );
        documentoHabil.setRetencao( retencaoMapper.toEntity( dto.getRetencao() ) );

        return documentoHabil;
    }

    @Override
    public DocumentoHabilResponseDTO toResponseDTO(DocumentoHabil entity) {
        if ( entity == null ) {
            return null;
        }

        DocumentoHabilResponseDTO documentoHabilResponseDTO = new DocumentoHabilResponseDTO();

        documentoHabilResponseDTO.setId( entity.getId() );
        documentoHabilResponseDTO.setUg( entity.getUg() );
        documentoHabilResponseDTO.setNumeroDh( entity.getNumeroDh() );
        documentoHabilResponseDTO.setCnpj( entity.getCnpj() );
        documentoHabilResponseDTO.setNumeroNf( entity.getNumeroNf() );
        documentoHabilResponseDTO.setDataNf( entity.getDataNf() );
        documentoHabilResponseDTO.setDataPagamento( entity.getDataPagamento() );
        documentoHabilResponseDTO.setStatus( entity.getStatus() );
        documentoHabilResponseDTO.setNaturezaRendimento( entity.getNaturezaRendimento() );
        documentoHabilResponseDTO.setRetencao( retencaoMapper.toDTO( entity.getRetencao() ) );

        return documentoHabilResponseDTO;
    }
}
