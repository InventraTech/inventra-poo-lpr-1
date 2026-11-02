package org.inventra.dao;

import org.inventra.conexao.ConexaoBanco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Opções das chaves estrangeiras para as listas de seleção dos formulários.
 * Cada método devolve um mapa ID -> descrição, na ordem em que as opções aparecem na tela.
 * O LinkedHashMap mantém a ordem da consulta.
 */
public class OpcoesDAO {

    private OpcoesDAO() {
    }

    public static Map<Integer, String> enderecos() {
        return consultar("SELECT id_endereco, CONCAT(estado, ' | ', cidade) FROM endereco ORDER BY estado, cidade");
    }

    public static Map<Integer, String> fornecedores() {
        return consultar("SELECT id_fornecedor, nome_juridico FROM fornecedor ORDER BY nome_juridico");
    }

    public static Map<Integer, String> marcas() {
        return consultar("SELECT id_marca, nome FROM marca ORDER BY nome");
    }

    public static Map<Integer, String> categorias() {
        return consultar("SELECT id_categoria, nome FROM categoria ORDER BY nome");
    }

    public static Map<Integer, String> produtos() {
        return consultar("SELECT id_produto, nome FROM produto ORDER BY nome");
    }

    public static Map<Integer, String> funcionarios() {
        return consultar("SELECT id_funcionario, nome FROM funcionario ORDER BY nome");
    }

    public static Map<Integer, String> tiposRequisicao() {
        return consultar("SELECT id_tipo_requisicao, tipo FROM tipo_requisicao ORDER BY tipo");
    }

    public static Map<Integer, String> setores() {
        return consultar("SELECT id_setor, nome FROM setor ORDER BY nome");
    }

    private static Map<Integer, String> consultar(String sql) {
        Map<Integer, String> opcoes = new LinkedHashMap<>();
        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                opcoes.put(rs.getInt(1), rs.getString(2));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar as opções de seleção no banco de dados", e);
        }
        return opcoes;
    }
}
