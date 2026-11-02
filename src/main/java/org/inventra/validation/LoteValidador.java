
package org.inventra.validation;

import org.inventra.model.LoteMolde;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

/**
 * Regras de lote: quantidade inicial positiva, quantidade atual entre zero e a inicial, validade não anterior à entrada
 * e valor de compra não negativo.
 */
public class LoteValidador {

    /**
     * Confere os dados já montados a partir dos parâmetros HTTP e devolve as inconsistências encontradas.
     *
     * @return mensagens de erro prontas para exibir ao usuário; lista vazia quando os dados são válidos
     */
    public static List<String> validar(LoteMolde lote) {

        List<String> erros = new ArrayList<>();

        if (lote == null) {
            erros.add("Os dados do lote não foram informados.");
            return erros;
        }

        // Produto
        if (lote.getFkProduto() <= 0) {
            erros.add("Selecione um produto válido.");
        }

        // Número do lote
        if (lote.getNumeroLote() == null ||
                lote.getNumeroLote().isBlank()) {
            erros.add("O número do lote é obrigatório.");
        }

        // Nota fiscal (obrigatória no formulário de cadastro e de edição)
        if (lote.getNotaFiscal() == null ||
                lote.getNotaFiscal().isBlank()) {
            erros.add("A nota fiscal é obrigatória.");
        }

        // Quantidade inicial
        if (lote.getQtdInicial() <= 0) {
            erros.add("A quantidade inicial deve ser maior que zero.");
        }

        // Quantidade atual
        if (lote.getQtdAtual() < 0) {
            erros.add("A quantidade atual não pode ser menor que zero.");
        }

        // Relação entre quantidades
        if (lote.getQtdInicial() > 0 &&
                lote.getQtdAtual() > lote.getQtdInicial()) {
            erros.add("A quantidade atual não pode superar a inicial.");
        }

        // Data de entrada
        if (lote.getDtEntrada() == null) {
            erros.add("A data de entrada é obrigatória.");
        }

        // Data de validade
        if (lote.getDtValidade() == null) {
            erros.add("A data de validade é obrigatória.");
        }

        // Comparação das datas
        if (lote.getDtEntrada() != null &&
                lote.getDtValidade() != null &&
                lote.getDtValidade().isBefore(lote.getDtEntrada())) {
            erros.add("A validade não pode ser anterior à entrada.");
        }

        // Valor da compra
        if (lote.getValorCompra() == null) {
            erros.add("O valor da compra é obrigatório");
        } else if (lote.getValorCompra().compareTo(BigDecimal.ZERO) < 0) {
            erros.add("O valor da compra não pode ser menor que zero");
        }

        return erros;
    }
}
