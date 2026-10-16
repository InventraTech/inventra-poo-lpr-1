package org.inventra.model;

public class ProdutoMolde {
    private int idProduto;
    private String nome;
    private int fk_marca;
    private int fk_categoria;
    private String unidadeMedida;
    private int estoqueMin;
    private int estoqueMax;
    private int fk_fornecedor;
    private String descricao;
    private boolean ativo;

    //Váriaveis para puxar o nome das foreign keys
    private String marca;
    private String categoria;
    private String fornecedor;

    public ProdutoMolde (int fk_fornecedor, int fk_marca, int fk_categoria, String nome, String unidadeMedida, int estoqueMin, int estoqueMax, String descricao, boolean ativo) {
        this.fk_fornecedor = fk_fornecedor;
        this.fk_marca = fk_marca;
        this.fk_categoria = fk_categoria;
        this.nome = nome;
        this.unidadeMedida = unidadeMedida;
        this.estoqueMin = estoqueMin;
        this.estoqueMax = estoqueMax;
        this.descricao = descricao;
        this.ativo = ativo;
    }

    public ProdutoMolde(int idProduto, int fk_fornecedor, int fk_marca, int fk_categoria, String nome, String unidadeMedida, int estoqueMin, int estoqueMax, String descricao, boolean ativo) {
        this.idProduto = idProduto;
        this.fk_fornecedor = fk_fornecedor;
        this.fk_marca = fk_marca;
        this.fk_categoria = fk_categoria;
        this.nome = nome;
        this.unidadeMedida = unidadeMedida;
        this.estoqueMin = estoqueMin;
        this.estoqueMax = estoqueMax;
        this.descricao = descricao;
        this.ativo = ativo;
    }

    //CONSTRUTOR PARA SER USADO PARA OS JOINS
    public ProdutoMolde(int idProduto, String fornecedor, String marca, String categoria, String nome, String unidadeMedida, int estoqueMin, int estoqueMax, String descricao, boolean ativo){
        this.idProduto = idProduto;
        this.fornecedor = fornecedor;
        this.marca = marca;
        this.categoria = categoria;
        this.nome = nome;
        this.unidadeMedida = unidadeMedida;
        this.estoqueMin = estoqueMin;
        this.estoqueMax = estoqueMax;
        this.descricao = descricao;
        this.ativo = ativo;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getFk_marca() {
        return fk_marca;
    }

    public void setFk_marca(int fk_marca) {
        this.fk_marca = fk_marca;
    }

    public int getFk_categoria() {
        return fk_categoria;
    }

    public void setFk_categoria(int fk_categoria) {
        this.fk_categoria = fk_categoria;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public int getEstoqueMin() {
        return estoqueMin;
    }

    public void setEstoqueMin(int estoqueMin) {
        this.estoqueMin = estoqueMin;
    }

    public int getEstoqueMax() {
        return estoqueMax;
    }

    public void setEstoqueMax(int estoqueMax) {
        this.estoqueMax = estoqueMax;
    }

    public int getFk_fornecedor() {
        return fk_fornecedor;
    }

    public void setFk_fornecedor(int fk_fornecedor) {
        this.fk_fornecedor = fk_fornecedor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public String getNomeCategoria() {
        return categoria;
    }

    public String getNomeFornecedor() {
        return fornecedor;
    }

    public String getNomeMarca() {
        return marca;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
