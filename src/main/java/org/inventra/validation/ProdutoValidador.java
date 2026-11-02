package org.inventra.validation;

import org.inventra.model.ProdutoMolde;
import java.util.ArrayList;
import java.util.List;

public class ProdutoValidador {

    public static List<String> validar(ProdutoMolde produto) {

        List<String> erros = new ArrayList<>();

        if (produto == null) {
            erros.add("Os dados do produto não foram informados.");
            return erros;
        }

        // Nome
        if (produto.getNome() == null ||
                produto.getNome().isBlank()) {
            erros.add("O nome do produto é obrigatório.");
        }

        // Marca
        if (produto.getFk_marca() <= 0) {
            erros.add("Selecione uma marca válida.");
        }

        // Categoria
        if (produto.getFk_categoria() <= 0) {
            erros.add("Selecione uma categoria válida.");
        }

        // Fornecedor
        if (produto.getFk_fornecedor() <= 0) {
            erros.add("Selecione um fornecedor válido.");
        }

        // Unidade de medida
        if (produto.getUnidadeMedida() == null ||
                produto.getUnidadeMedida().isBlank()) {
            erros.add("A unidade de medida é obrigatória.");
        }

        // Estoque mínimo
        if (produto.getEstoqueMin() < 0) {
            erros.add("O estoque mínimo não pode ser negativo.");
        }

        // Estoque máximo
        if (produto.getEstoqueMax() < 0) {
            erros.add("O estoque máximo não pode ser negativo.");
        }

        // Relação entre os estoques
        if (produto.getEstoqueMin() >= 0 &&
                produto.getEstoqueMax() >= 0 &&
                produto.getEstoqueMin() > produto.getEstoqueMax()) {
            erros.add("O estoque mínimo não pode superar o máximo.");
        }

        return erros;
    }
}
