package org.api.darf.exceptions.business;

import java.util.List;

/**
 * Exceção lançada quando:
 * 1) Há tentativa de criar/atualizar um DocumentoHabil com numeroDh já existente para a mesma UG (violação de regra de negócio)
 * 2) A busca por numeroDh resulta em múltiplos registros em UGs diferentes sem que a UG seja informada (ambiguidade)
 */
public class NumeroDhDuplicadoException extends RuntimeException {

    private final String numeroDh;
    private final String ugInformada;
    private final List<String> ugsEncontradas;

    public NumeroDhDuplicadoException(String numeroDh, String ugInformada, List<String> ugsEncontradas) {
        super(montarMensagem(numeroDh, ugInformada, ugsEncontradas));
        this.numeroDh = numeroDh;
        this.ugInformada = ugInformada;
        this.ugsEncontradas = ugsEncontradas;
    }

    private static String montarMensagem(String numeroDh, String ugInformada, List<String> ugsEncontradas) {
        if (ugsEncontradas == null || ugsEncontradas.isEmpty()) {
            return "Número DH '" + numeroDh + "' já existe"; // fallback genérico
        }
        // Caso clássico: duplicado MESMA UG (violação de unicidade)
        if (ugsEncontradas.size() == 1 && ugInformada != null && ugsEncontradas.contains(ugInformada)) {
            return "Já existe Documento Hábil com número DH '" + numeroDh + "' para a UG '" + ugInformada + "'.";
        }
        // Ambiguidade entre múltiplas UGs sem UG informada
        if (ugInformada == null && ugsEncontradas.size() > 1) {
            return "Número DH '" + numeroDh + "' existe em múltiplas UGs: " + ugsEncontradas + ". Informe o parâmetro 'ug' para desambiguar.";
        }
        // UG informada mas ainda assim não foi possível desambiguar (caso raro de dados inconsistentes)
        if (ugInformada != null && ugsEncontradas.size() > 1) {
            return "Número DH '" + numeroDh + "' existe em múltiplas UGs (" + ugsEncontradas + "). A UG informada '" + ugInformada + "' não foi suficiente para desambiguar.";
        }
        // Duplicado na mesma UG detectado sem passar ugInformada (ex: na criação)
        if (ugsEncontradas.size() == 1 && ugInformada == null) {
            return "Já existe Documento Hábil com número DH '" + numeroDh + "' para a UG '" + ugsEncontradas.get(0) + "'.";
        }
        return "Número DH duplicado ou ambíguo: '" + numeroDh + "'.";
    }

    public String getNumeroDh() { return numeroDh; }
    public String getUgInformada() { return ugInformada; }
    public List<String> getUgsEncontradas() { return ugsEncontradas; }
}

