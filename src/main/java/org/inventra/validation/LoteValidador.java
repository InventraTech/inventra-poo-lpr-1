
package org.inventra.validation;

import org.inventra.model.LoteMolde;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class LoteValidador {

    public static List<String> validar(LoteMolde lote) {

        List<String> erros = new ArrayList<>();

        if (lote == null) {
            erros.add("Os dados do lote não foram informados.");
            return erros;
        }

        // Produto
        if (lote.getFk_produto() <= 0) {
            erros.add("A FK do produto não pode ser menor que zero");
        }

        // Número do lote
        if (lote.getNumero_lote() == null ||
                lote.getNumero_lote().isBlank()) {
            erros.add("O número do lote é obrigatório.");
        }

        // Quantidade inicial
        if (lote.getQtd_inicial() <= 0) {
            erros.add("A quantidade inicial deve ser maior que zero.");
        }

        // Quantidade atual
        if (lote.getQtd_atual() < 0) {
            erros.add("A quantidade atual não pode ser menor que zero.");
        }

        // Relação entre quantidades
        if (lote.getQtd_inicial() > 0 &&
                lote.getQtd_atual() > lote.getQtd_inicial()) {
            erros.add("A quantidade atual não pode superar a inicial.");
        }

        // Data de entrada
        if (lote.getDt_entrada() == null) {
            erros.add("A data de entrada é obrigatória.");
        }

        // Data de validade
        if (lote.getDt_validade() == null) {
            erros.add("A data de validade é obrigatória.");
        }

        // Comparação das datas
        if (lote.getDt_entrada() != null &&
                lote.getDt_validade() != null &&
                lote.getDt_validade().isBefore(lote.getDt_entrada())) {
            erros.add("A validade não pode ser anterior à entrada.");
        }

        // Valor da compra
        if (lote.getValor_compra() == null) {
            erros.add("O valor da compra é obrigatório");
        } else if (lote.getValor_compra().compareTo(BigDecimal.ZERO) < 0) {
            erros.add("O valor da compra não pode ser menor que zero");
        }

        return erros;
    }
}
