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

    public List<ProdutoMolde> consultaProdutos(String sql, Integer limite, Integer offset) {
        List<ProdutoMolde> listaProdutos = new ArrayList<>();

        //Se precisar de paginação vai para a tela do usuário, se não o exportar pega todos(para o arquivo CSV)
        boolean comPaginacao = (limite != null && offset != null);
        if (comPaginacao){
            sql = sql.concat(" LIMIT ? OFFSET ?");
        }

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            if (comPaginacao){
                stmt.setInt(1, limite);
                stmt.setInt(2, offset);
            }

            try (ResultSet rs = stmt.executeQuery()) {
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

    public List<ProdutoMolde> listarProdutos(Integer limite, Integer offset) {
        return consultaProdutos(sqlBase.concat(" ORDER BY p.id_produto asc"), limite, offset);
    }

    public List<ProdutoMolde> listarProdutosQuantidade(Integer limite, Integer offset) {
        return consultaProdutos(sqlBase.concat(" ORDER BY l.qtd_atual desc"), limite, offset);
    }

    public List<ProdutoMolde> listarProdutosPorMarca(Integer limite, Integer offset) {
        return consultaProdutos(sqlBase.concat(" ORDER BY m.nome, p.nome asc"), limite, offset);
    }

    public List<ProdutoMolde> listarProdutosAlfabetica(boolean ordem, Integer limite, Integer offset) {
        if (ordem) {
            return consultaProdutos(sqlBase.concat(" order by p.nome ASC"), limite, offset);
        } else {
            return consultaProdutos(sqlBase.concat(" order by p.nome DESC"), limite, offset);
        }
    }

    public List<ProdutoMolde> listarProdutosPorData(boolean ordem, Integer limite, Integer offset) {
        if (ordem) {
            return consultaProdutos(sqlBase.concat(" ORDER BY l.dt_validade asc"), limite, offset);
        } else {
            return consultaProdutos(sqlBase.concat(" ORDER BY l.dt_validade desc"), limite, offset);
        }
    }

    public List<ProdutoMolde> listarProdutosParaExportar(String filtragem){
        if (filtragem != null) {
            switch (filtragem) {
                case "validade_proxima" -> {
                    return listarProdutosPorData(true, null, null);
                }
                case "validade_distante" -> {
                    return listarProdutosPorData(false, null, null);
                }
                case "az" -> {
                    return listarProdutosAlfabetica(true, null, null);
                }
                case "za" -> {
                    return listarProdutosAlfabetica(false, null, null);
                }
                case "quantidade" -> {
                    return listarProdutosQuantidade(null, null);
                }
                case "marca" -> {
                    return listarProdutosPorMarca(null, null);
                }
                default -> {
                    return listarProdutos(null, null);
                }
            }
        }
        return null;
    }

    public int contarProdutos(){
        String sql = "SELECT COUNT(*) FROM produto";

        try(Connection conexao = ConexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}
