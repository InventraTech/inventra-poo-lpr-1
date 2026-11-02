package org.inventra.utils;

import java.util.regex.Pattern;

/**
 * Validação de formato para os dados do funcionário. Confere apenas o padrão do texto: o CPF, por exemplo, não tem os
 * dígitos verificadores calculados, e o e-mail não é verificado quanto à existência.
 */
public class FuncionarioREGEX {

    //REGEX CPF. Formato: 000.000.000-00 ou 00000000000
    private static final Pattern CPF_REGEX = Pattern.compile("^(\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}|\\d{11})$");
    //REGEX EMAIL. Formato usuario@dominio.extensao (ex.: .com, .br), igual ao usado para fornecedores.
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public static boolean validarCpf(String cpf) {
        return cpf != null && CPF_REGEX.matcher(cpf).matches();
    }

    public static boolean validarEmail(String email) {
        return email != null && EMAIL_REGEX.matcher(email).matches();
    }

    /**
     * Atalho que confere CPF e e-mail de uma vez; só é verdadeiro se os dois tiverem formato válido.
     */
    public static boolean validandoTodos (String cpf, String email){
        boolean emailValido = FuncionarioREGEX.validarEmail(email);
        boolean cpfValido = FuncionarioREGEX.validarCpf(cpf);

        if(!emailValido || !cpfValido){
            return false;
        }

        return true;
    }
}
