package org.inventra.model;

import java.time.LocalDateTime;

public class RequisicaoMolde {
    private int idRequisicao;
    private int idTipoRequisicao;
    private int quantidadeProduto;
    private String motivo;
    private String status;
    private LocalDateTime dataHora;
    private int fkFuncionarioSolicitante;
    private int fkFuncionarioAprovador;
    private int fkProduto;

    //VÁRIAVEL PARA SUBSTITUIR A FK
    private String tipoRequisicao;
    private String funcionarioSolicitante;
    private String funcionarioAprovador;
    private String produto;

    public RequisicaoMolde(int id_tipoRequisicao, int fkFuncionarioSolicitante, int fkFuncionarioAprovador, int fkProduto, int quantidadeProduto, String motivo, String status, LocalDateTime dataHora) {
        this.idTipoRequisicao = id_tipoRequisicao;
        this.quantidadeProduto = quantidadeProduto;
        this.motivo = motivo;
        this.status = status;
        this.dataHora = dataHora;
        this.fkFuncionarioSolicitante = fkFuncionarioSolicitante;
        this.fkFuncionarioAprovador = fkFuncionarioAprovador;
        this.fkProduto = fkProduto;
    }

    public RequisicaoMolde(int idRequisicao, String tipoRequisicao, String funcionarioSolicitante, String funcionarioAprovador, String produto, int quantidadeProduto, String motivo, String status, LocalDateTime dataHora) {
        this.idRequisicao = idRequisicao;
        this.tipoRequisicao = tipoRequisicao;
        this.quantidadeProduto = quantidadeProduto;
        this.motivo = motivo;
        this.status = status;
        this.dataHora = dataHora;
        this.funcionarioSolicitante = funcionarioSolicitante;
        this.funcionarioAprovador = funcionarioAprovador;
        this.produto = produto;
    }

    public RequisicaoMolde(int idRequisicao, int idTipoRequisicao, int fkFuncionarioSolicitante, int fkFuncionarioAprovador, int fkProduto, int quantidadeProduto, String motivo, String status, LocalDateTime dataHora) {
        this.idRequisicao = idRequisicao;
        this.idTipoRequisicao = idTipoRequisicao;
        this.quantidadeProduto = quantidadeProduto;
        this.motivo = motivo;
        this.status = status;
        this.dataHora = dataHora;
        this.fkFuncionarioSolicitante = fkFuncionarioSolicitante;
        this.fkFuncionarioAprovador = fkFuncionarioAprovador;
        this.fkProduto = fkProduto;
    }

    public int getIdRequisicao() {
        return idRequisicao;
    }

    public void setIdRequisicao(int idRequisicao) {
        this.idRequisicao = idRequisicao;
    }

    public int getIdTipoRequisicao() {
        return idTipoRequisicao;
    }

    public void setIdTipoRequisicao(int idTipoRequisicao) {
        this.idTipoRequisicao = idTipoRequisicao;
    }

    public int getQuantidadeProduto() {
        return quantidadeProduto;
    }

    public void setQuantidadeProduto(int quantidadeProduto) {
        this.quantidadeProduto = quantidadeProduto;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public int getFkFuncionarioSolicitante() {
        return fkFuncionarioSolicitante;
    }

    public void setFkFuncionarioSolicitante(int fkFuncionarioSolicitante) {
        this.fkFuncionarioSolicitante = fkFuncionarioSolicitante;
    }

    public int getFkFuncionarioAprovador() {
        return fkFuncionarioAprovador;
    }

    public void setFkFuncionarioAprovador(int fkFuncionarioAprovador) {
        this.fkFuncionarioAprovador = fkFuncionarioAprovador;
    }

    public int getFkProduto() {
        return fkProduto;
    }

    public void setFkProduto(int fkProduto) {
        this.fkProduto = fkProduto;
    }

    public String getFuncionarioAprovador() {
        return funcionarioAprovador;
    }

    public String getFuncionarioSolicitante() {
        return funcionarioSolicitante;
    }

    public String getProduto() {
        return produto;
    }

    public String getTipoRequisicao() {
        return tipoRequisicao;
    }
}
