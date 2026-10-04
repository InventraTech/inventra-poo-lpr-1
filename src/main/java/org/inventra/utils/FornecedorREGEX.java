package org.inventra.utils;

import jakarta.servlet.http.HttpServletResponse;

import java.util.regex.Pattern;

public class FornecedorREGEX {

    //REGEX CNPJ: Aceita o formato padrão: (00.000.000/0000-00) ou 00000000000000
    private static final Pattern CNPJ_REGEX = Pattern.compile("^(\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}|\\d{14})$");
    //REGEX EMAIL: Aceita qualquer coisa com esse padrão: (-@-.-)
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    //REGEX TELEFONE: Aceita o formato padrão: (11) 99999-9999, (11) 9999-9999 ou 11999999999
    private static final Pattern TELEFONE_REGEX = Pattern.compile("^(\\(\\d{2}\\)\\s?)?\\d{4,5}-\\d{4}$|^\\d{10,11}$");

    public static boolean validarCNPJ(String cnpj){
        return cnpj != null && CNPJ_REGEX.matcher(cnpj).matches();
    }

    public static boolean validarEmail(String email){
        return email != null && EMAIL_REGEX.matcher(email).matches();
    }

    public static boolean validarTelefone(String telefone){
        return telefone != null && TELEFONE_REGEX.matcher(telefone).matches();
    }

    public static boolean validandoTodos(String cnpj, String email, String telefone){
        boolean emailValido = FornecedorREGEX.validarEmail(email);
        boolean telefoneValido = FornecedorREGEX.validarTelefone(telefone);
        boolean cnpjValido = FornecedorREGEX.validarCNPJ(cnpj);

        if(!emailValido || !telefoneValido || !cnpjValido){
            return false;
        }

        return true;
    }

}
