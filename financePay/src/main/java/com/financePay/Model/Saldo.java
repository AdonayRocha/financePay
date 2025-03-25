package com.financePay.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Entity
public class Saldo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double saldo;



    public void taskValidar() {
        validacaoId();
        validacaoSaldo();
    }

    public void taskValidarSaldo() {
        validacaoSaldo();
    }

    private void validacaoId() {
        if (id == null) {  
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID Null");
        } 
        
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID Não pode ser menor ou igual a 0");
        }
    }

    private void validacaoSaldo() {
        if (saldo == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O saldo não pode ser nulo");
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
}