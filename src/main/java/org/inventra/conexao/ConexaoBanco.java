package org.inventra.conexao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Ponto único de acesso ao banco: mantém um pool de conexões (HikariCP) criado uma só vez,
 * quando a classe é carregada. Usuário, senha e URL vêm do arquivo .env (raiz do projeto) ou, em produção,
 * das variáveis de ambiente DB_URL, DB_USUARIO e DB_SENHA. Variáveis de ambiente têm prioridade sobre o .env.
 */
public class ConexaoBanco {
    // Procura o .env subindo a partir da pasta de execução e da pasta do código compilado.
    // Em produção (Render) não existe arquivo: as variáveis vêm do painel de ambiente.
    private static Dotenv dotEnv = carregarDotEnv();

    private static Dotenv carregarDotEnv() {
        List<Path> inicios = new ArrayList<>();
        inicios.add(Paths.get(System.getProperty("user.dir")));
        try {
            inicios.add(Paths.get(ConexaoBanco.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
        } catch (Exception e) {
            // Sem a localização do código, a busca usa só a pasta de execução
        }
        for (Path inicio : inicios) {
            for (Path pasta = inicio; pasta != null; pasta = pasta.getParent()) {
                if (Files.isRegularFile(pasta.resolve(".env"))) {
                    System.out.println("Arquivo .env carregado de: " + pasta.resolve(".env"));
                    return Dotenv.configure().directory(pasta.toString()).load();
                }
            }
        }
        System.out.println("Nenhum arquivo .env encontrado; usando as variáveis de ambiente.");
        return Dotenv.configure().ignoreIfMissing().load();
    }
    private static HikariDataSource dataSource;

    static {
        try {
            HikariConfig config = new HikariConfig();

            // Configurações básicas de conexão obtidas do .env
            String url = dotEnv.get("DB_URL");
            if (url == null || url.isBlank()) {
                throw new IllegalStateException("Variável DB_URL não encontrada. Confira se o .env está na raiz do projeto ou defina DB_URL nas variáveis de ambiente.");
            }
            config.setJdbcUrl(url);
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