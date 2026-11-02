
package org.inventra.validation;

import org.inventra.model.FornecedorMolde;
import org.inventra.utils.FornecedorREGEX;

import java.util.ArrayList;
import java.util.List;

/**
 * Regras de cadastro e edição de fornecedor. CNPJ, e-mail e telefone são conferidos apenas quanto ao formato
 * (ver FornecedorREGEX); a nota VPQ não pode ser negativa.
 */
public class FornecedorValidador {

    /**
     * Confere os dados já montados a partir dos parâmetros HTTP e devolve as inconsistências encontradas.
     * O telefone é recebido à parte porque o modelo do fornecedor não o guarda.
     *
     * @return mensagens de erro prontas para exibir ao usuário; lista vazia quando os dados são válidos
     */
    public static List<String> validar(
            FornecedorMolde fornecedor,
            String telefone) {

        List<String> erros = new ArrayList<>();

        if (fornecedor == null) {
            erros.add("Os dados do fornecedor não foram informados.");
            return erros;
        }

        // Nome jurídico
        if (fornecedor.getNomeJuridico() == null ||
                fornecedor.getNomeJuridico().isBlank()) {
            erros.add("O nome jurídico do fornecedor é obrigatório.");
        }

        // Endereço
        if (fornecedor.getFkEndereco() <= 0) {
            erros.add("Selecione um endereço válido.");
        }

        // CNPJ
        if (fornecedor.getCnpj() == null ||
                fornecedor.getCnpj().isBlank()) {
            erros.add("O CNPJ é obrigatório.");

        } else if (!FornecedorREGEX.validarCNPJ(
                fornecedor.getCnpj())) {
            erros.add("O formato do CNPJ é inválido.");
        }

        // E-mail
        if (fornecedor.getEmail() == null ||
                fornecedor.getEmail().isBlank()) {
            erros.add("O e-mail do fornecedor é obrigatório.");

        } else if (!FornecedorREGEX.validarEmail(
                fornecedor.getEmail())) {
            erros.add("O formato do e-mail é inválido.");
        }

        // Telefone
        if (telefone == null || telefone.isBlank()) {
            erros.add("O telefone do fornecedor é obrigatório.");

        } else if (!FornecedorREGEX.validarTelefone(telefone)) {
            erros.add("O formato do telefone é inválido.");
        }

        // Nota VPQ
        if (fornecedor.getNotaVpq() < 0) {
            erros.add("A nota VPQ não pode ser negativa.");
        }

        return erros;
    }
}
