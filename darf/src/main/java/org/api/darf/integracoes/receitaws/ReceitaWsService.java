package org.api.darf.integracoes.receitaws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class ReceitaWsService {
//
//    private static final String BASE_URL = "https://www.receitaws.com.br/v1/cnpj/";
//
//    public ReceitaWsResponse buscarDadosPorCnpj(String cnpj) {
//        String url = UriComponentsBuilder
//                .fromHttpUrl(BASE_URL + cnpj)
//                .toUriString();
//
//        RestTemplate restTemplate = new RestTemplate();
//        ReceitaWsResponse resposta = restTemplate.getForObject(url, ReceitaWsResponse.class);
//
//        if (resposta == null || resposta.getNome() == null) {
//            throw new IllegalArgumentException("CNPJ inválido ou não encontrado na ReceitaWS");
//        }
//
//        return resposta;
//    }
}
