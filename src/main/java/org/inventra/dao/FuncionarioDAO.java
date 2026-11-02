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


/**
 * Acesso à tabela funcionario, incluindo a validação de login. A senha nunca é gravada em texto puro:
 * ela passa por SenhaUtil.gerarHash antes de ir para o banco.
 */
public class FuncionarioDAO {

    // 1.CREATE
    public void inserirFuncionario(FuncionarioMolde funcionario){

        //String sql: É o código SQL que será enviado para ser executado no banco.
        String sql = "INSERT INTO funcionario (fk_setor, nome, senha, email, cpf, dt_admissao, status, is_admin) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection conexao = ConexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, funcionario.getFkSetor());
            stmt.setString(2, funcionario.getNome());
            stmt.setString(3, SenhaUtil.gerarHash(funcionario.getSenha()));
            stmt.setString(4, funcionario.getEmail());
            stmt.setString(5, funcionario.getCpf() );
            stmt.setObject(6, funcionario.getDtAdmissao());
            stmt.setString(7, funcionario.getStatus());
            stmt.setBoolean(8, funcionario.isAdmin());

            stmt.executeUpdate();
            System.out.println("Cadastro concluída.");

        } catch (SQLException e) {
            //Lança a exceção para ser capturada no Servlet
            throw new RuntimeException("Erro ao inserir um novo funcionário no banco de dados", e);
        }
    }

    // 2.READ
    public static final String SQL_BASE = """
            SELECT f.id_funcionario, f.nome AS nome_funcionario, f. email, f.cpf,
            f.dt_admissao, f.status, s.nome AS setor, f.is_admin
            FROM funcionario f 
            JOIN setor s ON f.fk_setor = s.id_setor
            """;

    /**
     * Executa a consulta recebida. Com {@code limite} e {@code offset} informados, aplica a paginação da tela;
     * com algum deles nulo, devolve todos os registros (usado na exportação para CSV).
     */
    public List<FuncionarioMolde> consultarFuncionario(String sql, Integer limite, Integer offset){
        List<FuncionarioMolde> listaFuncionarios = new ArrayList<>();

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

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_funcionario");
                    String nome = rs.getString("nome_funcionario");
                    String email = rs.getString("email");
                    String cpf = rs.getString("cpf");
                    LocalDate dtAdmissao = rs.getObject("dt_admissao", LocalDate.class);
                    String status = rs.getString("status");
                    String setor = rs.getString("setor");
                    boolean isAdmin = rs.getBoolean("is_admin");

                    FuncionarioMolde funcionario = new FuncionarioMolde(id, nome, email, cpf, dtAdmissao, status, setor, isAdmin);
                    listaFuncionarios.add(funcionario);
                }
            }

        } catch(SQLException e){
            //Lança a exceção para ser capturada no Servlet
            throw new RuntimeException("Erro ao consultar a tabela de funcionarios no banco de dados", e);
        }
        return listaFuncionarios;
    }

    // 3. UPDATES

    //Metodo que recebe o molde completo com todos os atributos do funcionario, assim alterando tudo.
    public void atualizarFuncionarioCompleto(FuncionarioMolde funcionario, int idFuncionario){

        // Na edição completa, senha em branco significa "manter a senha atual": nesse caso o UPDATE nem inclui a coluna.
        boolean alterarSenha = (funcionario.getSenha() != null && !funcionario.getSenha().trim().isEmpty());

        String sql = "";
        if (alterarSenha){
        sql = """
                UPDATE funcionario
                SET fk_setor = ?,
                	nome = ?,
                	senha = ?,
                	email = ?,
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

            stmt.setInt(index++, funcionario.getFkSetor());
            stmt.setString(index++, funcionario.getNome());
            if (alterarSenha) {
                stmt.setString(index++, SenhaUtil.gerarHash(funcionario.getSenha()));
            }
            stmt.setString(index++, funcionario.getEmail());
            stmt.setString(index++, funcionario.getCpf() );
            stmt.setObject(index++, funcionario.getDtAdmissao());
            stmt.setString(index++, funcionario.getStatus());
            stmt.setBoolean(index++, funcionario.isAdmin());

            stmt.setInt(index++, idFuncionario);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar funcionario",e);
        }
    }

    /**
     * Atualiza uma única coluna. O nome da coluna é concatenado ao SQL (o "?" não vale para nome de coluna),
     * então o servlet só deve chamá-lo com colunas já conferidas. O valor chega como texto e é convertido conforme a coluna.
     *
     * Para a coluna {@code senha}, o novo valor é transformado em hash antes de gravar e senha vazia é recusada
     * com {@code IllegalArgumentException}.
     */
    public void atualizarColunaFuncionario(String nomeColuna, String novoValor, int idFuncionario){

        String valorParaSalvar = "";

        if ("senha".equalsIgnoreCase(nomeColuna)) {
            if (novoValor != null && !novoValor.trim().isEmpty()) {
                valorParaSalvar = SenhaUtil.gerarHash(novoValor);
            } else {
                throw new IllegalArgumentException("A senha não pode ser vazia");
            }
        }

        String sql = ("UPDATE funcionario SET "+nomeColuna+" = ? WHERE id_funcionario = ?");

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)){

            if(nomeColuna.equalsIgnoreCase("nome")){
                stmt.setString(1, novoValor);
            }
            else if (nomeColuna.equalsIgnoreCase("senha")){
                stmt.setString(1, valorParaSalvar);
            }
            else if (nomeColuna.equalsIgnoreCase("fk_setor")){
                stmt.setInt(1, Integer.parseInt(novoValor));
                //Transforma a STRING novoValor em um INT para o banco.
            }
            else if (nomeColuna.equalsIgnoreCase("email")){
                stmt.setString(1, novoValor);
            }
            else if (nomeColuna.equalsIgnoreCase("cpf")){
                stmt.setString(1, novoValor);
            }
            else if (nomeColuna.equalsIgnoreCase("dt_admissao")){
                LocalDate dtAdmissao = LocalDate.parse(novoValor); // Converte o texto para LocalDate
                stmt.setObject(1, dtAdmissao);
            }
            else if (nomeColuna.equalsIgnoreCase("status")){
                stmt.setString(1, novoValor);
            }
            else if (nomeColuna.equalsIgnoreCase("is_admin")){
                stmt.setBoolean(1, Boolean.parseBoolean(novoValor));
            }
            //! Passa o segundo parâmetro, o ID
            stmt.setInt(2, idFuncionario);

            stmt.executeUpdate();
            System.out.println("Funcionário atualizado!");

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar funcionario", e);
        }
    }

    // 4.DELETE
    public void deletarFuncionario(int idFuncionario){

        String sql = "DELETE FROM funcionario WHERE id_funcionario = ?";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, idFuncionario);

            int linhasExecutadas = stmt.executeUpdate();

            //O execute update guarda a quantidade de linhas afetadas.
            if(linhasExecutadas > 0){
                System.out.println("Alteração concluída com sucesso.");
            } else {
                System.out.println("Nenhum funcionário com o id "+idFuncionario+" foi encontrado");
            }

        } catch (SQLException erro) {
            System.out.println("Erro ao conectar com o banco.");
            erro.printStackTrace();
        }
    }

    //VALIDAÇÃO DE LOGIN
    /**
     * Confere e-mail e senha no login. Só aceita administradores com status "Ativo"; o e-mail ignora maiúsculas e minúsculas
     * e a senha é comparada com o hash bcrypt do banco.
     *
     * @return o funcionário logado, sem a senha, ou {@code null} se as credenciais não forem válidas
     */
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
                                rs.getString("cpf"),
                                rs.getObject("dt_admissao", LocalDate.class), //aceita data nula
                                rs.getString("status"),
                                null, //nome do setor: o login só tem a FK
                                rs.getBoolean("is_admin")
                        );
                        funcionario.setFkSetor(rs.getInt("fk_setor"));

                        return funcionario;
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar funcionario", e);
        }
        //Só chega aqui se o email ou senha estiverem errados
        return null;
    }

    //MÉTODOS DE FILTRAGEM

    public List<FuncionarioMolde> listarFuncionario(Integer limite, Integer offset){
        return consultarFuncionario(SQL_BASE.concat("order by f.id_funcionario"), limite, offset);
    }

    public List<FuncionarioMolde> listarFuncionarioPorSetor(Integer limite, Integer offset){
        return consultarFuncionario(SQL_BASE.concat("order by s.nome, f.nome"), limite, offset);
    }

    public List<FuncionarioMolde> listarFuncionarioAlfabetico(boolean ordem, Integer limite, Integer offset){
        if (ordem) {
            return consultarFuncionario(SQL_BASE.concat(" ORDER BY f.nome ASC"), limite, offset);
        } else {
            return consultarFuncionario(SQL_BASE.concat(" ORDER BY f.nome DESC"), limite, offset);
        }
    }

    public List<FuncionarioMolde> listarFuncionarioPorData(boolean ordem, Integer limite, Integer offset){
        if (ordem) {
            return consultarFuncionario(SQL_BASE.concat(" order by f.dt_admissao desc"), limite, offset);
        } else {
            return consultarFuncionario(SQL_BASE.concat(" order by f.dt_admissao asc"), limite, offset);
        }
    }

    /**
     * Versão sem paginação das listagens, para o CSV. Cada filtro corresponde a uma ordenação da tela;
     * um filtro desconhecido usa a ordem padrão e um filtro nulo devolve {@code null}.
     */
    public List<FuncionarioMolde> listarFuncionarioParaExportar(String filtragem){
        if (filtragem != null){
            switch (filtragem){
                case "az" -> {
                    return listarFuncionarioAlfabetico(true, null, null);
                } case "za" -> {
                    return listarFuncionarioAlfabetico(false, null, null);
                } case "setor" -> {
                    return listarFuncionarioPorSetor(null, null);
                } case "data_crescente" -> {
                    return listarFuncionarioPorData(true, null, null);
                } case "data_decrescente" -> {
                    return listarFuncionarioPorData(false, null, null);
                }
                default -> {
                    return listarFuncionario(null, null);
                }
            }
        }
        return null;
    }

    public static final String CONTADOR_BASE = """
            select count(*) from funcionario
            """;

    public int contarFuncionarios(String sql){

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

    public int contarFuncionariosPadrao(){
        return contarFuncionarios(CONTADOR_BASE);
    }

    public int contarFuncionariosAdmins(){
        return contarFuncionarios(CONTADOR_BASE.concat(" where is_admin = true"));
    }

    public int contarSetores(){
        return contarFuncionarios("select count(distinct f.fk_setor ) from funcionario f ");
    }

    public int contarAtivos(){
        return contarFuncionarios("select count(*) from funcionario f where status = 'Ativo'");
    }
}
