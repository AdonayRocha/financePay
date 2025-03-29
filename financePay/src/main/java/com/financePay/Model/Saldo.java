package com.financePay.model;

import jakarta.persistence.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Entity
public class Saldo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double saldo;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean ativo = true;

    public void taskValidar() {
        validacaoId();
        validacaoSaldo();
        validacaoStatus();
    }

    public void taskValidarSaldo() {
        validacaoSaldo();
    }

    public void taskValidarStatus(){
        validacaoId();
        validacaoStatus();
    }

    private void validacaoId() {
        if (id == null) {  
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identificação do saldo não pode ser ausente");
        } 
        
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identificação inválida: Valor deve ser positivo");
        }
    }

    private void validacaoSaldo() {
        if (saldo == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor do saldo é obrigatório e não pode ser nulo");
        }
    }

    private void validacaoStatus() {
        if (ativo == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status da conta deve ser definido (true/false)");
        }
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        validacaoId();
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
        validacaoSaldo();
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
        validacaoStatus();
    }
}