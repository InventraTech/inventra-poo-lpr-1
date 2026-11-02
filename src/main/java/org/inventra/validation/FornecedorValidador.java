
package org.inventra.validation;

import org.inventra.model.FornecedorMolde;
import org.inventra.utils.FornecedorREGEX;

import java.util.ArrayList;
import java.util.List;

public class FornecedorValidador {

    public static List<String> validar(
            FornecedorMolde fornecedor,
            String telefone) {

        List<String> erros = new ArrayList<>();

        if (fornecedor == null) {
            erros.add("Os dados do fornecedor não foram informados.");
            return erros;
        }

        // Nome jurídico
        if (fornecedor.getNome_juridico() == null ||
                fornecedor.getNome_juridico().isBlank()) {
            erros.add("O nome jurídico do fornecedor é obrigatório.");
        }

        // Endereço
        if (fornecedor.getFk_endereco() <= 0) {
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
        if (fornecedor.getNota_vpq() < 0) {
            erros.add("A nota VPQ não pode ser negativa.");
        }

        return erros;
    }
}
