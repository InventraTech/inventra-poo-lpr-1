package org.inventra.conexao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.SQLException;

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
            throw new RuntimeException("Falha crítica ao iniciar o pool de conexões", e);
        }
    }

    // Método que retorna uma Connection obtida do pool
    public static Connection conectar() throws SQLException {
        return dataSource.getConnection();
    }
}