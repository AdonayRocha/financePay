package com.financePay.Controller;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.financePay.Model.Saldo;
import com.financePay.Repository.SaldoRepository;

@RestController
@RequestMapping("saldo")
public class SaldoControler {

    private final Logger logger = LoggerFactory.getLogger(SaldoControler.class);

    @Autowired 
    private SaldoRepository saldoRepository; 

    // Pega saldo
    @GetMapping("getsaldo/{id}")
    public ResponseEntity<Saldo> getSaldoPorId(@PathVariable Long id) {
        Saldo saldo = saldoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Saldo não encontrado ou inexistente"));
    
        if (saldo.getSaldo() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo encontrado, valor nulo");
        }
    
        return ResponseEntity.ok(saldo);
    }

    // Criar saldo
    @PostMapping("/createsaldo")
    public ResponseEntity<Saldo> createSaldo(@RequestBody Saldo saldo) {
        Saldo novoSaldo = saldoRepository.save(saldo);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoSaldo);
    }
    
    // Atualiza saldo
    @PutMapping("/updatesaldo/{id}")
    public ResponseEntity<Saldo> updateSaldo(@PathVariable Long id, @RequestBody Saldo saldoAtualizado) {
        Saldo saldo = saldoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Saldo não encontrado"));
    
        saldo.setSaldo(saldoAtualizado.getSaldo());
        saldoRepository.save(saldo);
        return ResponseEntity.ok(saldo);
    }
    
    // Deleta saldo
    @DeleteMapping("/deletesaldo/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSaldo(@PathVariable Long id) {
        Saldo saldo = saldoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Saldo não encontrado"));
        saldoRepository.delete(saldo);
    }
}