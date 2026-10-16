package org.inventra.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class SenhaUtil {
    //Vai definir qual o limite de tempo (custo 12 é cerca de 200ms a 300ms)
    private static final int CUSTO = 12;

    public static String gerarHash(String senha) {
        return BCrypt.withDefaults().hashToString(CUSTO, senha.toCharArray());
    }

    public static boolean confere(String senha, String hash) {
        if (senha == null || hash == null) return false;
        return BCrypt.verifyer().verify(senha.toCharArray(), hash).verified;
    }
}
