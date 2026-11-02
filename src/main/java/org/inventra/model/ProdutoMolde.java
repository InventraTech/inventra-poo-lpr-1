package org.inventra.model;

/**
 * Produto do catálogo. Não guarda quantidade nem validade (isso é do lote). Os campos de texto marca, categoria
 * e fornecedor só são preenchidos nas leituras com JOIN.
 */
public class ProdutoMolde {
    private int idProduto;
    private String nome;
    private int fkMarca;
    private int fkCategoria;
    private String unidadeMedida;
    private int estoqueMin;
    private int estoqueMax;
    private int fkFornecedor;
    private String descricao;
    private boolean ativo;

    //Váriaveis para puxar o nome das foreign keys
    private String marca;
    private String categoria;
    private String fornecedor;

    public ProdutoMolde (int fkFornecedor, int fkMarca, int fkCategoria, String nome, String unidadeMedida, int estoqueMin, int estoqueMax, String descricao, boolean ativo) {
        this.fkFornecedor = fkFornecedor;
        this.fkMarca = fkMarca;
        this.fkCategoria = fkCategoria;
        this.nome = nome;
        this.unidadeMedida = unidadeMedida;
        this.estoqueMin = estoqueMin;
        this.estoqueMax = estoqueMax;
        this.descricao = descricao;
        this.ativo = ativo;
    }

    public ProdutoMolde(int idProduto, int fkFornecedor, int fkMarca, int fkCategoria, String nome, String unidadeMedida, int estoqueMin, int estoqueMax, String descricao, boolean ativo) {
        this.idProduto = idProduto;
        this.fkFornecedor = fkFornecedor;
        this.fkMarca = fkMarca;
        this.fkCategoria = fkCategoria;
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

    public int getFkMarca() {
        return fkMarca;
    }

    public void setFkMarca(int fkMarca) {
        this.fkMarca = fkMarca;
    }

    public int getFkCategoria() {
        return fkCategoria;
    }

    public void setFkCategoria(int fkCategoria) {
        this.fkCategoria = fkCategoria;
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

    public int getFkFornecedor() {
        return fkFornecedor;
    }

    public void setFkFornecedor(int fkFornecedor) {
        this.fkFornecedor = fkFornecedor;
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

    // Estes três getters devolvem os nomes obtidos pelo JOIN (não os ids); são eles que as telas exibem.
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
