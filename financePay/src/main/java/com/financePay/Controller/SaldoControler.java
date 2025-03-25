package com.financePay.controller;

import com.financePay.model.Saldo;
import com.financePay.repository.SaldoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("saldo")
public class SaldoControler {

    @Autowired 
    private SaldoRepository saldoRepository; 

    // Pegar saldo por ID
    @GetMapping("getsaldo/{id}")
    public ResponseEntity<Saldo> getSaldoPorId(@PathVariable Long id) {
        try {
            Saldo saldo = saldoRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Saldo não encontrado"));
            return ResponseEntity.ok(saldo);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao buscar saldo");
        }
    }

    // Criar saldo
    @PostMapping("/createsaldo")
    public ResponseEntity<Saldo> createSaldo(@RequestBody Saldo saldo) {
        try {
            saldo.taskValidarSaldo();
            Saldo novoSaldo = saldoRepository.save(saldo);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoSaldo);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao criar saldo: " + e.getMessage());
        }
    }
    
    // Atualizar saldo
    @PutMapping("/updatesaldo")
    public ResponseEntity<Saldo> updateSaldo(@RequestBody Saldo saldoAtualizado) {
        try {
            saldoAtualizado.taskValidar(); 
            return ResponseEntity.ok(saldoRepository.save(saldoAtualizado));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Erro ao atualizar saldo: " + e.getMessage());
        }
    }
    
    // Deletar saldo
    @DeleteMapping("/deletesaldo/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Certo mas sem conteudo rs
    public void deleteSaldo(@PathVariable Long id) {
        try {
            Saldo saldo = saldoRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Saldo não encontrado"));
            saldoRepository.delete(saldo);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao deletar saldo");
        }
    }

    // Pegar todos os saldos
    @GetMapping("/allSaldo")
    public ResponseEntity<List<Saldo>> get() {
        try {
            return ResponseEntity.ok(saldoRepository.findAll());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao buscar saldos");
        }
    }
}