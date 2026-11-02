package org.inventra.conexao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Ponto único de acesso ao banco: mantém um pool de conexões (HikariCP) criado uma só vez,
 * quando a classe é carregada. Usuário, senha e URL vêm do arquivo .env, para não ficarem no código.
 */
public class ConexaoBanco {
    private static Dotenv dotEnv = Dotenv.configure().directory("/").filename(".env").load();
    private static HikariDataSource dataSource;

    static {
        try {
            HikariConfig config = new HikariConfig();

            // Configurações básicas de conexão obtidas do .env
            config.setJdbcUrl(dotEnv.get("DB_URL"));
            config.setUsername(dotEnv.get("DB_USUARIO"));
            config.setPassword(dotEnv.get("DB_SENHA"));
            config.setDriverClassName("org.postgresql.Driver");

            // Configurações recomendadas do Pool (Opcionais, mas ajustam a performance)
            config.setMaximumPoolSize(4); // Quantidade máxima de conexões simultâneas no pool
            config.setMinimumIdle(2);      // Quantidade mínima de conexões mantidas vivas
            config.setIdleTimeout(30000);  // Tempo máximo (ms) que uma conexão pode ficar ociosa (30 segundos)
            config.setConnectionTimeout(30000); // Tempo máximo (ms) (equivalente a 30 segundos)

            dataSource = new HikariDataSource(config);

        } catch (Exception e) {
            System.out.println("Erro ao inicializar o Pool de Conexões do HikariCP");
            e.printStackTrace();
            // Sem o pool nenhum DAO funciona; por isso a falha interrompe o carregamento da classe em vez de seguir com dataSource nulo.
            throw new RuntimeException("Falha crítica ao iniciar o pool de conexões", e);
        }
    }

    /**
     * Entrega uma conexão emprestada do pool. Quem chama deve usar try-with-resources:
     * fechar a conexão a devolve ao pool, não a encerra de verdade.
     *
     * @throws SQLException se o pool não conseguir fornecer uma conexão dentro do tempo limite
     */
    public static Connection conectar() throws SQLException {
        return dataSource.getConnection();
    }
}