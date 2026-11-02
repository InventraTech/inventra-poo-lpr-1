package org.inventra.model;

import java.time.LocalDate;

/**
 * Funcionário do sistema. Um construtor serve ao cadastro e à edição (chave do setor e senha); o outro
 * às leituras com JOIN (nome do setor, sem senha). {@code isAdmin} define quem pode acessar o sistema.
 */
public class FuncionarioMolde {
    private int idFuncionario;
    private String nome;
    private String senha;
    private int fkSetor;
    private String email;
    private String telefone;
    private String cpf;
    private LocalDate dtAdmissao;
    private String status;
    private boolean isAdmin;

    //VÁRIAVEL PARA SUBSTITUIR A FK
    private String setor;

    public FuncionarioMolde(int fkSetor, String nome, String senha, String email, String telefone, String cpf, LocalDate dtAdmissao, String status, boolean isAdmin) {
        this.nome = nome;
        this.senha = senha;
        this.fkSetor = fkSetor;
        this.email = email;
        this.telefone = telefone;
        this.cpf = cpf;
        this.dtAdmissao = dtAdmissao;
        this.status = status;
        this.isAdmin = isAdmin;
    }


    //SOBRECARGA PARA USAR O JOIN E SUBSTITUIR A FK
    public FuncionarioMolde(int idFuncionario, String nome, String email, String telefone, String cpf, LocalDate dtAdmissao, String status, String setor, boolean isAdmin) {
        this.idFuncionario = idFuncionario;
        this.nome = nome;
        this.setor = setor;
        this.email = email;
        this.telefone = telefone;
        this.cpf = cpf;
        this.dtAdmissao = dtAdmissao;
        this.status = status;
        this.isAdmin = isAdmin;
        this.senha = null;
    }


    public int getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(int idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public int getFkSetor() {
        return fkSetor;
    }

    public void setFkSetor(int fkSetor) {
        this.fkSetor = fkSetor;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getDtAdmissao() {
        return dtAdmissao;
    }

    public void setDtAdmissao(LocalDate dtAdmissao) {
        this.dtAdmissao = dtAdmissao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSetor() {
        return setor;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    @Override
    public String toString(){
        return "id: "+ idFuncionario + " nome: " + nome + " email: " + email + " telefone: " + telefone + " cpf: " + cpf + "dt_adimissao" + dtAdmissao + " status" + status + " fk_setor: " + fkSetor + " isAdmin: "+ isAdmin;
    }
}
