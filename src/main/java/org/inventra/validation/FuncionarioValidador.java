
package org.inventra.validation;

import org.inventra.model.FuncionarioMolde;
import org.inventra.utils.FuncionarioREGEX;

import java.util.ArrayList;
import java.util.List;

public class FuncionarioValidador {

    public static List<String> validar(FuncionarioMolde funcionario) {

        List<String> erros = new ArrayList<>();

        if (funcionario == null) {
            erros.add("Os dados do funcionário não foram informados.");
            return erros;
        }

        // Nome
        if (funcionario.getNome() == null ||
                funcionario.getNome().isBlank()) {
            erros.add("O nome do funcionário é obrigatório.");
        }

        // Setor
        if (funcionario.getFk_setor() <= 0) {
            erros.add("Selecione um setor válido.");
        }

        // E-mail
        if (funcionario.getEmail() == null || funcionario.getEmail().isBlank()) {
            erros.add("O e-mail é obrigatório.");

        } else if (!FuncionarioREGEX.validarEmail(funcionario.getEmail())) {
            erros.add("O formato do e-mail é inválido.");
        }

        // Telefone
        if (funcionario.getTelefone() == null || funcionario.getTelefone().isBlank()) {
            erros.add("O telefone é obrigatório.");

        } else if (!FuncionarioREGEX.validarTelefone(funcionario.getTelefone())) {
            erros.add("O formato do telefone é inválido.");
        }

        // CPF
        if (funcionario.getCpf() == null || funcionario.getCpf().isBlank()) {
            erros.add("O CPF é obrigatório.");

        } else if (!FuncionarioREGEX.validarCpf(funcionario.getCpf())) {
            erros.add("O formato do CPF é inválido.");
        }

        // Data de admissão
        if (funcionario.getDt_admissao() == null) {
            erros.add("A data de admissão é obrigatória.");
        }

        // Status
        if (funcionario.getStatus() == null || funcionario.getStatus().isBlank()) {
            erros.add("O status do funcionário é obrigatório.");
        }

        return erros;
    }
}
