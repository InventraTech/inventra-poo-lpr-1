package org.inventra.dao;

import org.inventra.conexao.ConexaoBanco;
import org.inventra.model.ProdutoMolde;
import org.inventra.model.RequisicaoMolde;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RequisicaoDAO {

    // 1. CREATE
    public void inserirRequisicao(RequisicaoMolde requisicao){
        String sql = "INSERT INTO requisicao (fk_tipo_requisicao, fk_funcionario_solicitante, fk_funcionario_aprovador, fk_produto, quantidade_prod, motivo, status, dt_requisicao) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection conexao = ConexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, requisicao.getIdTipoRequisicao());
            stmt.setInt(2, requisicao.getFkFuncionarioSolicitante());
            stmt.setInt(3, requisicao.getFkFuncionarioAprovador());
            stmt.setInt(4, requisicao.getFkProduto());
            stmt.setInt(5, requisicao.getQuantidadeProduto());
            stmt.setString(6, requisicao.getMotivo());
            stmt.setString(7, requisicao.getStatus());
            stmt.setObject(8, requisicao.getDataHora());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir uma nova requisicao no banco de dados", e);
        }
    }

    // 2. READ

    //SQL base:
    public static final String sqlBase = """
            SELECT r.id_requisicao, t.tipo AS tipo_requisicao, fs.nome AS funcionario_solicitante, fa.nome AS funcionario_aprovador,
            p.nome AS produto, r.quantidade_prod, r.motivo, r.status, r.dt_requisicao
            FROM requisicao r
            JOIN tipo_requisicao t ON r.fk_tipo_requisicao = t.id_tipo_requisicao
            JOIN funcionario fs ON r.fk_funcionario_solicitante = fs.id_funcionario
            JOIN funcionario fa ON r.fk_funcionario_aprovador = fa.id_funcionario
            JOIN produto p ON r.fk_produto = p.id_produto
            """;

    public List<RequisicaoMolde> consultarRequisicoes(String sql){
        List<RequisicaoMolde> array = new ArrayList<>();

        try(Connection conexao = ConexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){

            while (rs.next()){
                int id = rs.getInt("id_requisicao");
                String idTipoRequisicao = rs.getString("tipo_requisicao");
                String funcionarioSolicitante = rs.getString("funcionario_solicitante");
                String funcionarioAprovador = rs.getString("funcionario_aprovador");
                String produto = rs.getString("produto");
                int qtd = rs.getInt("quantidade_prod");
                String motivo = rs.getString("motivo");
                String status = rs.getString("status");

                // Lê a data e a hora individualmente e as combina em um LocalDateTime
                LocalDateTime dataHora = rs.getObject("dt_requisicao", LocalDateTime.class);

                RequisicaoMolde requisicao = new RequisicaoMolde(id, idTipoRequisicao, funcionarioSolicitante, funcionarioAprovador, produto, qtd, motivo, status, dataHora);
                array.add(requisicao);
            }

        } catch(SQLException e){
            throw new RuntimeException("Erro ao consultar a tabela de requisicoes no banco de dados", e);
        }
        return array;
    }

    // 3. UPDATE

    public void atualizarRequisicaoCompleta(RequisicaoMolde requisicao, int id_requisicao){

        String sql = """
                UPDATE requisicao
                SET fk_tipo_requisicao = ?,
                	fk_funcionario_solicitante = ?,
                	fk_funcionario_aprovador = ?,
                	fk_produto = ?,
                	quantidade_prod = ?,
                	motivo = ?,
                	status = ?,
                	dt_requisicao = ?
                WHERE id_requisicao = ?
                """;

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, requisicao.getIdTipoRequisicao());
            stmt.setInt(2, requisicao.getFkFuncionarioSolicitante());
            stmt.setInt(3, requisicao.getFkFuncionarioAprovador());
            stmt.setInt(4, requisicao.getFkProduto());
            stmt.setInt(5, requisicao.getQuantidadeProduto());
            stmt.setString(6, requisicao.getMotivo());
            stmt.setString(7, requisicao.getStatus());
            stmt.setObject(8, requisicao.getDataHora());

            stmt.setInt(9, id_requisicao);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar uma requisicao no banco de dados", e);
        }
    }

    //Metodo que recebe uma coluna especifica e um novo valor, editando apenas uma, ideal para alterações simples.
    public void atualizarColunaRequisicao(String nomeColuna, String novoValor, int id_requisicao){
        String sql = "UPDATE requisicao SET " + nomeColuna + " = ? WHERE id_requisicao = ?";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)){

            if(nomeColuna.equalsIgnoreCase("fk_tipo_requisicao")){
                stmt.setInt(1, Integer.parseInt(novoValor));
            }
            else if (nomeColuna.equalsIgnoreCase("quantidade_prod")){
                stmt.setInt(1, Integer.parseInt(novoValor));
            }
            else if (nomeColuna.equalsIgnoreCase("motivo")){
                stmt.setString(1, novoValor);
            }
            else if (nomeColuna.equalsIgnoreCase("status")){
                stmt.setString(1, novoValor);
            }
            else if (nomeColuna.equalsIgnoreCase("dt_requisicao")){
                LocalDateTime data = LocalDateTime.parse(novoValor);
                stmt.setObject(1, data);
            }
            else if (nomeColuna.equalsIgnoreCase("fk_funcionario_solicitante")){
                stmt.setInt(1, Integer.parseInt(novoValor));
            }
            else if (nomeColuna.equalsIgnoreCase("fk_funcionario_aprovador")){
                stmt.setInt(1, Integer.parseInt(novoValor));
            }
            else if (nomeColuna.equalsIgnoreCase("fk_produto")){
                stmt.setInt(1, Integer.parseInt(novoValor));
            }

            stmt.setInt(2, id_requisicao);
            stmt.executeUpdate();
            System.out.println("Requisição atualizada!");

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar uma requisicao no banco de dados", e);
        }
    }

    // 4. DELETE
    public void deletarRequisicao(int id_requisicao){
        String sql = "DELETE FROM requisicao WHERE id_requisicao = ?";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, id_requisicao);
            int linhasExecutadas = stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar uma requisicao no banco de dados", e);
        }
    }

    //MÉTODOS DE LISTAGENS

    public List<RequisicaoMolde> listarRequisicoes(){
        return consultarRequisicoes(sqlBase.concat("order by r.id_requisicao"));
    }

    public List<RequisicaoMolde> listarRequisicoesPorTipo(){
        return consultarRequisicoes(sqlBase.concat(" ORDER BY r.fk_tipo_requisicao"));
    }

    public List<RequisicaoMolde> listarRequisicoesPorQuantidadeProduto(boolean ordem) {
        if (ordem) {
            return consultarRequisicoes(sqlBase.concat(" ORDER BY r.quantidade_prod ASC"));
        } else {
            return consultarRequisicoes(sqlBase.concat(" ORDER BY r.quantidade_prod DESC"));
        }
    }

    public List<RequisicaoMolde> listarRequisicoesPorData(boolean ordem) {
        if (ordem) {
            return consultarRequisicoes(sqlBase.concat(" ORDER BY r.dt_requisicao ASC"));
        } else {
            return consultarRequisicoes(sqlBase.concat(" ORDER BY r.dt_requisicao DESC"));
        }
    }
}