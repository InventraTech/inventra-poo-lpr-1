package org.inventra.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Geração e conferência de senhas com bcrypt. O banco guarda apenas o hash; a senha original nunca é armazenada.
 */
public class SenhaUtil {
    //Fator de custo do bcrypt: cada +1 dobra o tempo de cálculo. Com 12 leva cerca de 200 a 300 ms, o que pesa pouco no login
    //e encarece tentativas de força bruta.
    private static final int CUSTO = 12;

    public static String gerarHash(String senha) {
        return BCrypt.withDefaults().hashToString(CUSTO, senha.toCharArray());
    }

    /**
     * Compara a senha digitada com o hash guardado. Valores nulos nunca conferem.
     */
    public static boolean confere(String senha, String hash) {
        if (senha == null || hash == null) return false;
        return BCrypt.verifyer().verify(senha.toCharArray(), hash).verified;
    }
}
