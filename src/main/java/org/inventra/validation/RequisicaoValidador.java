
package org.inventra.validation;

import org.inventra.model.RequisicaoMolde;
import java.util.ArrayList;
import java.util.List;

/**
 * Regras de requisição: tipo, produto, quantidade, motivo, status e solicitante obrigatórios.
 * O aprovador é opcional (zero), mas, quando informado, não pode ser o próprio solicitante.
 */
public class RequisicaoValidador {

    /**
     * Confere os dados já montados a partir dos parâmetros HTTP e devolve as inconsistências encontradas.
     *
     * @return mensagens de erro prontas para exibir ao usuário; lista vazia quando os dados são válidos
     */
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

        // Status (apenas os valores oferecidos no formulário)
        if (requisicao.getStatus() == null || requisicao.getStatus().isBlank()) {
            erros.add("O status da requisição é obrigatório.");

        } else if (!requisicao.getStatus().equals("Pendente")
                && !requisicao.getStatus().equals("Aprovada")
                && !requisicao.getStatus().equals("Recusada")) {
            erros.add("O status da requisição deve ser Pendente, Aprovada ou Recusada.");
        }

        // Data e hora
        if (requisicao.getDataHora() == null) {
            erros.add("A data e hora da requisição são obrigatórias.");
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
