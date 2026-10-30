package org.inventra.dao;

import org.inventra.conexao.ConexaoBanco;
import org.inventra.model.LoteMolde;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class LoteDAO {

    // 1. CREATE
    public void inserirLote(LoteMolde lote){
        String sql = "INSERT INTO lote (fk_produto, numero_lote, qtd_inicial, qtd_atual, dt_entrada, dt_validade, valor_compra, nota_fiscal) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection conexao = ConexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, lote.getFk_produto());
            stmt.setString(2, lote.getNumero_lote());
            stmt.setInt(3, lote.getQtd_inicial());
            stmt.setInt(4, lote.getQtd_atual());
            stmt.setObject(5, lote.getDt_entrada());
            stmt.setObject(6, lote.getDt_validade());
            stmt.setBigDecimal(7, lote.getValor_compra()); // Utilização correta do BigDecimal
            stmt.setString(8, lote.getNota_fiscal());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir um novo lote no banco de dados", e);
        }
    }

    // 2. READ

    public static final String sqlBase = """
            SELECT l.id_lote, p.nome AS nome_produto, l.numero_lote, l.qtd_inicial, l.qtd_atual, l.dt_entrada, l.dt_validade, l.valor_compra, l.nota_fiscal
            FROM lote l
            JOIN produto p ON l.fk_produto = p.id_produto
            """;

    public List<LoteMolde> consultarLote(String sql, Integer limite, Integer offset){
        List<LoteMolde> array = new ArrayList<>();

        //Se precisar de paginação vai para a tela do usuário, se não o exportar pega todos(para o arquivo CSV)
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

            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_lote");
                    String produto = rs.getString("nome_produto");
                    String numero_lote = rs.getString("numero_lote");
                    int qtd_inicial = rs.getInt("qtd_inicial");
                    int qtd_atual = rs.getInt("qtd_atual");
                    LocalDate dt_entrada = rs.getObject("dt_entrada", LocalDate.class);
                    LocalDate dt_validade = rs.getObject("dt_validade", LocalDate.class);
                    BigDecimal valor_custo = rs.getBigDecimal("valor_compra");
                    String nota_fiscal = rs.getString("nota_fiscal");

                    LoteMolde lote = new LoteMolde(id, produto, numero_lote, qtd_inicial, qtd_atual, dt_entrada, dt_validade, valor_custo, nota_fiscal);
                    array.add(lote);
                }
            }

        } catch(SQLException e){
            throw new RuntimeException("Erro ao consultar a tabela de lotes no banco de dados", e);
        }
        return array;
    }

    // 3. UPDATE

    public void atualizarLoteCompleto(LoteMolde lote, int id_lote){

        String sql = """
                UPDATE lote
                SET fk_produto = ?,
                	numero_lote = ?,
                	qtd_inicial = ?,
                	qtd_atual = ?,
                	dt_entrada = ?,
                	dt_validade = ?,
                	valor_compra = ?,
                	nota_fiscal = ?
                WHERE id_lote
                """;

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, lote.getFk_produto());
            stmt.setString(2, lote.getNumero_lote());
            stmt.setInt(3, lote.getQtd_inicial());
            stmt.setInt(4, lote.getQtd_atual());
            stmt.setObject(5, lote.getDt_entrada());
            stmt.setObject(6, lote.getDt_validade());
            stmt.setBigDecimal(7, lote.getValor_compra()); // Utilização correta do BigDecimal
            stmt.setString(8, lote.getNota_fiscal());

            stmt.setInt(9, id_lote);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar lote no banco de dados", e);
        }
    }

    //Metodo que recebe uma coluna especifica e um novo valor, editando apenas uma, ideal para alterações simples.
    public void atualizarColunaLote(String NomeColuna, String novoValor, int id_lote){
        String sql = ("UPDATE lote SET "+NomeColuna+" = ? WHERE id_lote = ?");

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)){

            if(NomeColuna.equalsIgnoreCase("fk_produto")){
                stmt.setInt(1, Integer.parseInt(novoValor));
            }
            else if (NomeColuna.equalsIgnoreCase("numero_lote")){
                stmt.setString(1, novoValor);
            }
            else if (NomeColuna.equalsIgnoreCase("qtd_inicial")){
                stmt.setInt(1, Integer.parseInt(novoValor));
            }
            else if (NomeColuna.equalsIgnoreCase("qtd_atual")){
                stmt.setInt(1, Integer.parseInt(novoValor));
            }
            else if (NomeColuna.equalsIgnoreCase("dt_entrada")){
                LocalDate dt = LocalDate.parse(novoValor);
                stmt.setObject(1, dt);
            }
            else if (NomeColuna.equalsIgnoreCase("dt_validade")){
                LocalDate dt = LocalDate.parse(novoValor);
                stmt.setObject(1, dt);
            }
            else if (NomeColuna.equalsIgnoreCase("valor_compra")){
                BigDecimal valorBigDecimal = new BigDecimal(novoValor);
                stmt.setBigDecimal(1, valorBigDecimal);
            }
            else if (NomeColuna.equalsIgnoreCase("nota_fiscal")){
                stmt.setString(1, novoValor);
            }

            stmt.setInt(2, id_lote);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar lote no banco de dados", e);
        }
    }

    // 4. DELETE
    public void deletarLote(int id_lote){
        String sql = "DELETE FROM lote WHERE id_lote = ?";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, id_lote);
            int linhasExecutadas = stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar lote no banco de dados", e);
        }
    }

    public List<LoteMolde> listarLote(Integer limite, Integer offset){
        return consultarLote(sqlBase.concat(" ORDER BY id_lote asc"), limite, offset);
    }

    public List<LoteMolde> listarLoteQuantidadeProdutosAtual(Integer limite, Integer offset){
        return consultarLote(sqlBase.concat(" ORDER BY qtd_atual desc"), limite, offset);
    }

    public List<LoteMolde> listarLoteDataEntrada(boolean ordem, Integer limite, Integer offset){
        if(ordem){
            return consultarLote(sqlBase.concat(" ORDER BY dt_entrada asc"), limite, offset);
        } else {
            return consultarLote(sqlBase.concat(" ORDER BY dt_entrada desc"), limite, offset);
        }
    }

    public List<LoteMolde> listarLoteDataValidade(boolean ordem, Integer limite, Integer offset){
        if(ordem){
            return consultarLote(sqlBase.concat(" ORDER BY dt_validade asc"), limite, offset);
        } else {
            return consultarLote(sqlBase.concat(" ORDER BY dt_validade desc"), limite, offset);
        }
    }

    public List<LoteMolde> listarLoteParaExportar(String filtragem){
        if (filtragem != null) {
            switch (filtragem) {
                case "entrada_crescente" -> {
                    return listarLoteDataEntrada(true, null, null);
                }
                case "entrada_decrescente" -> {
                    return listarLoteDataEntrada(false, null, null);
                }
                case "validade_crescente" -> {
                    return listarLoteDataValidade(true, null, null);
                }
                case "validade_decrescente" -> {
                    return listarLoteDataValidade(false, null, null);
                }
                case "produtos_atual" -> {
                    return listarLoteQuantidadeProdutosAtual(null, null);
                }
                default -> {
                    return listarLote(null, null);
                }
            }
        }
        return null;
    }

    public static final String sqlBaseContador = """
            SELECT count(*)
            FROM lote l
        """;

    public int contarLotes(String sql){

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

    public int contarLotesPadrao(){
        return contarLotes(sqlBaseContador);
    }

    public int contarLotesVencidos(){
        return contarLotes(sqlBaseContador.concat("where dt_validade < current_date"));
    }

    public int contarLotesVencendo30dias(){
        return contarLotes(sqlBaseContador.concat("where dt_validade >= current_date and dt_validade <= current_date + interval '30 days';"));
    }
}