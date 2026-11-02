package org.inventra.dao;

import org.inventra.conexao.ConexaoBanco;
import org.inventra.model.FornecedorMolde;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso à tabela fornecedor. A listagem junta a tabela endereco e entrega o endereço já montado como
 * "estado | cidade", que é o que as telas e o CSV mostram no lugar da chave estrangeira.
 */
public class FornecedorDAO {
    public void inserirFornecedor(FornecedorMolde fornecedor){

        String sql = "INSERT INTO fornecedor (fk_endereco, nome_juridico, cnpj, email, nota_vpq) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            int index = 1;

            stmt.setInt(index++, fornecedor.getFkEndereco());
            stmt.setString(index++, fornecedor.getNomeJuridico());
            stmt.setString(index++, fornecedor.getCnpj());
            stmt.setString(index++, fornecedor.getEmail());
            stmt.setInt(index++, fornecedor.getNotaVpq());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir fornecedor no banco de dados", e);
        }
    }

    public static final String SQL_BASE = """
            SELECT f.id_fornecedor, f.nome_juridico, f.cnpj, f.email, f.nota_vpq, CONCAT(e.estado, ' | ', e.cidade) AS endereco
            FROM fornecedor f
            JOIN endereco e ON f.fk_endereco = e.id_endereco
            """;

    /**
     * Executa a consulta recebida. Com {@code limite} e {@code offset} informados, aplica a paginação da tela;
     * com algum deles nulo, devolve todos os registros (usado na exportação para CSV).
     */
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
                    String nomeJuridico = rs.getString("nome_juridico");
                    String endereco = rs.getString("endereco");
                    String cnpj = rs.getString("cnpj");
                    String email = rs.getString("email");
                    int notaVpq = rs.getInt("nota_vpq");

                    FornecedorMolde novoFornecedor = new FornecedorMolde(id, endereco, nomeJuridico, cnpj, email, notaVpq);
                    listaFornecedores.add(novoFornecedor);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar a tabela de fornecedores no banco de dados", e);
        }
        return listaFornecedores;
    }

    public void atualizarFornecedorCompleto(FornecedorMolde fornecedor, int idFornecedor){

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

            stmt.setInt(index++, fornecedor.getFkEndereco());
            stmt.setString(index++, fornecedor.getNomeJuridico());
            stmt.setString(index++, fornecedor.getCnpj());
            stmt.setString(index++, fornecedor.getEmail());
            stmt.setInt(index++, fornecedor.getNotaVpq());

            stmt.setInt(index++, idFornecedor);

            stmt.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar fornecedor", e);
        }

    }

    /**
     * Atualiza uma única coluna. O nome da coluna é concatenado ao SQL (o "?" não vale para nome de coluna),
     * então o servlet só deve chamá-lo com colunas já conferidas. O valor chega como texto e é convertido conforme a coluna.
     */
    public void atualizarColunaFornecedor(String nomeColuna, String novoValor, int idFornecedor) {
        String sql = ("UPDATE fornecedor SET " + nomeColuna + " = ? WHERE id_fornecedor = ?");

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            if (nomeColuna.equalsIgnoreCase("nome_juridico")) {
                stmt.setString(1, novoValor);
            }
            else if (nomeColuna.equalsIgnoreCase("fk_endereco")) {
                stmt.setInt(1, Integer.parseInt(novoValor));
            }
            else if (nomeColuna.equalsIgnoreCase("cnpj")) {
                stmt.setString(1, novoValor);
            }
            else if (nomeColuna.equalsIgnoreCase("email")) {
                stmt.setString(1, novoValor);
            }
            else if (nomeColuna.equalsIgnoreCase("nota_vpq")) {
                stmt.setInt(1, Integer.parseInt(novoValor));
            }

            //! Passa o segundo parâmetro, o ID
            stmt.setInt(2, idFornecedor);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar fornecedor", e);
        }
    }

    public void deletarFornecedor(int idFornecedor){
        String sql = "DELETE FROM fornecedor WHERE id_fornecedor = ?";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, idFornecedor);

            int linhasExecutadas = stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar fornecedor", e);
        }
    }

    public List<FornecedorMolde> listarFornecedor(Integer limite, Integer offset){
        return consultarFornecedor(SQL_BASE.concat("order by f.id_fornecedor"), limite, offset);
    }

    public List<FornecedorMolde> listarFornecedorNotaVPQ(boolean ordem, Integer limite, Integer offset) {
        if (ordem) {
            return consultarFornecedor(SQL_BASE.concat(" ORDER BY f.nota_vpq ASC"), limite, offset);
        } else {
            return consultarFornecedor(SQL_BASE.concat(" ORDER BY f.nota_vpq DESC"), limite, offset);
        }
    }

    /**
     * Versão sem paginação das listagens, para o CSV. Cada filtro corresponde a uma ordenação da tela;
     * um filtro desconhecido usa a ordem padrão e um filtro nulo devolve {@code null}.
     */
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


    /**
     * Executa uma consulta de agregação (count, média) que devolve um único número; sem resultado devolve 0.
     */
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

    // Média arredondada em duas casas, direto no banco.
    public double mediaNotaVPQFornecedores(){
        return funcaoDeGrupoFornecedor("select round(AVG(f.nota_vpq)::numeric, 2) from fornecedor f");
    }

    public int contarProdutosVinculados(){
        return (int) funcaoDeGrupoFornecedor("select count(distinct p.fk_fornecedor) from produto p where p.fk_fornecedor is not null");
    }


}

