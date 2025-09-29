package org.api.darf.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.api.darf.models.enums.NaturezaRendimento;
import org.api.darf.models.enums.StatusDocumento;
import org.api.darf.models.enums.UG;
import org.hibernate.validator.constraints.br.CNPJ;

import java.time.LocalDate;

@Entity
@Table(
        name = "documento_habil",
        uniqueConstraints = @UniqueConstraint(name = "uk_documento_habil_numero_dh_ug", columnNames = {"numeroDh", "ug"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoHabil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Geração automática do ID pelo banco (auto-incremento)
    private Long id;

    @Enumerated(EnumType.STRING) // Salva o enum como texto no banco
    @Column(nullable = false)
    private UG ug; // Unidade Gestora que emitiu o documento

    @Pattern(
            regexp = "^202\\d[A-Za-z]{2}\\d{6}$",
            message = "O número do documento hábil deve estar no formato: 202X YY XXXXXX"
    )
    @Column(nullable = false, length = 14)
    private String numeroDh; // Número do Documento Hábil, seguindo padrão específico

    @CNPJ(message = "CNPJ inválido")
    @Column(length = 14, nullable = false)
    private String cnpj; // CNPJ do prestador ou fornecedor

    @Column(nullable = false)
    private Integer numeroNf; // Número da Nota Fiscal associada

    @Column(nullable = false)
    private LocalDate dataNf; // Data de emissão da nota fiscal

    private LocalDate dataPagamento; // Data do pagamento, se já realizado

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusDocumento status; // Status atual do documento (ex: PAGO, LIQUIDADO)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NaturezaRendimento naturezaRendimento; // Tipo de natureza relacionado ao serviço/produto

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "retencao_id", referencedColumnName = "id")
    private Retencao retencao; // Dados da retenção de impostos relacionados ao documento
}
