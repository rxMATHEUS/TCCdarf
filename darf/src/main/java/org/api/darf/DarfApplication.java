package org.api.darf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicação DARF.
 *
 * Ponto de entrada da aplicação Spring Boot, responsável por iniciar
 * o contexto da aplicação, fazer o scan dos componentes e iniciar o servidor embutido (Tomcat).
 *
 * A anotação @SpringBootApplication encapsula:
 * - @Configuration: Define a classe como fonte de beans.
 * - @EnableAutoConfiguration: Ativa a configuração automática do Spring com base nas dependências do classpath.
 * - @ComponentScan: Habilita o escaneamento de componentes nos pacotes abaixo.
 */
@SpringBootApplication
public class DarfApplication {

    /**
     * Método principal que inicia a aplicação.
     *
     * @param args argumentos de linha de comando, se houver
     */
    public static void main(String[] args) {
        SpringApplication.run(DarfApplication.class, args);
    }

}
