package org.inventra.conexao;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class
ConexaoBanco {
    private static Dotenv dotEnv = Dotenv.configure().directory("/").filename(".env").load();

    private static final String URL = dotEnv.get("DB_URL");
    private static final String USUARIO = dotEnv.get("DB_USUARIO");
    private static final String SENHA = dotEnv.get("DB_SENHA");

    // Metodo que retorna Connection que pode ser chamado por outras classes
    public static Connection conectar() {
        try {
            Class.forName("org.postgresql.Driver");

            // Abre a conexão e devolve ela para quem chamou o método
            return DriverManager.getConnection(URL, USUARIO, SENHA);

        } catch (ClassNotFoundException erro) {
            System.out.println("Driver do banco de dados não localizado");
            erro.printStackTrace();
            return null;

        } catch (SQLException erro) {
            System.out.println("Erro: Verifique se o usuário e senha estão corretos");
            erro.printStackTrace();
            return null;
        }
    }
}