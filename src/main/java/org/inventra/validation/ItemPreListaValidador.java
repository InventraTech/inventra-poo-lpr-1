
package org.inventra.validation;

import org.inventra.model.ItemPreListaMolde;
import java.util.ArrayList;
import java.util.List;

/**
 * Regras para os itens da pré-lista: pré-lista, fornecedor e produto precisam estar selecionados e a quantidade deve ser positiva.
 */
public class ItemPreListaValidador {

    /**
     * Confere os dados já montados a partir dos parâmetros HTTP e devolve as inconsistências encontradas.
     *
     * @return mensagens de erro prontas para exibir ao usuário; lista vazia quando os dados são válidos
     */
    public static List<String> validar(ItemPreListaMolde item) {

        List<String> erros = new ArrayList<>();

        if (item == null) {
            erros.add("Os dados do item não foram informados.");
            return erros;
        }

        // Pré-lista
        if (item.getFkPreLista() <= 0) {
            erros.add("Selecione uma pré-lista válida.");
        }

        // Fornecedor
        if (item.getFkFornecedor() <= 0) {
            erros.add("Selecione um fornecedor válido.");
        }

        // Produto
        if (item.getFkProduto() <= 0) {
            erros.add("Selecione um produto válido");
        }

        // Quantidade
        if (item.getQtd() <= 0) {
            erros.add("A quantidade deve ser maior que zero.");
        }

        return erros;
    }
}
