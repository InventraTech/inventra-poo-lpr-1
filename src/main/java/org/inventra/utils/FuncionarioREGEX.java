package org.inventra.utils;

import java.util.regex.Pattern;

/**
 * Validação de formato para os dados do funcionário. Confere apenas o padrão do texto: o CPF, por exemplo, não tem os
 * dígitos verificadores calculados, e o e-mail não é verificado quanto à existência.
 */
public class FuncionarioREGEX {

    //REGEX CPF. Formato: 000.000.000-00 ou 00000000000
    private static final Pattern CPF_REGEX = Pattern.compile("^(\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}|\\d{11})$");
    //REGEX EMAIL. Formato usuario@dominio, sem exigir extensão (.com, .br...).
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    //REGEX TELEFONE. Formato: (11) 99999-9999 ou 11999999999
    private static final Pattern TELEFONE_REGEX = Pattern.compile("^(\\(\\d{2}\\)\\s?)?\\d{4,5}-\\d{4}$|^\\d{10,11}$");

    public static boolean validarCpf(String cpf) {
        return cpf != null && CPF_REGEX.matcher(cpf).matches();
    }

    public static boolean validarEmail(String email) {
        return email != null && EMAIL_REGEX.matcher(email).matches();
    }

    public static boolean validarTelefone(String telefone) {
        return telefone != null && TELEFONE_REGEX.matcher(telefone).matches();
    }

    /**
     * Atalho que confere CPF, e-mail e telefone de uma vez; só é verdadeiro se os três tiverem formato válido.
     */
    public static boolean validandoTodos (String cpf, String email, String telefone){
        boolean emailValido = FuncionarioREGEX.validarEmail(email);
        boolean telefoneValido = FuncionarioREGEX.validarTelefone(telefone);
        boolean cpfValido = FuncionarioREGEX.validarCpf(cpf);

        if(!emailValido || !telefoneValido || !cpfValido){
            return false;
        }

        return true;
    }
}
