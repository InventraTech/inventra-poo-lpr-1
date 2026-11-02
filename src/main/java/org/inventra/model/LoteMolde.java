package org.inventra.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entrada de um produto no estoque. {@code qtdInicial} é o que chegou e {@code qtdAtual} o que ainda resta;
 * a validade é do lote, não do produto. O construtor com {@code produto} em texto é o das leituras com JOIN.
 */
public class LoteMolde {
    private int idLote;
    private int fkProduto;
    private String numeroLote;
    private int qtdInicial;
    private int qtdAtual;
    private LocalDate dtEntrada;
    private LocalDate dtValidade;
    private BigDecimal valorCompra;
    private String notaFiscal;

    //VÁRIAVEL PARA SUBSTITUIR A FK
    private String produto;

    // Construtor de cadastro: sem id, que é gerado pelo banco.
    public LoteMolde(int fkProduto, String numeroLote, int qtdInicial, int qtdAtual, LocalDate dtEntrada, LocalDate dtValidade, BigDecimal valorCompra, String notaFiscal) {
        this.fkProduto = fkProduto;
        this.numeroLote = numeroLote;
        this.qtdInicial = qtdInicial;
        this.qtdAtual = qtdAtual;
        this.dtEntrada = dtEntrada;
        this.dtValidade = dtValidade;
        this.valorCompra = valorCompra;
        this.notaFiscal = notaFiscal;
    }

    public LoteMolde(int idLote, int fkProduto, String numeroLote, int qtdInicial, int qtdAtual, LocalDate dtEntrada, LocalDate dtValidade, BigDecimal valorCompra, String notaFiscal) {
        this.idLote = idLote;
        this.fkProduto = fkProduto;
        this.numeroLote = numeroLote;
        this.qtdInicial = qtdInicial;
        this.qtdAtual = qtdAtual;
        this.dtEntrada = dtEntrada;
        this.dtValidade = dtValidade;
        this.valorCompra = valorCompra;
        this.notaFiscal = notaFiscal;
    }

    public LoteMolde(int idLote, String produto, String numeroLote, int qtdInicial, int qtdAtual, LocalDate dtEntrada, LocalDate dtValidade, BigDecimal valorCompra, String notaFiscal) {
        this.idLote = idLote;
        this.produto = produto;
        this.numeroLote = numeroLote;
        this.qtdInicial = qtdInicial;
        this.qtdAtual = qtdAtual;
        this.dtEntrada = dtEntrada;
        this.dtValidade = dtValidade;
        this.valorCompra = valorCompra;
        this.notaFiscal = notaFiscal;
    }

    public int getIdLote() {
        return idLote;
    }

    public void setIdLote(int idLote) {
        this.idLote = idLote;
    }

    public int getFkProduto() {
        return fkProduto;
    }

    public void setFkProduto(int fkProduto) {
        this.fkProduto = fkProduto;
    }

    public String getNumeroLote() {
        return numeroLote;
    }

    public void setNumeroLote(String numeroLote) {
        this.numeroLote = numeroLote;
    }

    public int getQtdInicial() {
        return qtdInicial;
    }

    public void setQtdInicial(int qtdInicial) {
        this.qtdInicial = qtdInicial;
    }

    public int getQtdAtual() {
        return qtdAtual;
    }

    public void setQtdAtual(int qtdAtual) {
        this.qtdAtual = qtdAtual;
    }

    public LocalDate getDtEntrada() {
        return dtEntrada;
    }

    public void setDtEntrada(LocalDate dtEntrada) {
        this.dtEntrada = dtEntrada;
    }

    public LocalDate getDtValidade() {
        return dtValidade;
    }

    public void setDtValidade(LocalDate dtValidade) {
        this.dtValidade = dtValidade;
    }

    public BigDecimal getValorCompra() {
        return valorCompra;
    }

    public void setValorCompra(BigDecimal valorCompra) {
        this.valorCompra = valorCompra;
    }

    public String getNotaFiscal() {
        return notaFiscal;
    }

    public void setNotaFiscal(String notaFiscal) {
        this.notaFiscal = notaFiscal;
    }

    public String getProduto() {
        return produto;
    }
}