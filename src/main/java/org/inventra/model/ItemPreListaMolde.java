package org.inventra.model;

/**
 * Item da pré-lista de compras: produto e quantidade a comprar de um fornecedor. O construtor com nomes
 * ({@code fornecedor} e {@code produto}) é o das leituras com JOIN.
 */
public class ItemPreListaMolde {
    private int idItemPreLista;
    private int fkPreLista;
    private int fkFornecedor;
    private int fkProduto;
    private int qtd;

    //VARIAVÉIS PARA SUBSTITUIR AS FK
    private String fornecedor;
    private String produto;

    public ItemPreListaMolde(int idItemPreLista, int fkPreLista, int fkFornecedor, int fkProduto, int qtd) {
        this.idItemPreLista = idItemPreLista;
        this.fkPreLista = fkPreLista;
        this.fkFornecedor = fkFornecedor;
        this.fkProduto = fkProduto;
        this.qtd = qtd;
    }

    public ItemPreListaMolde(int fkPreLista, int fkFornecedor, int fkProduto, int qtd) {
        this.fkPreLista = fkPreLista;
        this.fkFornecedor = fkFornecedor;
        this.fkProduto = fkProduto;
        this.qtd = qtd;
    }

    public ItemPreListaMolde(int idItemPreLista, String fornecedor, String produto, int qtd) {
        this.idItemPreLista = idItemPreLista;
        this.fornecedor = fornecedor;
        this.produto = produto;
        this.qtd = qtd;
    }

    public int getIdItemPreLista() {
        return idItemPreLista;
    }

    public int getFkPreLista() {
        return fkPreLista;
    }

    public void setFkPreLista(int fkPreLista) {
        this.fkPreLista = fkPreLista;
    }

    public int getFkFornecedor() {
        return fkFornecedor;
    }

    public void setFkFornecedor(int fkFornecedor) {
        this.fkFornecedor = fkFornecedor;
    }

    public int getFkProduto() {
        return fkProduto;
    }

    public void setFkProduto(int fkProduto) {
        this.fkProduto = fkProduto;
    }

    public int getQtd() {
        return qtd;
    }

    public void setQtd(int qtd) {
        this.qtd = qtd;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public String getProduto() {
        return produto;
    }
}
