package org.inventra.dao;

import org.inventra.conexao.ConexaoBanco;
import org.inventra.model.FornecedorMolde;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FornecedorDAO {
    public void inserirFornecedor(FornecedorMolde fornecedor){

        String sql = "INSERT INTO fornecedor (fk_endereco, nome_juridico, cnpj, email, nota_vpq) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            int index = 1;

            stmt.setInt(index++, fornecedor.getFk_endereco());
            stmt.setString(index++, fornecedor.getNome_juridico());
            stmt.setString(index++, fornecedor.getCnpj());
            stmt.setString(index++, fornecedor.getEmail());
            stmt.setInt(index++, fornecedor.getNota_vpq());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir fornecedor no banco de dados", e);
        }
    }

    public static final String sqlBase = """
            SELECT f.id_fornecedor, f.nome_juridico, f.cnpj, f.email, f.nota_vpq, CONCAT(e.estado, ' | ', e.cidade) AS endereco
            FROM fornecedor f
            JOIN endereco e ON f.fk_endereco = e.id_endereco
            """;

    public List<FornecedorMolde> consultarFornecedor(String sql, Integer limite, Integer offset){
        List<FornecedorMolde> listaFornecedores = new ArrayList<>();

        //Se precisar de paginação vai para a tela do usuário, se não o exportar pega todos(para o arquivo CSV)
        boolean comPaginacao = (limite != null && offset != null);
        if (comPaginacao){
            sql = sql.concat(" LIMIT ? OFFSET ?");
        }

        try(Connection conexao = ConexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)) {

            if (comPaginacao){
                stmt.setInt(1, limite);
                stmt.setInt(2, offset);
            }

            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_fornecedor");
                    String nome_juridico = rs.getString("nome_juridico");
                    String endereco = rs.getString("endereco");
                    String cnpj = rs.getString("cnpj");
                    String email = rs.getString("email");
                    int nota_vpq = rs.getInt("nota_vpq");

                    FornecedorMolde novoFornecedor = new FornecedorMolde(id, endereco, nome_juridico, cnpj, email, nota_vpq);
                    listaFornecedores.add(novoFornecedor);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar a tabela de fornecedores no banco de dados", e);
        }
        return listaFornecedores;
    }

    public void atualizarFornecedorCompleto(FornecedorMolde fornecedor, int id_fornecedor){

        String sql = """
                UPDATE fornecedor
                SET fk_endereco = ?,
                    nome_juridico = ?,
                	cnpj = ?,
                	email = ?,
                	nota_vpq = ?
                WHERE id_fornecedor = ?
                """;

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)){

            int index = 1;

            stmt.setInt(index++, fornecedor.getFk_endereco());
            stmt.setString(index++, fornecedor.getNome_juridico());
            stmt.setString(index++, fornecedor.getCnpj());
            stmt.setString(index++, fornecedor.getEmail());
            stmt.setInt(index++, fornecedor.getNota_vpq());

            stmt.setInt(index++, id_fornecedor);

            stmt.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar fornecedor", e);
        }

    }

    //Metodo que recebe uma coluna especifica e um novo valor, editando apenas uma, ideal para alterações simples.
    public void atualizarColunaFornecedor(String NomeColuna, String novoValor, int id_fornecedor) {
        String sql = ("UPDATE fornecedor SET " + NomeColuna + " = ? WHERE id_fornecedor = ?");

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            if (NomeColuna.equalsIgnoreCase("nome_juridico")) {
                stmt.setString(1, novoValor);
            }
            else if (NomeColuna.equalsIgnoreCase("fk_endereco")) {
                stmt.setInt(1, Integer.parseInt(novoValor));
            }
            else if (NomeColuna.equalsIgnoreCase("cnpj")) {
                stmt.setString(1, novoValor);
            }
            else if (NomeColuna.equalsIgnoreCase("email")) {
                stmt.setString(1, novoValor);
            }
            else if (NomeColuna.equalsIgnoreCase("nota_vpq")) {
                stmt.setInt(1, Integer.parseInt(novoValor));
            }

            //! Passa o segundo parâmetro, o ID
            stmt.setInt(2, id_fornecedor);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar fornecedor", e);
        }
    }

    public void deletarFornecedor(int id_fornecedor){
        String sql = "DELETE FROM fornecedor WHERE id_fornecedor = ?";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, id_fornecedor);

            int linhasExecutadas = stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar fornecedor", e);
        }
    }

    public List<FornecedorMolde> listarFornecedor(Integer limite, Integer offset){
        return consultarFornecedor(sqlBase.concat("order by f.id_fornecedor"), limite, offset);
    }

    public List<FornecedorMolde> listarFornecedorNotaVPQ(boolean ordem, Integer limite, Integer offset) {
        if (ordem) {
            return consultarFornecedor(sqlBase.concat(" ORDER BY f.nota_vpq ASC"), limite, offset);
        } else {
            return consultarFornecedor(sqlBase.concat(" ORDER BY f.nota_vpq DESC"), limite, offset);
        }
    }

    public List<FornecedorMolde> listarFornecedorParaExportar(String filtragem){
        if (filtragem != null) {
            switch (filtragem) {
                case "notaVPQ_crescente" -> {
                    return listarFornecedorNotaVPQ(true, null, null);
                }
                case "notaVPQ_decrescente" -> {
                    return listarFornecedorNotaVPQ(false, null, null);
                }
                default -> {
                    return listarFornecedor(null, null);
                }
            }
        }
        return null;
    }


    public double funcaoDeGrupoFornecedor(String sql){
        try(Connection conexao = ConexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0.0;
    }

    public int contarFornecedores(){
        return (int) funcaoDeGrupoFornecedor("select count(*) from fornecedor");
    }

    public double mediaNotaVPQFornecedores(){
        return funcaoDeGrupoFornecedor("select round(AVG(f.nota_vpq)::numeric, 2) from fornecedor f");
    }

    public int contarProdutosVinculados(){
        return (int) funcaoDeGrupoFornecedor("select count(distinct p.fk_fornecedor) from produto p where p.fk_fornecedor is not null");
    }


}

