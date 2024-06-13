package com.guard.epidemicguard.model;

public class Casos {

    private String nome;
    private String endereco;
    private String descricao;

    public Casos() {

    }

    public Casos(String nome, String endereco, String descricao) {
        this.nome = nome;
        this.endereco = endereco;
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
