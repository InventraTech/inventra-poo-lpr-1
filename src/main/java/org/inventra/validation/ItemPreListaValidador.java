
package org.inventra.validation;

import org.inventra.model.ItemPreListaMolde;
import java.util.ArrayList;
import java.util.List;

public class ItemPreListaValidador {

    public static List<String> validar(ItemPreListaMolde item) {

        List<String> erros = new ArrayList<>();

        if (item == null) {
            erros.add("Os dados do item não foram informados.");
            return erros;
        }

        // Pré-lista
        if (item.getFk_pre_lista() <= 0) {
            erros.add("Selecione uma pré-lista válida.");
        }

        // Fornecedor
        if (item.getFk_fornecedor() <= 0) {
            erros.add("Selecione um fornecedor válido.");
        }

        // Produto
        if (item.getFk_produto() <= 0) {
            erros.add("Selecione um produto válido");
        }

        // Quantidade
        if (item.getQtd() <= 0) {
            erros.add("A quantidade deve ser maior que zero.");
        }

        return erros;
    }
}
