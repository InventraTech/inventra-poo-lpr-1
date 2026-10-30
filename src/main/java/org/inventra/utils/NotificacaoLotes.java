package org.inventra.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.inventra.dao.LoteDAO;

public final class NotificacaoLotes {

    private static final int quantidade_lotes = 5;

    private NotificacaoLotes() {
    }

    public static void carregar(HttpServletRequest request) {
        LoteDAO loteDAO = new LoteDAO();

        request.setAttribute("lotesProximosValidade", loteDAO.listarLoteDataValidade(true, quantidade_lotes, 0));
        request.setAttribute("totalLotesVencidos", loteDAO.contarLotesVencidos());
        request.setAttribute("totalLotesVencendo30dias", loteDAO.contarLotesVencendo30dias());
    }
}
