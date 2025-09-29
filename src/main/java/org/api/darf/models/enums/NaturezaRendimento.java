package org.api.darf.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum NaturezaRendimento {

    // Enum para diferentes tipos de natureza de rendimento com seus respectivos códigos e descrições
    ALIMENTACAO("17001", "6147", "Alimentação"),
    ENERGIA_ELETRICA("17002", "6147", "Energia elétrica"),
    SERVICOS_COM_EMPREGOS_DE_MATERIAIS("17003", "6147", "Serviços prestados com emprego de materiais"),
    CONSTRUCAO_CIVIL_COM_EMPREGOS_DE_MATERIAIS("17004", "6147", "Construção Civil por empreitada com emprego de materiais"),
    SERVICOS_HOSPITALARES("17005", "6147", "Serviços hospitalares de que trata o art. 30 da IN RFB nº 1.234/2012"),
    TRANSPORTE_DE_CARGAS("17006", "6147", "Transporte de cargas, exceto os relacionados na natureza de rendimento '17017'"),
    SERVICOS_DE_AUXILIO_DIAGNOSTICO("17007", "6147", "Serviços de auxílio diagnóstico e terapia, patologia clínica, imagenologia, anatomia patológica e citopatológica, medicina nuclear e análises e patologias clínicas, exames por métodos gráficos, procedimentos endoscópicos, radioterapia, quimioterapia, diálise e oxigenoterapia hiperbárica de que trata o art. 31 e parágrafo único da IN RFB nº 1.234/2012"),
    PRODUTOS_FARMACEUTICOS("17008", "6147", "Produtos farmacêuticos, de perfumaria, de toucador ou de higiene pessoal adquiridos de produtor, importador, distribuidor ou varejista, exceto os relacionados nas naturezas de rendimentos de '17019' a '17022'"),
    MERCADORIAS_E_BENS("17009", "6147", "Mercadorias e bens em geral"),
    GASOLINA_OLEO_DIESEL("17010", "9060", "Gasolina, inclusive de aviação, óleo diesel, gás liquefeito de petróleo (GLP), combustíveis derivados de petróleo ou de gás natural, querosene de aviação (QAV), e demais produtos derivados de petróleo, adquiridos de refinarias de petróleo, de demais produtores, de importadores, de distribuidor ou varejista"),
    ALCOOL_ETILICO("17011", "9060", "Álcool etílico hidratado, inclusive para fins carburantes, adquirido diretamente de produtor, importador ou do distribuidor"),
    BIODIESEL("17012", "9060", "Biodiesel adquirido de produtor ou importador"),
    GASOLINA_EXCETO_AVIACAO("17013", "8739", "Gasolina, exceto gasolina de aviação, óleo diesel e gás liquefeito de petróleo (GLP), derivados de petróleo ou de gás natural e querosene de aviação adquiridos de distribuidores e comerciantes varejistas"),
    ALCOOL_ETILICO_NACIONAL("17014", "8739", "Álcool etílico hidratado nacional, inclusive para fins carburantes adquirido de comerciante varejista"),
    BIODIESEL_COMERCIANTE_VAREJISTA("17015", "8739", "Biodiesel adquirido de distribuidores e comerciantes varejistas"),
    BIODIESEL_COMBUSTIVEL_SOCIAL("17016", "8739", "Biodiesel adquirido de produtor detentor regular do selo 'Combustível Social', fabricado a partir de mamona ou fruto, caroço ou amêndoa de palma produzidos nas regiões norte e nordeste e no semiárido, por agricultor familiar enquadrado no"),
    TRANSPORTE_INTERNACIONAL_DE_CARGAS("17017", "8767", "Transporte internacional de cargas efetuado por empresas nacionais"),
    ESTALEIROS_NAVAIS("17018", "8767", "Estaleiros navais brasileiros nas atividades de Construção, conservação, modernização, conversão e reparo de embarcações pré-registradas ou registradas no REB"),
    PRODUTOS_DE_PERFUMARIA("17019", "8767", "Produtos de perfumaria, de toucador e de higiene pessoal a que se refere o § 1º do art. 22 da IN RFB nº 1.234/2012, adquiridos de distribuidores e de comerciantes varejistas"),
    PRODUTOS_DE_QUE_TRATAM_ART_22("17020", "8767", "Produtos a que se refere o § 2º do art. 22 da IN RFB nº 1.234/2012"),
    PRODUTOS_DE_ALINHA_C_A_K("17021", "8767", "Produtos de que tratam as alíneas 'c' a 'k' do inciso I do art. 5º da IN RFB nº 1.234/2012"),
    PRODUTOS_DE_OUTROS_BENEFICIADOS("17022", "8767", "Outros produtos ou serviços beneficiados com isenção, não incidência ou Alíquotas zero da Cofins e da Contribuição para o PIS/Pasep, observado o disposto no § 5º do art. 2º da IN RFB nº 1.234/2012"),
    PASSAGENS_AEREAS("17023", "8850", "Passagens aéreas, rodoviárias e demais serviços de transporte de passageiros, inclusive, tarifa de embarque, exceto transporte internacional de passageiros, efetuado por empresas nacionais"),
    TRANSPORTE_INTERNACIONAL_DE_PASSAGEIROS("17024", "8850", "Transporte internacional de passageiros efetuado por empresas nacionais"),
    SERVICOS_PRESTADOS_POR_ASSOCIACOES("17025", "8863", "Serviços prestados por associações profissionais ou assemelhadas e cooperativas"),
    SERVICOS_BANCARIOS("17026", "8863", "Serviços prestados por bancos comerciais, bancos de investimento, bancos de desenvolvimento, caixas econômicas, sociedades de crédito, financiamento e investimento, sociedades de crédito imobiliário, e câmbio, distribuidoras de títulos e valores mobiliários, empresas de arrendamento mercantil, cooperativas de crédito, empresas de seguros privados e de capitalização e entidades abertas de previdência complementar."),
    SEGURO_SAUDE("17027", "8863", "Seguro Saúde"),
    SERVICOS_DE_ABASTECIMENTO_DE_AGUA("17028", "6190", "Serviços de abastecimento de água"),
    TELEFONE("17029", "6190", "Telefone"),
    CORREIO_E_TELEGRAFOS("17030", "6190", "Correio e telégrafos"),
    VIGILANCIA("17031", "6190", "Vigilância"),
    LIMPEZA("17032", "6190", "Limpeza"),
    LOCACAO_DE_MAO_DE_OBRA("17033", "6190", "Locação de mão de obra"),
    INTERMEDIACAO_DE_NEGOCIOS("17034", "6190", "Intermediação de negócios"),
    ADMINISTRACAO_LOCACAO_OU_CESSAO_DE_BENS("17035", "6190", "Administração, locação ou cessão de bens imóveis, móveis e direitos de qualquer natureza"),
    FACTORING("17036", "6190", "Factoring"),
    PLANO_DE_SAUDE("17037", "6190", "Plano de saúde humano, veterinário ou odontológico com valores fixos por servidor, por empregado ou por animal"),
    PAGAMENTO_EFETUADO_A_SOCIEDADE_COOPERATIVA("17038", "6190", "Pagamento efetuado a sociedade cooperativa pelo fornecimento de bens, conforme art. 24, da IN 1234/12."),
    SERVICOS_PRESTADOS_COM_EMPREGOS_DE_MATERIAIS("17039", "6190", "Serviços prestados com emprego de materiais, inclusive o de que trata a alínea 'C' do Inciso II do art. 27 da IN 1.1234."),
    DEMAIS_SERVICOS("17040", "6190", "Demais serviços");

    // Atributos de cada tipo de natureza de rendimento
    private final String naturezaRendimento; // Código do tipo de natureza de rendimento
    private final String codigoRFB; // Código associado ao RFB (Receita Federal do Brasil)
    private final String descricao; // Descrição do tipo de natureza de rendimento

    /**
     * Método para buscar uma natureza de rendimento pelo código RFB.
     * @param codigoRfb O código da Receita Federal para buscar a natureza de rendimento.
     * @return A natureza de rendimento correspondente ao código ou null se não encontrado.
     */
    public static Optional<NaturezaRendimento> getByCodigoRFB(String codigoRfb) {
        return Arrays.stream(values())
                .filter(n -> n.getCodigoRFB().equals(codigoRfb))
                .findFirst();
    }
}
