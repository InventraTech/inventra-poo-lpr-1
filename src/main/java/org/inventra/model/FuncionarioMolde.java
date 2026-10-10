package org.inventra.model;

import java.time.LocalDate;

public class FuncionarioMolde {
    private int id_funcionario;
    private String nome;
    private String senha;
    private int fk_setor;
    private String email;
    private String telefone;
    private String cpf;
    private LocalDate dt_admissao;
    private String status;
    private boolean isAdmin;

    //VÁRIAVEL PARA SUBSTITUIR A FK
    private String setor;

    public FuncionarioMolde(String nome, String senha, String email, String telefone, String cpf, LocalDate dt_admissao, String status, int fk_setor, boolean isAdmin) {
        this.nome = nome;
        this.senha = senha;
        this.fk_setor = fk_setor;
        this.email = email;
        this.telefone = telefone;
        this.cpf = cpf;
        this.dt_admissao = dt_admissao;
        this.status = status;
        this.isAdmin = isAdmin;
    }


    //SOBRECARGA PARA USAR O JOIN E SUBSTITUIR A FK
    public FuncionarioMolde(int id_funcionario, String nome, String email, String telefone, String cpf, LocalDate dt_admissao, String status, String setor, boolean isAdmin) {
        this.id_funcionario = id_funcionario;
        this.nome = nome;
        this.setor = setor;
        this.email = email;
        this.telefone = telefone;
        this.cpf = cpf;
        this.dt_admissao = dt_admissao;
        this.status = status;
        this.isAdmin = isAdmin;
        this.senha = null;
    }


    public int getId_funcionario() {
        return id_funcionario;
    }

    public void setId_funcionario(int id_funcionario) {
        this.id_funcionario = id_funcionario;
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

    public int getFk_setor() {
        return fk_setor;
    }

    public void setFk_setor(int fk_setor) {
        this.fk_setor = fk_setor;
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

    public LocalDate getDt_admissao() {
        return dt_admissao;
    }

    public void setDt_admissao(LocalDate dt_admissao) {
        this.dt_admissao = dt_admissao;
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
        return "id: "+ id_funcionario + " nome: " + nome + " email: " + email + " telefone: " + telefone + " cpf: " + cpf + "dt_adimissao" + dt_admissao + " status" + status + " fk_setor: " + fk_setor + " isAdmin: "+ isAdmin;
    }
}
