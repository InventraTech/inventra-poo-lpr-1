package org.inventra.dao;

import org.inventra.conexao.ConexaoBanco;
import org.inventra.model.FuncionarioMolde;
import org.inventra.utils.SenhaUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class FuncionarioDAO {

    // 1.CREATE
    public void inserirFuncionario(FuncionarioMolde funcionario){

        //String sql: É o código SQL que será enviado para ser executado no banco.
        String sql = "INSERT INTO funcionario (fk_setor, nome, senha, email, telefone, cpf, dt_admissao, status, is_admin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection conexao = ConexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, funcionario.getFk_setor());
            stmt.setString(2, funcionario.getNome());
            stmt.setString(3, SenhaUtil.gerarHash(funcionario.getSenha()));
            stmt.setString(4, funcionario.getEmail());
            stmt.setString(5, funcionario.getTelefone());
            stmt.setString(6, funcionario.getCpf() );
            stmt.setObject(7, funcionario.getDt_admissao());
            stmt.setString(8, funcionario.getStatus());
            stmt.setBoolean(9, funcionario.isAdmin());

            stmt.executeUpdate();
            System.out.println("Cadastro concluída.");

        } catch (SQLException erro) {
            System.out.println("Erro ao salvar funcionário.");
            erro.printStackTrace();
        }
    }

    // 2.READ
    public static final String sqlBase = """
            SELECT f.id_funcionario, f.nome AS nome_funcionario, f. email, f.telefone, f.cpf,
            f.dt_admissao, f.status, s.nome AS setor, f.is_admin
            FROM funcionario f 
            JOIN setor s ON f.fk_setor = s.id_setor
            """;

    public List<FuncionarioMolde> consultarFuncionario(String sql){
        List<FuncionarioMolde> array = new ArrayList<>();

        try(Connection conexao = ConexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){

            while (rs.next()){

                int id = rs.getInt("id_funcionario");
                String nome = rs.getString("nome_funcionario");
                String email = rs.getString("email");
                String telefone = rs.getString("telefone");
                String cpf = rs.getString("cpf");
                LocalDate dt_admissao = rs.getObject("dt_admissao", LocalDate.class);
                String status = rs.getString("status");
                String setor = rs.getString("setor");
                boolean isAdmin = rs.getBoolean("is_admin");

                FuncionarioMolde funcionario = new FuncionarioMolde(id, nome, email, telefone, cpf, dt_admissao, status, setor, isAdmin);
                array.add(funcionario);
            }

        } catch(SQLException erro){
            System.out.println("Erro ao consultar funcionário.");
            erro.printStackTrace();
        }
        return array;
    }

    // 3. UPDATES

    //Metodo que recebe o molde completo com todos os atributos do funcionario, assim alterando tudo.
    public void atualizarFuncionarioCompleto(FuncionarioMolde funcionario, int id_funcionario){

        boolean alterarSenha = (funcionario.getSenha() != null && !funcionario.getSenha().trim().isEmpty());

        String sql = "";
        if (alterarSenha){
        sql = """
                UPDATE funcionario
                SET fk_setor = ?,
                	nome = ?,
                	senha = ?,
                	email = ?,
                	telefone = ?,
                	cpf = ?,
                	dt_admissao = ?,
                	status = ?,
                    is_admin = ?
                WHERE id_funcionario = ?
                """;
        } else {
            sql = """
                UPDATE funcionario
                SET fk_setor = ?,
                	nome = ?,
                	email = ?,
                	telefone = ?,
                	cpf = ?,
                	dt_admissao = ?,
                	status = ?,
                    is_admin = ?
                WHERE id_funcionario = ?
                """;
        }

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)){

            //Precisaremos usar index aqui pois o indice vai variar de acordo com a String sql
            int index = 1;

            stmt.setInt(index++, funcionario.getFk_setor());
            stmt.setString(index++, funcionario.getNome());
            if (alterarSenha) {
                stmt.setString(index++, SenhaUtil.gerarHash(funcionario.getSenha()));
            }
            stmt.setString(index++, funcionario.getEmail());
            stmt.setString(index++, funcionario.getTelefone());
            stmt.setString(index++, funcionario.getCpf() );
            stmt.setObject(index++, funcionario.getDt_admissao());
            stmt.setString(index++, funcionario.getStatus());
            stmt.setBoolean(index++, funcionario.isAdmin());

            stmt.setInt(index++, id_funcionario);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Metodo que recebe uma coluna especifica e um novo valor, editando apenas uma, ideal para alterações simples.
    public void atualizarColunaFuncionario(String NomeColuna, String novoValor, int id_funcionario){

        String valorParaSalvar = "";

        if ("senha".equalsIgnoreCase(NomeColuna)) {
            if (novoValor != null && !novoValor.trim().isEmpty()) {
                valorParaSalvar = SenhaUtil.gerarHash(novoValor);
            } else {
                throw new IllegalArgumentException("A senha não pode ser vazia");
            }
        }

        String sql = ("UPDATE funcionario SET "+NomeColuna+" = ? WHERE id_funcionario = ?");

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)){

            if(NomeColuna.equalsIgnoreCase("nome")){
                stmt.setString(1, novoValor);
            }
            else if (NomeColuna.equalsIgnoreCase("senha")){
                stmt.setString(1, valorParaSalvar);
            }
            else if (NomeColuna.equalsIgnoreCase("fk_setor")){
                stmt.setInt(1, Integer.parseInt(novoValor));
                //Transforma a STRING novoValor em um INT para o banco.
            }
            else if (NomeColuna.equalsIgnoreCase("email")){
                stmt.setString(1, novoValor);
            }
            else if (NomeColuna.equalsIgnoreCase("telefone")){
            stmt.setString(1, novoValor);
            }
            else if (NomeColuna.equalsIgnoreCase("cpf")){
                stmt.setString(1, novoValor);
            }
            else if (NomeColuna.equalsIgnoreCase("dt_admissao")){
                LocalDate dt_admissao = LocalDate.parse(novoValor); // Converte o texto para LocalDate
                stmt.setObject(1, dt_admissao);
            }
            else if (NomeColuna.equalsIgnoreCase("status")){
                stmt.setString(1, novoValor);
            }
            else if (NomeColuna.equalsIgnoreCase("is_admin")){
                stmt.setBoolean(1, Boolean.parseBoolean(novoValor));
            }
            //! Passa o segundo parâmetro, o ID
            stmt.setInt(2, id_funcionario);

            stmt.executeUpdate();
            System.out.println("Funcionário atualizado!");

        } catch (SQLException erro) {
            System.out.println("Erro ao se conectar no banco.");
            erro.printStackTrace();
        }
    }

    // 4.DELETE
    public void deletarFuncionario(int id_funcionario){

        String sql = "DELETE FROM funcionario WHERE id_funcionario = ?";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, id_funcionario);

            int linhasExecutadas = stmt.executeUpdate();

            //O execute update guarda a quantidade de linhas afetadas.
            if(linhasExecutadas > 0){
                System.out.println("Alteração concluída com sucesso.");
            } else {
                System.out.println("Nenhum funcionário com o id "+id_funcionario+" foi encontrado");
            }

        } catch (SQLException erro) {
            System.out.println("Erro ao conectar com o banco.");
            erro.printStackTrace();
        }
    }

    //VALIDAÇÃO DE LOGIN
    public FuncionarioMolde validarLogin(String emailBase, String senhaDigitada){
        FuncionarioMolde funcionario = null;

        if (emailBase == null || senhaDigitada == null) {
            return null;
        }

        String email = emailBase.trim();

        String sql = """
                select * from funcionario
                where upper(email) = upper(?)
                  and is_admin = true
                  and status = 'Ativo'
                """;

        try(Connection conexao = ConexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    //Recupera a senha vinda do banco (em formato hash)
                    String hashDoBanco = rs.getString("senha");

                    if (SenhaUtil.confere(senhaDigitada, hashDoBanco)) {
                        //Cria o objeto aqui (antes ele era null e dava NullPointerException).
                        //Usa o construtor do JOIN, que não recebe senha, assim ela não vai para a sessão.
                        funcionario = new FuncionarioMolde(
                                rs.getInt("id_funcionario"),
                                rs.getString("nome"),
                                rs.getString("email"),
                                rs.getString("telefone"),
                                rs.getString("cpf"),
                                rs.getObject("dt_admissao", LocalDate.class), //aceita data nula
                                rs.getString("status"),
                                null, //nome do setor: o login só tem a FK
                                rs.getBoolean("is_admin")
                        );
                        funcionario.setFk_setor(rs.getInt("fk_setor"));

                        return funcionario;
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //Só chega aqui se o email ou senha estiverem errados
        return null;
    }

    //MÉTODOS DE FILTRAGEM

    public List<FuncionarioMolde> listarFuncionario(){
        return consultarFuncionario(sqlBase.concat("order by f.id_funcionario"));
    }

    public List<FuncionarioMolde> listarFuncionarioPorSetor(){
        return consultarFuncionario(sqlBase.concat("order by s.nome, f.nome"));
    }

    public List<FuncionarioMolde> listarFuncionarioAlfabetico(boolean ordem){
        if (ordem) {
            return consultarFuncionario(sqlBase.concat(" ORDER BY f.nome ASC"));
        } else {
            return consultarFuncionario(sqlBase.concat(" ORDER BY f.nome DESC"));
        }
    }

    public List<FuncionarioMolde> listarFuncionarioPorData(boolean ordem){
        if (ordem) {
            return consultarFuncionario(sqlBase.concat(" order by f.dt_admissao desc"));
        } else {
            return consultarFuncionario(sqlBase.concat(" order by f.dt_admissao asc"));
        }
    }

}
