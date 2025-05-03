package com.financePay.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.financePay.model.Saldo;
import com.financePay.repository.SaldoRepository;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private SaldoRepository saldoRepository;

    @Override
    public void run(String... args) throws Exception {
        if (saldoRepository.count() == 0) {
            Saldo saldo1 = new Saldo();
            saldo1.setSaldo(new java.math.BigDecimal("100.0"));
            saldo1.setAtivo(true);

            Saldo saldo2 = new Saldo();
            saldo2.setSaldo(new java.math.BigDecimal("200.0"));
            saldo2.setAtivo(true);

            saldoRepository.save(saldo1);
            saldoRepository.save(saldo2);

            System.out.println("Banco inicial para teste.");
        }
    }
}