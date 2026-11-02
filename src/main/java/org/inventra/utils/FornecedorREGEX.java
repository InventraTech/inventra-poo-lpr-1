package org.inventra.utils;

import jakarta.servlet.http.HttpServletResponse;

import java.util.regex.Pattern;

/**
 * Validação de formato para os dados do fornecedor. Confere apenas o padrão do texto: o CNPJ não tem os dígitos
 * verificadores calculados, e o e-mail não é verificado quanto à existência.
 */
public class FornecedorREGEX {

    //REGEX CNPJ: Aceita o formato padrão: (00.000.000/0000-00) ou 00000000000000
    private static final Pattern CNPJ_REGEX = Pattern.compile("^(\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}|\\d{14})$");
    //REGEX EMAIL: Aceita qualquer coisa com esse padrão: (-@-.-)
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public static boolean validarCNPJ(String cnpj){
        return cnpj != null && CNPJ_REGEX.matcher(cnpj).matches();
    }

    public static boolean validarEmail(String email){
        return email != null && EMAIL_REGEX.matcher(email).matches();
    }

    /**
     * Atalho que confere CNPJ e e-mail de uma vez; só é verdadeiro se os dois tiverem formato válido.
     */
    public static boolean validandoTodos(String cnpj, String email){
        boolean emailValido = FornecedorREGEX.validarEmail(email);
        boolean cnpjValido = FornecedorREGEX.validarCNPJ(cnpj);

        if(!emailValido || !cnpjValido){
            return false;
        }

        return true;
    }

}
