package org.inventra.model;

/**
 * Fornecedor cadastrado. Há um construtor para cadastro (sem id), um completo (com a chave do endereço) e um
 * para leituras com JOIN, em que {@code endereco} já vem como texto.
 */
public class FornecedorMolde {
    private int idFornecedor;
    private int fkEndereco;
    private String nomeJuridico;
    private String cnpj;
    private String email;
    private int notaVpq;

    //VÁRIAVEL PARA SUBSTITUIR A FK
    private String endereco;

    public FornecedorMolde(int idFornecedor, int fkEndereco, String nomeJuridico, String cnpj, String email, int notaVpq) {
        this.idFornecedor = idFornecedor;
        this.fkEndereco = fkEndereco;
        this.nomeJuridico = nomeJuridico;
        this.cnpj = cnpj;
        this.email = email;
        this.notaVpq = notaVpq;
    }

    public FornecedorMolde(int fkEndereco, String nomeJuridico, String cnpj, String email, int notaVpq) {
        this.nomeJuridico = nomeJuridico;
        this.fkEndereco = fkEndereco;
        this.cnpj = cnpj;
        this.email = email;
        this.notaVpq = notaVpq;
    }

    public FornecedorMolde(int idFornecedor, String endereco, String nomeJuridico, String cnpj, String email, int notaVpq) {
        this.idFornecedor = idFornecedor;
        this.nomeJuridico = nomeJuridico;
        this.cnpj = cnpj;
        this.email = email;
        this.notaVpq = notaVpq;
        this.endereco = endereco;
    }



    public int getIdFornecedor() {
        return idFornecedor;
    }

    public String getNomeJuridico() {
        return nomeJuridico;
    }

    public void setNomeJuridico(String nomeJuridico) {
        this.nomeJuridico = nomeJuridico;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getNotaVpq() {
        return notaVpq;
    }

    public void setNotaVpq(int notaVpq) {
        this.notaVpq = notaVpq;
    }

    public int getFkEndereco() {
        return fkEndereco;
    }

    public void setFkEndereco(int fkEndereco) {
        this.fkEndereco = fkEndereco;
    }

    public String getEndereco() {
        return endereco;
    }
}

