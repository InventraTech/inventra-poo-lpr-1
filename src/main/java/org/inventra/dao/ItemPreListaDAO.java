package org.inventra.dao;

import org.inventra.conexao.ConexaoBanco;
import org.inventra.model.FornecedorMolde;
import org.inventra.model.FuncionarioMolde;
import org.inventra.model.ItemPreListaMolde;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso aos itens da pré-lista de compras. A listagem junta fornecedor e produto para mostrar os nomes
 * em vez das chaves estrangeiras.
 */
public class ItemPreListaDAO {
    //CREATE
    public void inserirItemPreLista(ItemPreListaMolde itemPreLista){

        String sql = "INSERT INTO item_pre_lista (fk_pre_lista, fk_fornecedor, fk_produto, qtd) VALUES(?, ?, ?, ?)";

        try(Connection conexao = ConexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, itemPreLista.getFkPreLista());
            stmt.setInt(2, itemPreLista.getFkFornecedor());
            stmt.setInt(3, itemPreLista.getFkProduto());
            stmt.setInt(4, itemPreLista.getQtd());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir um novo item pre-lista no banco de dados", e);
        }
    }
    //READ

    public static final String SQL_BASE = """
            SELECT i.id_item_lista, f.nome_juridico AS fornecedor, p.nome AS produto, i.qtd AS quantidade, i.fk_pre_lista, i.fk_fornecedor, i.fk_produto
            FROM Item_pre_lista i
            JOIN fornecedor f ON i.fk_fornecedor = f.id_fornecedor
            JOIN produto p ON i.fk_produto = p.id_produto
            """;

    /**
     * Executa a consulta recebida. Com {@code limite} e {@code offset} informados, aplica a paginação da tela;
     * com algum deles nulo, devolve todos os registros (usado na exportação para CSV).
     */
    public List<ItemPreListaMolde> consultarItensPreLista(String sql, Integer limite, Integer offset){
        List<ItemPreListaMolde> listaItensPreLista = new ArrayList<>();

        //Se precisar de paginação vai para a tela do usuário, se não o exportar pega todos(para o arquivo CSV)
        boolean comPaginacao = (limite != null && offset != null);
        if (comPaginacao){
            sql = sql.concat(" LIMIT ? OFFSET ?");
        }

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)){

            if (comPaginacao){
                stmt.setInt(1, limite);
                stmt.setInt(2, offset);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    int idItemPreLista = rs.getInt("id_item_lista");
                    String fornecedor = rs.getString("fornecedor");
                    String produto = rs.getString("produto");
                    int qtd = rs.getInt("quantidade");

                    ItemPreListaMolde novoItemPreLista = new ItemPreListaMolde(idItemPreLista, fornecedor, produto, qtd);
                    novoItemPreLista.setFkPreLista(rs.getInt("fk_pre_lista"));
                    novoItemPreLista.setFkFornecedor(rs.getInt("fk_fornecedor"));
                    novoItemPreLista.setFkProduto(rs.getInt("fk_produto"));
                    listaItensPreLista.add(novoItemPreLista);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar a tabela de itens pre-lista no banco de dados", e);
        }
        return listaItensPreLista;

    }

    public void atualizarItemPreListaCompleto(ItemPreListaMolde itemPreListaMolde, int idItemPreLista){

        String sql = """
                UPDATE item_pre_lista
                SET fk_pre_lista = ?,
                	fk_fornecedor = ?,
                	fk_produto = ?,
                	qtd = ?
                WHERE id_item_lista = ?
                """;

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, itemPreListaMolde.getFkPreLista());
            stmt.setInt(2, itemPreListaMolde.getFkFornecedor());
            stmt.setInt(3, itemPreListaMolde.getFkProduto());
            stmt.setInt(4, itemPreListaMolde.getQtd());

            stmt.setInt(5, idItemPreLista);

            stmt.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar item pre-lista", e);
        }
    }

    /**
     * Atualiza uma única coluna. O nome da coluna é concatenado ao SQL (o "?" não vale para nome de coluna),
     * então o servlet só deve chamá-lo com colunas já conferidas. O valor chega como texto e é convertido conforme a coluna.
     */
    public void atualizarColunaItemPreLista(String nomeColuna, String novoValor, int idItemPreLista) {
        String sql = ("UPDATE item_pre_lista SET " + nomeColuna + " = ? WHERE id_item_lista = ?");

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            if (nomeColuna.equalsIgnoreCase("fk_pre_lista")) {
                stmt.setInt(1, Integer.parseInt(novoValor));
            }
            else if (nomeColuna.equalsIgnoreCase("fk_fornecedor")) {
                stmt.setInt(1, Integer.parseInt(novoValor));
            }
            else if (nomeColuna.equalsIgnoreCase("fk_produto")) {
                stmt.setInt(1, Integer.parseInt(novoValor));
            }
            else if (nomeColuna.equalsIgnoreCase("qtd")) {
                stmt.setInt(1, Integer.parseInt(novoValor));
            }

            //! Passa o segundo parâmetro, o ID
            stmt.setInt(2, idItemPreLista);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar item pre-lista", e);
        }
    }

    //DELETE
    public void deletarItemPreLista(int idItemPreLista){

        String sql = "DELETE FROM Item_pre_lista WHERE id_item_lista = ?";

        try(Connection conexao = ConexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, idItemPreLista);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar item pre-lista", e);
        }
    }

    //FILTRAGENS

    public List<ItemPreListaMolde> listarItensPreLista(Integer limite, Integer offset){
        return consultarItensPreLista(SQL_BASE.concat(" ORDER BY i.id_item_lista"), limite, offset);
    }

    public List<ItemPreListaMolde> listarItensPreListaPorFornecedores(Integer limite, Integer offset){
        return consultarItensPreLista(SQL_BASE.concat(" ORDER BY i.id_item_lista"), limite, offset);
    }

    public List<ItemPreListaMolde> listarItensPreListaPorProduto(Integer limite, Integer offset){
        return consultarItensPreLista(SQL_BASE.concat(" ORDER BY p.nome"), limite, offset);
    }

    public List<ItemPreListaMolde> listarItensPreListaPorQuantidade(boolean ordem, Integer limite, Integer offset) {
        if (ordem) {
            return consultarItensPreLista(SQL_BASE.concat(" ORDER BY i.qtd ASC"), limite, offset);
        } else {
            return consultarItensPreLista(SQL_BASE.concat(" ORDER BY i.qtd DESC"), limite, offset);
        }
    }

    /**
     * Versão sem paginação das listagens, para o CSV. Cada filtro corresponde a uma ordenação da tela;
     * um filtro desconhecido usa a ordem padrão e um filtro nulo devolve {@code null}.
     */
    public List<ItemPreListaMolde> listarItemPreListaParaExportar(String filtragem){
        if (filtragem != null){
            switch (filtragem){
                case "fornecedor" -> {
                    return listarItensPreListaPorFornecedores(null, null);
                }
                case "produto" -> {
                    return listarItensPreListaPorProduto(null, null);
                }
                case "quantidade_crescente" -> {
                    return listarItensPreListaPorQuantidade(true, null, null);
                }
                case "quantidade_decrescente" -> {
                    return listarItensPreListaPorQuantidade(false, null, null);
                }
                default -> {
                    return listarItensPreLista(null, null);
                }
            }
        }
        return null;
    }

    public int contarItensPreLista(String sql){

        try(Connection conexao = ConexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch(SQLException e){
            throw new RuntimeException(e);
        }
        return 0;
    }

    public int contarItensPreListaPadrao(){
        return contarItensPreLista("select count(*) from item_pre_lista");
    }

    public int somarUnidadesItensPreListaPadrao(){
        return contarItensPreLista("select sum(qtd) from item_pre_lista ipl");
    }

    public int contarFornecedoresVinculados(){
        return contarItensPreLista("""
                SELECT COUNT(DISTINCT f.nome_juridico) AS total_fornecedores
                FROM Item_pre_lista i
                JOIN fornecedor f ON i.fk_fornecedor = f.id_fornecedor;
                """);
    }

    public ItemPreListaMolde buscarItemPreListaPorId(int idItemPreLista){
        String sql = "SELECT id_item_lista, fk_pre_lista, fk_fornecedor, fk_produto, qtd FROM item_pre_lista WHERE id_item_lista = ?";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, idItemPreLista);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ItemPreListaMolde(
                            rs.getInt("id_item_lista"),
                            rs.getInt("fk_pre_lista"),
                            rs.getInt("fk_fornecedor"),
                            rs.getInt("fk_produto"),
                            rs.getInt("qtd"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar item pre-lista no banco de dados", e);
        }
        return null;
    }
}
