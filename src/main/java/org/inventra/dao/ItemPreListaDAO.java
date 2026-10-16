package org.inventra.dao;

import org.inventra.conexao.ConexaoBanco;
import org.inventra.model.FornecedorMolde;
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
            throw new RuntimeException(e);
        }
    }
    //READ

    public static final String sqlBase = """
            SELECT i.id_item_lista, f.nome_juridico AS fornecedor, p.nome AS produto, i.qtd AS quantidade
            FROM Item_pre_lista i
            JOIN fornecedor f ON i.fk_fornecedor = f.id_fornecedor
            JOIN produto p ON i.fk_produto = p.id_produto
            """;

    public List<ItemPreListaMolde> consultarItensPreLista(String sql){
        List<ItemPreListaMolde> listaItensPreLista = new ArrayList<>();

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()){

            while(rs.next()){

                int id_ItemPreLista = rs.getInt("id_item_lista");
                String fornecedor = rs.getString("fornecedor");
                String produto = rs.getString("produto");
                int qtd = rs.getInt("quantidade");

                ItemPreListaMolde novoItemPreLista = new ItemPreListaMolde(id_ItemPreLista, fornecedor, produto, qtd);
                listaItensPreLista.add(novoItemPreLista);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
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
            System.out.println("Item da pré-lista atualizado!");

        } catch (SQLException erro) {
            System.out.println("Erro ao se conectar no banco.");
            erro.printStackTrace();
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
            throw new RuntimeException(e);
        }
    }

    //FILTRAGENS

    public List<ItemPreListaMolde> listarItensPreLista(){
        return consultarItensPreLista(sqlBase.concat(" ORDER BY i.id_item_lista"));
    }

    public List<ItemPreListaMolde> listarItensPreListaPorFornecedores(){
        return consultarItensPreLista(sqlBase.concat(" ORDER BY i.id_item_lista"));
    }

    public List<ItemPreListaMolde> listarItensPreListaPorProduto(){
        return consultarItensPreLista(sqlBase.concat(" ORDER BY p.nome"));
    }

    public List<ItemPreListaMolde> listarItensPreListaPorQuantidade(boolean ordem) {
        if (ordem) {
            return consultarItensPreLista(sqlBase.concat(" ORDER BY i.qtd ASC"));
        } else {
            return consultarItensPreLista(sqlBase.concat(" ORDER BY i.qtd DESC"));
        }
    }
}
