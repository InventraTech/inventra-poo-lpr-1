
package org.inventra.validation;

import org.inventra.model.RequisicaoMolde;
import java.util.ArrayList;
import java.util.List;

public class RequisicaoValidador {

    public static List<String> validar(RequisicaoMolde requisicao) {

        List<String> erros = new ArrayList<>();

        if (requisicao == null) {
            erros.add("Os dados da requisição não foram informados.");
            return erros;
        }

        // Tipo de requisição
        if (requisicao.getIdTipoRequisicao() <= 0) {
            erros.add("Selecione um tipo de requisição válido.");
        }

        // Produto
        if (requisicao.getFkProduto() <= 0) {
            erros.add("Selecione um produto válido.");
        }

        // Quantidade
        if (requisicao.getQuantidadeProduto() <= 0) {
            erros.add("A quantidade solicitada deve ser maior que zero.");
        }

        // Motivo
        if (requisicao.getMotivo() == null || requisicao.getMotivo().isBlank()) {
            erros.add("O motivo da requisição é obrigatório.");
        }

        // Status
        if (requisicao.getStatus() == null || requisicao.getStatus().isBlank()) {
            erros.add("O status da requisição é obrigatório.");
        }

        // Funcionário solicitante
        if (requisicao.getFkFuncionarioSolicitante() <= 0) {
            erros.add("Selecione um funcionário solicitante válido.");
        }

        // Funcionário aprovador
        if (requisicao.getFkFuncionarioAprovador() < 0) {
            erros.add("O id do aprovador é inválido.");
        }

        // Não vai permitir que o funcionario aprove a própria requisição
        if (requisicao.getFkFuncionarioAprovador() > 0 && requisicao.getFkFuncionarioSolicitante() == requisicao.getFkFuncionarioAprovador()) {
            erros.add("O solicitante não pode aprovar a própria requisição.");
        }

        return erros;
    }
}
