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

public class ItemPreListaDAO {
    //CREATE
    public void inserirItemPreLista(ItemPreListaMolde itemPreLista){

        String sql = "INSERT INTO item_pre_lista (fk_pre_lista, fk_fornecedor, fk_produto, qtd) VALUES(?, ?, ?, ?)";

        try(Connection conexao = ConexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, itemPreLista.getFk_pre_lista());
            stmt.setInt(2, itemPreLista.getFk_fornecedor());
            stmt.setInt(3, itemPreLista.getFk_produto());
            stmt.setInt(4, itemPreLista.getQtd());

            stmt.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir um novo item pre-lista no banco de dados", e);
        }
    }
    //READ

    public static final String sqlBase = """
            SELECT i.id_item_lista, f.nome_juridico AS fornecedor, p.nome AS produto, i.qtd AS quantidade
            FROM Item_pre_lista i
            JOIN fornecedor f ON i.fk_fornecedor = f.id_fornecedor
            JOIN produto p ON i.fk_produto = p.id_produto
            """;

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

                    int id_ItemPreLista = rs.getInt("id_item_lista");
                    String fornecedor = rs.getString("fornecedor");
                    String produto = rs.getString("produto");
                    int qtd = rs.getInt("quantidade");

                    ItemPreListaMolde novoItemPreLista = new ItemPreListaMolde(id_ItemPreLista, fornecedor, produto, qtd);
                    listaItensPreLista.add(novoItemPreLista);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar a tabela de itens pre-lista no banco de dados", e);
        }
        return listaItensPreLista;

    }

    public void atualizarItemPreListaCompleto(ItemPreListaMolde itemPreListaMolde, int id_itemPreLista){

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

            stmt.setInt(1, itemPreListaMolde.getFk_pre_lista());
            stmt.setInt(2, itemPreListaMolde.getFk_fornecedor());
            stmt.setInt(3, itemPreListaMolde.getFk_produto());
            stmt.setInt(4, itemPreListaMolde.getQtd());

            stmt.setInt(5, id_itemPreLista);

            stmt.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar item pre-lista", e);
        }
    }

    public void atualizarColunaItemPreLista(String NomeColuna, String novoValor, int id_item_prelista) {
        String sql = ("UPDATE item_pre_lista SET " + NomeColuna + " = ? WHERE id_item_lista = ?");

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            if (NomeColuna.equalsIgnoreCase("fk_pre_lista")) {
                stmt.setInt(1, Integer.parseInt(novoValor));
            }
            else if (NomeColuna.equalsIgnoreCase("fk_fornecedor")) {
                stmt.setInt(1, Integer.parseInt(novoValor));
            }
            else if (NomeColuna.equalsIgnoreCase("fk_produto")) {
                stmt.setInt(1, Integer.parseInt(novoValor));
            }
            else if (NomeColuna.equalsIgnoreCase("qtd")) {
                stmt.setInt(1, Integer.parseInt(novoValor));
            }

            //! Passa o segundo parâmetro, o ID
            stmt.setInt(2, id_item_prelista);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar item pre-lista", e);
        }
    }

    //DELETE
    public void deletarItemPreLista(int id_itemPreLista){

        String sql = "DELETE FROM Item_pre_lista WHERE id_item_lista = ?";

        try(Connection conexao = ConexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, id_itemPreLista);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar item pre-lista", e);
        }
    }

    //FILTRAGENS

    public List<ItemPreListaMolde> listarItensPreLista(Integer limite, Integer offset){
        return consultarItensPreLista(sqlBase.concat(" ORDER BY i.id_item_lista"), limite, offset);
    }

    public List<ItemPreListaMolde> listarItensPreListaPorFornecedores(Integer limite, Integer offset){
        return consultarItensPreLista(sqlBase.concat(" ORDER BY i.id_item_lista"), limite, offset);
    }

    public List<ItemPreListaMolde> listarItensPreListaPorProduto(Integer limite, Integer offset){
        return consultarItensPreLista(sqlBase.concat(" ORDER BY p.nome"), limite, offset);
    }

    public List<ItemPreListaMolde> listarItensPreListaPorQuantidade(boolean ordem, Integer limite, Integer offset) {
        if (ordem) {
            return consultarItensPreLista(sqlBase.concat(" ORDER BY i.qtd ASC"), limite, offset);
        } else {
            return consultarItensPreLista(sqlBase.concat(" ORDER BY i.qtd DESC"), limite, offset);
        }
    }

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

    public int contarItensPreLista(){
        String sql = "SELECT COUNT(*) FROM Item_pre_lista";

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
}
