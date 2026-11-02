package org.inventra.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Leitura defensiva de parâmetros HTTP para os servlets. Em vez de lançar exceção, cada método registra uma mensagem
 * na lista {@code erros} e devolve um valor neutro, de modo que o usuário veja todos os problemas de uma vez.
 */
public final class ParametrosHttp {

    private ParametrosHttp() {
    }

    /** Número inteiro obrigatório. Ausente, vazio ou não numérico gera mensagem e devolve 0. */
    public static int lerInt(String valor, String rotulo, List<String> erros) {
        if (valor == null || valor.isBlank()) {
            erros.add("O campo " + rotulo + " é obrigatório.");
            return 0;
        }
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            erros.add("O campo " + rotulo + " deve ser um número inteiro válido.");
            return 0;
        }
    }

    /** Data (aaaa-mm-dd). Vazia devolve null sem erro (a obrigatoriedade é checada pelo validador). */
    public static LocalDate lerData(String valor, String rotulo, List<String> erros) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(valor);
        } catch (DateTimeParseException e) {
            erros.add("A data de " + rotulo + " é inválida.");
            return null;
        }
    }

    /** Data e hora (aaaa-mm-ddThh:mm). Vazia devolve null sem erro. */
    public static LocalDateTime lerDataHora(String valor, String rotulo, List<String> erros) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(valor);
        } catch (DateTimeParseException e) {
            erros.add("A data e hora de " + rotulo + " é inválida.");
            return null;
        }
    }

    /** Valor decimal; aceita vírgula ou ponto. Vazio devolve null sem erro. */
    public static BigDecimal lerDecimal(String valor, String rotulo, List<String> erros) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(valor.replace(",", "."));
        } catch (NumberFormatException e) {
            erros.add("O valor de " + rotulo + " é inválido.");
            return null;
        }
    }

    /** Booleano estrito: só aceita "true" ou "false" (usado nas edições por coluna). */
    public static boolean lerBooleano(String valor, String rotulo, List<String> erros) {
        if ("true".equalsIgnoreCase(valor)) {
            return true;
        }
        if (!"false".equalsIgnoreCase(valor)) {
            erros.add("O campo " + rotulo + " deve ser verdadeiro ou falso.");
        }
        return false;
    }

    /** Junta as mensagens numa única linha, que é o que as telas exibem em "mensagemErro". */
    public static String juntar(List<String> erros) {
        return String.join(" • ", erros);
    }

    /** Guarda as mensagens na sessão e volta para a tela da lista (padrão já usado pelos servlets). */
    public static void redirecionarComErros(HttpServletRequest request, HttpServletResponse response,
                                            String caminho, List<String> erros) throws IOException {
        request.getSession().setAttribute("mensagemErro", juntar(erros));
        response.sendRedirect(request.getContextPath() + caminho);
    }
}
