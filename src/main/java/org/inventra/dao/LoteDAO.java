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
/**
 * Acesso à tabela lote, que guarda quantidade, validade e custo de cada entrada de produto.
 * Também fornece as contagens usadas no sino de notificações (lotes vencidos e vencendo em 30 dias).
 */
public class LoteDAO {

    // 1. CREATE
    public void inserirLote(LoteMolde lote){
        String sql = "INSERT INTO lote (fk_produto, numero_lote, qtd_inicial, qtd_atual, dt_entrada, dt_validade, valor_compra, nota_fiscal) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection conexao = ConexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, lote.getFkProduto());
            stmt.setString(2, lote.getNumeroLote());
            stmt.setInt(3, lote.getQtdInicial());
            stmt.setInt(4, lote.getQtdAtual());
            stmt.setObject(5, lote.getDtEntrada());
            stmt.setObject(6, lote.getDtValidade());
            stmt.setBigDecimal(7, lote.getValorCompra()); // Utilização correta do BigDecimal
            stmt.setString(8, lote.getNotaFiscal());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir um novo lote no banco de dados", e);
        }
    }

    // 2. READ

    public static final String SQL_BASE = """
            SELECT l.id_lote, p.nome AS nome_produto, l.numero_lote, l.qtd_inicial, l.qtd_atual, l.dt_entrada, l.dt_validade, l.valor_compra, l.nota_fiscal
            FROM lote l
            JOIN produto p ON l.fk_produto = p.id_produto
            """;

    /**
     * Executa a consulta recebida. Com {@code limite} e {@code offset} informados, aplica a paginação da tela;
     * com algum deles nulo, devolve todos os registros (usado na exportação para CSV).
     */
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
                    String numeroLote = rs.getString("numero_lote");
                    int qtdInicial = rs.getInt("qtd_inicial");
                    int qtdAtual = rs.getInt("qtd_atual");
                    LocalDate dtEntrada = rs.getObject("dt_entrada", LocalDate.class);
                    LocalDate dtValidade = rs.getObject("dt_validade", LocalDate.class);
                    BigDecimal valorCompra = rs.getBigDecimal("valor_compra");
                    String notaFiscal = rs.getString("nota_fiscal");

                    LoteMolde lote = new LoteMolde(id, produto, numeroLote, qtdInicial, qtdAtual, dtEntrada, dtValidade, valorCompra, notaFiscal);
                    array.add(lote);
                }
            }

        } catch(SQLException e){
            throw new RuntimeException("Erro ao consultar a tabela de lotes no banco de dados", e);
        }
        return array;
    }

    // 3. UPDATE

    public void atualizarLoteCompleto(LoteMolde lote, int idLote){

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
                WHERE id_lote = ?
                """;

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, lote.getFkProduto());
            stmt.setString(2, lote.getNumeroLote());
            stmt.setInt(3, lote.getQtdInicial());
            stmt.setInt(4, lote.getQtdAtual());
            stmt.setObject(5, lote.getDtEntrada());
            stmt.setObject(6, lote.getDtValidade());
            stmt.setBigDecimal(7, lote.getValorCompra());
            stmt.setString(8, lote.getNotaFiscal());

            stmt.setInt(9, idLote);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar lote no banco de dados", e);
        }
    }

    /**
     * Atualiza uma única coluna. O nome da coluna é concatenado ao SQL (o "?" não vale para nome de coluna),
     * então o servlet só deve chamá-lo com colunas já conferidas. O valor chega como texto e é convertido conforme a coluna.
     */
    public void atualizarColunaLote(String nomeColuna, String novoValor, int idLote){
        String sql = ("UPDATE lote SET "+nomeColuna+" = ? WHERE id_lote = ?");

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)){

            if(nomeColuna.equalsIgnoreCase("fk_produto")){
                stmt.setInt(1, Integer.parseInt(novoValor));
            }
            else if (nomeColuna.equalsIgnoreCase("numero_lote")){
                stmt.setString(1, novoValor);
            }
            else if (nomeColuna.equalsIgnoreCase("qtd_inicial")){
                stmt.setInt(1, Integer.parseInt(novoValor));
            }
            else if (nomeColuna.equalsIgnoreCase("qtd_atual")){
                stmt.setInt(1, Integer.parseInt(novoValor));
            }
            else if (nomeColuna.equalsIgnoreCase("dt_entrada")){
                LocalDate dt = LocalDate.parse(novoValor);
                stmt.setObject(1, dt);
            }
            else if (nomeColuna.equalsIgnoreCase("dt_validade")){
                LocalDate dt = LocalDate.parse(novoValor);
                stmt.setObject(1, dt);
            }
            else if (nomeColuna.equalsIgnoreCase("valor_compra")){
                BigDecimal valorBigDecimal = new BigDecimal(novoValor);
                stmt.setBigDecimal(1, valorBigDecimal);
            }
            else if (nomeColuna.equalsIgnoreCase("nota_fiscal")){
                stmt.setString(1, novoValor);
            }

            stmt.setInt(2, idLote);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar lote no banco de dados", e);
        }
    }

    // 4. DELETE
    public void deletarLote(int idLote){
        String sql = "DELETE FROM lote WHERE id_lote = ?";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, idLote);
            int linhasExecutadas = stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar lote no banco de dados", e);
        }
    }

    public List<LoteMolde> listarLote(Integer limite, Integer offset){
        return consultarLote(SQL_BASE.concat(" ORDER BY id_lote asc"), limite, offset);
    }

    public List<LoteMolde> listarLoteQuantidadeProdutosAtual(Integer limite, Integer offset){
        return consultarLote(SQL_BASE.concat(" ORDER BY qtd_atual desc"), limite, offset);
    }

    public List<LoteMolde> listarLoteDataEntrada(boolean ordem, Integer limite, Integer offset){
        if(ordem){
            return consultarLote(SQL_BASE.concat(" ORDER BY dt_entrada asc"), limite, offset);
        } else {
            return consultarLote(SQL_BASE.concat(" ORDER BY dt_entrada desc"), limite, offset);
        }
    }

    public List<LoteMolde> listarLoteDataValidade(boolean ordem, Integer limite, Integer offset){
        if(ordem){
            return consultarLote(SQL_BASE.concat(" ORDER BY dt_validade asc"), limite, offset);
        } else {
            return consultarLote(SQL_BASE.concat(" ORDER BY dt_validade desc"), limite, offset);
        }
    }

    /**
     * Versão sem paginação das listagens, para o CSV. Cada filtro corresponde a uma ordenação da tela;
     * um filtro desconhecido usa a ordem padrão e um filtro nulo devolve {@code null}.
     */
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

    public static final String SQL_BASE_CONTADOR = """
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
        return contarLotes(SQL_BASE_CONTADOR);
    }

    // Vencido é o lote cuja validade já passou (a data de hoje ainda conta como válido).
    public int contarLotesVencidos(){
        return contarLotes(SQL_BASE_CONTADOR.concat("where dt_validade < current_date"));
    }

    // Janela de hoje até 30 dias à frente; é o número exibido no sino de notificações.
    public int contarLotesVencendo30dias(){
        return contarLotes(SQL_BASE_CONTADOR.concat("where dt_validade >= current_date and dt_validade <= current_date + interval '30 days';"));
    }

    // BUSCAR POR ID (leitura apenas; usado pelo LoteServlet para validar edições por coluna)
    public LoteMolde buscarLotePorId(int idLote){
        String sql = "SELECT id_lote, fk_produto, numero_lote, qtd_inicial, qtd_atual, dt_entrada, dt_validade, valor_compra, nota_fiscal FROM lote WHERE id_lote = ?";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, idLote);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new LoteMolde(
                            rs.getInt("id_lote"),
                            rs.getInt("fk_produto"),
                            rs.getString("numero_lote"),
                            rs.getInt("qtd_inicial"),
                            rs.getInt("qtd_atual"),
                            rs.getObject("dt_entrada", java.time.LocalDate.class),
                            rs.getObject("dt_validade", java.time.LocalDate.class),
                            rs.getBigDecimal("valor_compra"),
                            rs.getString("nota_fiscal"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar lote no banco de dados", e);
        }
        return null;
    }
}
