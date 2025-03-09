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

    @GetMapping
    public ResponseEntity<Saldo> getSaldo() {
        Saldo saldo = saldoRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Saldo não encontrado"));
        return ResponseEntity.ok(saldo);
    }
}
