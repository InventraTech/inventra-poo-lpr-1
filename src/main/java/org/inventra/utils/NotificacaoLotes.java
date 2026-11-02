package org.inventra.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.inventra.dao.LoteDAO;

/**
 * Prepara os dados do sino de notificações do cabeçalho: os lotes com validade mais próxima e os totais de lotes
 * vencidos e vencendo em 30 dias. Cada servlet de listagem chama {@link #carregar} para a tela exibir o sino.
 */
public final class NotificacaoLotes {

    // Quantos lotes aparecem na lista do sino.
    private static final int QUANTIDADE_LOTES = 5;

    private NotificacaoLotes() {
    }

    /**
     * Coloca na requisição os atributos {@code lotesProximosValidade}, {@code totalLotesVencidos} e
     * {@code totalLotesVencendo30dias}, lidos pelo JSP do cabeçalho.
     */
    public static void carregar(HttpServletRequest request) {
        LoteDAO loteDAO = new LoteDAO();

        request.setAttribute("lotesProximosValidade", loteDAO.listarLoteDataValidade(true, QUANTIDADE_LOTES, 0));
        request.setAttribute("totalLotesVencidos", loteDAO.contarLotesVencidos());
        request.setAttribute("totalLotesVencendo30dias", loteDAO.contarLotesVencendo30dias());
    }
}
