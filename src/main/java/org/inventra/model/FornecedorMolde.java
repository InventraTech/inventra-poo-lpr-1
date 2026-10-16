package org.inventra.model;

public class FornecedorMolde {
    private int id_fornecedor;
    private int fk_endereco;
    private String nome_juridico;
    private String cnpj;
    private String email;
    private int nota_vpq;

    //VÁRIAVEL PARA SUBSTITUIR A FK
    private String endereco;

    public FornecedorMolde(int id_fornecedor, int fk_endereco, String nome_juridico, String cnpj, String email, int nota_vpq) {
        this.id_fornecedor = id_fornecedor;
        this.fk_endereco = fk_endereco;
        this.nome_juridico = nome_juridico;
        this.cnpj = cnpj;
        this.email = email;
        this.nota_vpq = nota_vpq;
    }

    public FornecedorMolde(int fk_endereco, String nome_juridico, String cnpj, String email, int nota_vpq) {
        this.nome_juridico = nome_juridico;
        this.fk_endereco = fk_endereco;
        this.cnpj = cnpj;
        this.email = email;
        this.nota_vpq = nota_vpq;
    }

    public FornecedorMolde(int id_fornecedor, String endereco, String nome_juridico, String cnpj, String email, int nota_vpq) {
        this.id_fornecedor = id_fornecedor;
        this.nome_juridico = nome_juridico;
        this.cnpj = cnpj;
        this.email = email;
        this.nota_vpq = nota_vpq;
        this.endereco = endereco;
    }



    public int getId_fornecedor() {
        return id_fornecedor;
    }

    public String getNome_juridico() {
        return nome_juridico;
    }

    public void setNome_juridico(String nome_juridico) {
        this.nome_juridico = nome_juridico;
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

    public int getNota_vpq() {
        return nota_vpq;
    }

    public void setNota_vpq(int nota_vpq) {
        this.nota_vpq = nota_vpq;
    }

    public int getFk_endereco() {
        return fk_endereco;
    }

    public void setFk_endereco(int fk_endereco) {
        this.fk_endereco = fk_endereco;
    }

    public String getEndereco() {
        return endereco;
    }
}

