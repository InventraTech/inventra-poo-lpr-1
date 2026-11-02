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

/**
 * Acesso à tabela requisicao. A listagem junta tipo, solicitante, aprovador e produto para exibir nomes
 * em vez das chaves estrangeiras.
 */
public class RequisicaoDAO {

    // 1. CREATE
    public void inserirRequisicao(RequisicaoMolde requisicao){
        String sql = "INSERT INTO Requisicao (fk_tipo_requisicao, fk_funcionario_solicitante, fk_funcionario_aprovador, fk_produto, quantidade_prod, motivo, status, dt_requisicao) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

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
    public static final String SQL_BASE = """
            SELECT r.id_requisicao, t.tipo AS tipo_requisicao, fs.nome AS funcionario_solicitante, fa.nome AS funcionario_aprovador,
            p.nome AS produto, r.quantidade_prod, r.motivo, r.status, r.dt_requisicao,
            r.fk_tipo_requisicao, r.fk_funcionario_solicitante, r.fk_funcionario_aprovador, r.fk_produto
            FROM requisicao r
            JOIN tipo_requisicao t ON r.fk_tipo_requisicao = t.id_tipo_requisicao
            JOIN funcionario fs ON r.fk_funcionario_solicitante = fs.id_funcionario
            JOIN funcionario fa ON r.fk_funcionario_aprovador = fa.id_funcionario
            JOIN produto p ON r.fk_produto = p.id_produto
            """;

    /**
     * Executa a consulta recebida. Com {@code limite} e {@code offset} informados, aplica a paginação da tela;
     * com algum deles nulo, devolve todos os registros (usado na exportação para CSV).
     */
    public List<RequisicaoMolde> consultarRequisicoes(String sql, Integer limite, Integer offset){
        List<RequisicaoMolde> array = new ArrayList<>();

        boolean comPaginacao = (limite != null && offset != null);
        if (comPaginacao){
            sql = sql.concat(" LIMIT ? OFFSET ?");
        }

        try(Connection conexao = ConexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)){

            if (comPaginacao){
                stmt.setInt(1, limite);
                stmt.setInt(2, offset);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_requisicao");
                    String idTipoRequisicao = rs.getString("tipo_requisicao");
                    String funcionarioSolicitante = rs.getString("funcionario_solicitante");
                    String funcionarioAprovador = rs.getString("funcionario_aprovador");
                    String produto = rs.getString("produto");
                    int qtd = rs.getInt("quantidade_prod");
                    String motivo = rs.getString("motivo");
                    String status = rs.getString("status");
                    LocalDateTime dataHora = rs.getObject("dt_requisicao", LocalDateTime.class);

                    RequisicaoMolde requisicao = new RequisicaoMolde(id, idTipoRequisicao, funcionarioSolicitante, funcionarioAprovador, produto, qtd, motivo, status, dataHora);
                    requisicao.setIdTipoRequisicao(rs.getInt("fk_tipo_requisicao"));
                    requisicao.setFkFuncionarioSolicitante(rs.getInt("fk_funcionario_solicitante"));
                    requisicao.setFkFuncionarioAprovador(rs.getInt("fk_funcionario_aprovador"));
                    requisicao.setFkProduto(rs.getInt("fk_produto"));
                    array.add(requisicao);
                }
            }

        } catch(SQLException e){
            throw new RuntimeException("Erro ao consultar a tabela de requisicoes no banco de dados", e);
        }
        return array;
    }

    // 3. UPDATE

    public void atualizarRequisicaoCompleta(RequisicaoMolde requisicao, int idRequisicao){

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

            stmt.setInt(9, idRequisicao);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar uma requisicao no banco de dados", e);
        }
    }

    /**
     * Atualiza uma única coluna. O nome da coluna é concatenado ao SQL (o "?" não vale para nome de coluna),
     * então o servlet só deve chamá-lo com colunas já conferidas. O valor chega como texto e é convertido conforme a coluna.
     */
    public void atualizarColunaRequisicao(String nomeColuna, String novoValor, int idRequisicao){
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

            stmt.setInt(2, idRequisicao);
            stmt.executeUpdate();
            System.out.println("Requisição atualizada!");

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar uma requisicao no banco de dados", e);
        }
    }

    // 4. DELETE
    public void deletarRequisicao(int idRequisicao){
        String sql = "DELETE FROM requisicao WHERE id_requisicao = ?";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, idRequisicao);
            int linhasExecutadas = stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar uma requisicao no banco de dados", e);
        }
    }

    //MÉTODOS DE LISTAGENS

    public List<RequisicaoMolde> listarRequisicoes(Integer limite, Integer offset){
        return consultarRequisicoes(SQL_BASE.concat("order by r.id_requisicao"), limite, offset);
    }

    public List<RequisicaoMolde> listarRequisicoesPorTipo(Integer limite, Integer offset){
        return consultarRequisicoes(SQL_BASE.concat(" ORDER BY r.fk_tipo_requisicao"), limite, offset);
    }

    public List<RequisicaoMolde> listarRequisicoesPorQuantidadeProduto(boolean ordem, Integer limite, Integer offset) {
        if (ordem) {
            return consultarRequisicoes(SQL_BASE.concat(" ORDER BY r.quantidade_prod ASC"), limite, offset);
        } else {
            return consultarRequisicoes(SQL_BASE.concat(" ORDER BY r.quantidade_prod DESC"), limite, offset);
        }
    }

    public List<RequisicaoMolde> listarRequisicoesPorData(boolean ordem, Integer limite, Integer offset) {
        if (ordem) {
            return consultarRequisicoes(SQL_BASE.concat(" ORDER BY r.dt_requisicao ASC"), limite, offset);
        } else {
            return consultarRequisicoes(SQL_BASE.concat(" ORDER BY r.dt_requisicao DESC"), limite, offset);
        }
    }

    /**
     * Versão sem paginação das listagens, para o CSV. Cada filtro corresponde a uma ordenação da tela;
     * um filtro desconhecido usa a ordem padrão e um filtro nulo devolve {@code null}.
     */
    public List<RequisicaoMolde> listarRequisicaoParaExportar(String filtragem){
        if (filtragem != null) {
            switch (filtragem) {
                case "tipo" -> {
                    return listarRequisicoesPorTipo(null, null);
                }
                case "quantidade_crescente" -> {
                    return listarRequisicoesPorQuantidadeProduto(true, null, null);
                }
                case "quantidade_decrescente" -> {
                    return listarRequisicoesPorQuantidadeProduto(false, null, null);
                }
                case "data_proxima" -> {
                    return listarRequisicoesPorData(true, null, null);
                }
                case "data_distante" -> {
                    return listarRequisicoesPorData(false, null, null);
                }
                default -> {
                    return listarRequisicoes(null, null);
                }
            }
        }
        return null;
    }

    public static final String CONTADOR_BASE = """
            select count(*) from requisicao r
            """;

    public int contarRequisicoes(String sql){

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

    public int contarRequisicoesPadrao(){
        return contarRequisicoes(CONTADOR_BASE);
    }

    public int contarRequisicoesPendentes(){
        return contarRequisicoes(CONTADOR_BASE.concat("where r.status = 'Pendente'"));
    }

    public int contarRequisicoesAprovadas(){
        return contarRequisicoes(CONTADOR_BASE.concat("where r.status = 'Aprovada'"));
    }

    public int contarRequisicoesFiltro(boolean status){
        if (status){
            return contarRequisicoes(CONTADOR_BASE.concat("where r.status = 'Aprovada'"));
        } else {
            return contarRequisicoes(CONTADOR_BASE.concat("where r.status = 'Pendente'"));
        }
    }

    // BUSCAR POR ID (leitura apenas; usado pelo RequisicaoServlet para validar edições por coluna)
    public RequisicaoMolde buscarRequisicaoPorId(int idRequisicao){
        String sql = "SELECT id_requisicao, fk_tipo_requisicao, fk_funcionario_solicitante, fk_funcionario_aprovador, fk_produto, quantidade_prod, motivo, status, dt_requisicao FROM requisicao WHERE id_requisicao = ?";

        try(Connection conexao = ConexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, idRequisicao);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new RequisicaoMolde(
                            rs.getInt("id_requisicao"),
                            rs.getInt("fk_tipo_requisicao"),
                            rs.getInt("fk_funcionario_solicitante"),
                            rs.getInt("fk_funcionario_aprovador"),
                            rs.getInt("fk_produto"),
                            rs.getInt("quantidade_prod"),
                            rs.getString("motivo"),
                            rs.getString("status"),
                            rs.getObject("dt_requisicao", LocalDateTime.class));
                }
            }

        } catch(SQLException e){
            throw new RuntimeException("Erro ao buscar requisicao no banco de dados", e);
        }
        return null;
    }
}
