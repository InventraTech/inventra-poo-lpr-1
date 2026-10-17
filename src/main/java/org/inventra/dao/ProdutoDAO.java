package org.inventra.dao;

import org.inventra.conexao.ConexaoBanco;
import org.inventra.model.ProdutoMolde;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {
    // 1.CREATE
    public void inserirProduto(ProdutoMolde produto) {

        String sql = "INSERT INTO produto (fk_fornecedor, fk_marca, fk_categoria, nome, unidade_medida, estoque_min, estoque_max, descricao, ativo) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            int index = 1;

            stmt.setInt(index++, produto.getFk_fornecedor());
            stmt.setInt(index++, produto.getFk_marca());
            stmt.setInt(index++, produto.getFk_categoria());
            stmt.setString(index++, produto.getNome());
            stmt.setString(index++, produto.getUnidadeMedida());
            stmt.setInt(index++, produto.getEstoqueMin());
            stmt.setInt(index++, produto.getEstoqueMax());
            stmt.setString(index++, produto.getDescricao());
            stmt.setBoolean(index++, produto.isAtivo());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir um novo produto no banco de dados", e);
        }
    }

    //2 SELECT

    //SQL base que será modificado para cada uma das filtragens posteriormente
    public static final String sqlBase = """
            SELECT p.id_produto, f.nome_juridico AS nome_fornecedor, m.nome AS nome_marca, c.nome AS nome_categoria,
                       p.nome, p.unidade_medida, p.estoque_min, p.estoque_max, p.descricao, p.ativo
                FROM produto p
                JOIN marca m ON p.fk_marca = m.id_marca
                JOIN categoria c ON p.fk_categoria = c.id_categoria
                JOIN fornecedor f ON p.fk_fornecedor = f.id_fornecedor
            """;

    public List<ProdutoMolde> consultaProdutos(String sql) {
        List<ProdutoMolde> listaProdutos = new ArrayList<>();

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_produto");
                String nome = rs.getString("nome");
                String marca = rs.getString("nome_marca");
                String categoria = rs.getString("nome_categoria");
                String unidadeMedida = rs.getString("unidade_medida");
                int estoqueMin = rs.getInt("estoque_min");
                int estoqueMax = rs.getInt("estoque_max");
                String fornecedor = rs.getString("nome_fornecedor");
                String descricao = rs.getString("descricao");
                boolean ativo = rs.getBoolean("ativo");

                ProdutoMolde produto = new ProdutoMolde(id, fornecedor, marca, categoria, nome, unidadeMedida, estoqueMin, estoqueMax, descricao, ativo);
                listaProdutos.add(produto);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar a tabela de produtos no banco de dados", e);
        }
        return listaProdutos;

    }

    // 3.UPDATE
    //Metodo que recebe o molde completo com todos os atributos do produto, assim alterando tudo.
    public void atualizarProdutoCompleto(ProdutoMolde produto, int id_produto) {
        String sql = """
                UPDATE produto
                SET fk_fornecedor = ?,
                    fk_marca = ?,
                	fk_categoria = ?,
                	nome = ?,
                	unidade_medida = ?,
                	estoque_min = ?,
                	estoque_max = ?,
                	descricao = ?,
                	ativo = ?
                WHERE id_produto = ?
                """;

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            int index = 1;

            stmt.setInt(index++, produto.getFk_fornecedor());
            stmt.setInt(index++, produto.getFk_marca());
            stmt.setInt(index++, produto.getFk_categoria());
            stmt.setString(index++, produto.getNome());
            stmt.setString(index++, produto.getUnidadeMedida());
            stmt.setInt(index++, produto.getEstoqueMin());
            stmt.setInt(index++, produto.getEstoqueMax());
            stmt.setString(index++, produto.getDescricao());
            stmt.setBoolean(index++, produto.isAtivo());

            stmt.setInt(index++, id_produto);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar um produto no banco de dados", e);
        }
    }

    //Metodo que recebe uma coluna especifica e um novo valor, editando apenas uma, ideal para alterações simples.
    public void atualizarColunaProduto(String nomeColuna, String novoValor, int id_produto) {

        String sql = ("UPDATE produto SET " + nomeColuna + " = ? WHERE id_produto = ?");

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            if (nomeColuna.equalsIgnoreCase("FK_fornecedor")) {
                stmt.setInt(1, Integer.parseInt(novoValor));
            } else if (nomeColuna.equalsIgnoreCase("FK_marca")) {
                stmt.setInt(1, Integer.parseInt(novoValor));
            } else if (nomeColuna.equalsIgnoreCase("FK_categoria")) {
                stmt.setInt(1, Integer.parseInt(novoValor));
            } else if (nomeColuna.equalsIgnoreCase("nome")) {
                stmt.setString(1, novoValor);
            } else if (nomeColuna.equalsIgnoreCase("Unidade_medida")) {
                stmt.setString(1, novoValor);
            } else if (nomeColuna.equalsIgnoreCase("estoque_min")) {
                stmt.setInt(1, Integer.parseInt(novoValor));
            } else if (nomeColuna.equalsIgnoreCase("estoque_max")) {
                stmt.setInt(1, Integer.parseInt(novoValor));
            } else if (nomeColuna.equalsIgnoreCase("descricao")) {
                stmt.setString(1, novoValor);
            }

            stmt.setInt(2, id_produto);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar um produto no banco de dados", e);
        }


    }

    // 4.DELETE
    public void deletarProduto(int id_produto) {
        String sql = "DELETE FROM produto WHERE id_produto = ?";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id_produto);

            int linhasExecutadas = stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar um produto no banco de dados", e);
        }
    }

    //FILTROS

    public List<ProdutoMolde> listarProdutos() {
        return consultaProdutos(sqlBase.concat(" ORDER BY p.id_produto asc"));
    }

    public List<ProdutoMolde> listarProdutosQuantidade() {
        return consultaProdutos(sqlBase.concat(" ORDER BY l.qtd_atual desc"));
    }

    public List<ProdutoMolde> listarProdutosPorMarca() {
        return consultaProdutos(sqlBase.concat(" ORDER BY m.nome, p.nome asc"));
    }

    public List<ProdutoMolde> listarProdutosAlfabetica(boolean ordem) {
        if (ordem) {
            return consultaProdutos(sqlBase.concat(" order by p.nome ASC"));
        } else {
            return consultaProdutos(sqlBase.concat(" order by p.nome DESC"));
        }
    }

    public List<ProdutoMolde> listarProdutosPorData(boolean ordem) {
        if (ordem) {
            return consultaProdutos(sqlBase.concat(" ORDER BY l.dt_validade asc"));
        } else {
            return consultaProdutos(sqlBase.concat(" ORDER BY l.dt_validade desc"));
        }
    }
}
