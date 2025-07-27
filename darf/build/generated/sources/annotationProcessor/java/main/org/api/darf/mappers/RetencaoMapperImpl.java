package org.api.darf.mappers;

import javax.annotation.processing.Generated;
import org.api.darf.dtos.retencao.RetencaoDTO;
import org.api.darf.models.Retencao;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-26T21:08:34-0400",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.3.jar, environment: Java 21.0.7 (Microsoft)"
)
@Component
public class RetencaoMapperImpl implements RetencaoMapper {

    @Override
    public Retencao toEntity(RetencaoDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Retencao retencao = new Retencao();

        retencao.setCodigoRfb( dto.getCodigoRfb() );
        retencao.setAliquotaIr( dto.getAliquotaIr() );
        retencao.setAliquotaCsll( dto.getAliquotaCsll() );
        retencao.setAliquotaCofins( dto.getAliquotaCofins() );
        retencao.setAliquotaPisPasep( dto.getAliquotaPisPasep() );
        retencao.setValorBruto( dto.getValorBruto() );
        retencao.setRetidoIr( dto.getRetidoIr() );
        retencao.setRetidoCsll( dto.getRetidoCsll() );
        retencao.setRetidoCofins( dto.getRetidoCofins() );
        retencao.setRetidoPis( dto.getRetidoPis() );
        retencao.setValorLiquido( dto.getValorLiquido() );

        return retencao;
    }

    @Override
    public RetencaoDTO toDTO(Retencao entity) {
        if ( entity == null ) {
            return null;
        }

        RetencaoDTO retencaoDTO = new RetencaoDTO();

        retencaoDTO.setCodigoRfb( entity.getCodigoRfb() );
        retencaoDTO.setValorBruto( entity.getValorBruto() );
        retencaoDTO.setAliquotaIr( entity.getAliquotaIr() );
        retencaoDTO.setAliquotaCsll( entity.getAliquotaCsll() );
        retencaoDTO.setAliquotaCofins( entity.getAliquotaCofins() );
        retencaoDTO.setAliquotaPisPasep( entity.getAliquotaPisPasep() );
        retencaoDTO.setRetidoIr( entity.getRetidoIr() );
        retencaoDTO.setRetidoCsll( entity.getRetidoCsll() );
        retencaoDTO.setRetidoCofins( entity.getRetidoCofins() );
        retencaoDTO.setRetidoPis( entity.getRetidoPis() );
        retencaoDTO.setValorLiquido( entity.getValorLiquido() );

        return retencaoDTO;
    }
}
