package com.financePay.modela;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;

@Entity
@Schema(name = "Saldo", description = "Entidade que representa um saldo financeiro")
public class Saldo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Min(value = 1, message = "ID deve ser maior que 0")
    @Schema(description = "Identificador único do saldo", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Column(precision = 19, scale = 2)
    @Schema(description = "Valor monetário atual do saldo", example = "1500.50", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal saldo;

    @Column(columnDefinition = "Boolean Default True")
    @Schema(description = "Status de ativação da conta", example = "true", defaultValue = "true")
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

    public void taskValidarId(){
        validacaoId();
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        validacaoId();
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
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