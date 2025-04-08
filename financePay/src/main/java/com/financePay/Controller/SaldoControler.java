package com.financePay.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.financePay.model.Saldo;
import com.financePay.repository.SaldoRepository;

@RestController
@RequestMapping("saldo")
public class SaldoControler {

    @Autowired 
    private SaldoRepository saldoRepository; 

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/conta/{id}")
    public ResponseEntity<Saldo> getSaldoPorId(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID deve ser um número positivo maior que zero");
            }

            Saldo saldo = saldoRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro financeiro não localizado para o ID: " + id));

            return ResponseEntity.ok(saldo);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Falha na comunicação com o banco de dados: " + e.getMessage());
        }
    }

    @CacheEvict(value = "saldo", allEntries=true)
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/novo")
    public ResponseEntity<Saldo> createSaldo(@RequestBody Saldo saldo) {
        try {
            if (saldo == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados completos do saldo devem ser fornecidos no corpo da requisição");
            }

            saldo.taskValidarSaldo();
            Saldo novoSaldo = saldoRepository.save(saldo);

            return ResponseEntity.status(HttpStatus.CREATED).body(novoSaldo);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro inesperado ao processar a criação do saldo: " + e.getMessage());
        }
    }
    
    @CacheEvict(value = "saldo", allEntries=true)
    @CrossOrigin(origins = "http://localhost:3000")
    @PutMapping("/atualizar")
    public ResponseEntity<Saldo> updateSaldo(@RequestBody Saldo saldoAtualizado) {
        try {
            if (saldoAtualizado == null || saldoAtualizado.getId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados do saldo e ID válido devem ser fornecidos para atualização");
            }
    
            Saldo contaExistente = saldoRepository.findById(saldoAtualizado.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada para atualização"));
    
            if (!contaExistente.getAtivo()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Conta inativa - Não pode ser atualizada");
            }

            saldoAtualizado.taskValidar();
            saldoAtualizado.setAtivo(true);
            
            return ResponseEntity.ok(saldoRepository.save(saldoAtualizado));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Serviço temporariamente indisponível. Tente novamente mais tarde: " + e.getMessage());
        }
    }
    
    @CacheEvict(value = "saldo", allEntries=true)
    @CrossOrigin(origins = "http://localhost:3000")
    @DeleteMapping("/deletar/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSaldo(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID inválido: Deve ser um valor numérico positivo");
            }

            Saldo saldo = saldoRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Não foi possível localizar o saldo para exclusão com ID: " + id));

            saldo.setAtivo(false);
            saldoRepository.save(saldo);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Falha crítica ao executar operação de exclusão: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/lista")
    public ResponseEntity<List<Saldo>> getAllSaldos() {
        try {
            List<Saldo> saldos = saldoRepository.findAll();

            if (saldos.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Nenhum registro de saldo encontrado no sistema");
            }

            return ResponseEntity.ok(saldos);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro na recuperação de registros financeiros: " + e.getMessage());
        }
    }

    @CacheEvict(value = "saldo", allEntries=true)
    @CrossOrigin(origins = "http://localhost:3000")
    @PutMapping("/ativar/{id}")
    public ResponseEntity<Saldo> ativarConta(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID inválido: Deve ser um valor numérico positivo");
            }

            Saldo saldo = saldoRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada com ID: " + id));

            saldo.setAtivo(true);
            Saldo contaAtivada = saldoRepository.save(saldo);

            return ResponseEntity.ok(contaAtivada);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao ativar a conta: " + e.getMessage());
        }
    }
}