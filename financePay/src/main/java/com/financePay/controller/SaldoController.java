package com.financePay.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.financePay.model.Saldo;
import com.financePay.repository.SaldoRepository;
import com.financePay.config.SaldoSpecification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("saldo")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Saldos", description = "API para gestão de saldos financeiros")
public class SaldoController {

    @Autowired
    private SaldoRepository saldoRepository;

    // Buscar por ID
    @GetMapping("/conta/{id}")
    @Cacheable(value = "saldo")
    @Operation(summary = "Buscar saldo por ID")
    public ResponseEntity<Saldo> getSaldoPorId(@PathVariable Long id) {
        Saldo saldo = saldoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Saldo não encontrado com ID: " + id));
        return ResponseEntity.ok(saldo);
    }

    // Criar novo
    @PostMapping("/conta")
    @CacheEvict(value = "saldo", allEntries = true)
    @Operation(summary = "Criar novo saldo")
    public ResponseEntity<Saldo> createSaldo(@RequestBody Saldo saldo) {
        saldo.taskValidarSaldo();
        return ResponseEntity.status(HttpStatus.CREATED).body(saldoRepository.save(saldo));
    }

    // Atualizar
    @PutMapping("/movimentacao")
    @CacheEvict(value = "saldo", allEntries = true)
    @Operation(summary = "Atualizar saldo existente")
    public ResponseEntity<Saldo> updateSaldo(@RequestBody Saldo saldoAtualizado) {
        Saldo saldoExistente = saldoRepository.findById(saldoAtualizado.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada para atualização"));

        if (!saldoExistente.getAtivo()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Conta inativa - não pode ser atualizada");
        }

        saldoAtualizado.taskValidar();
        saldoAtualizado.setAtivo(true);
        return ResponseEntity.ok(saldoRepository.save(saldoAtualizado));
    }

    // Listar todos
    @GetMapping("/lista")
    @Cacheable(value = "saldo")
    @Operation(summary = "Listar todos os saldos")
    public ResponseEntity<List<Saldo>> getAllSaldos() {
        List<Saldo> saldos = saldoRepository.findAll();
        if (saldos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(saldos);
    }

    // Desativar conta 
    @DeleteMapping("/desativa/{id}")
    @CacheEvict(value = "saldo", allEntries = true)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desativar uma conta")
    public void desativarConta(@PathVariable Long id) {
        Saldo saldo = saldoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada"));
        saldo.setAtivo(false);
        saldo.taskValidarStatus();
        saldoRepository.save(saldo);
    }

    // Reativar conta
    @PutMapping("/ativa/{id}")
    @CacheEvict(value = "saldo", allEntries = true)
    @Operation(summary = "Reativar uma conta")
    public ResponseEntity<Saldo> ativarConta(@PathVariable Long id) {
        Saldo saldo = saldoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada"));
        saldo.setAtivo(true);
        saldo.taskValidarStatus();
        return ResponseEntity.ok(saldoRepository.save(saldo));
    }

    // Filter
    @GetMapping("/filtro")
    @Operation(summary = "Filtrar saldos por status, valor mínimo/máximo, com paginação e ordenação")
    public ResponseEntity<Page<Saldo>> filtrarSaldos(
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) Double saldoMin,
            @RequestParam(required = false) Double saldoMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Specification<Saldo> spec = Specification.where(null);

        if (ativo != null) {
            spec = spec.and(SaldoSpecification.ativo(ativo));
        }
        if (saldoMin != null) {
            spec = spec.and(SaldoSpecification.saldoMin(saldoMin));
        }
        if (saldoMax != null) {
            spec = spec.and(SaldoSpecification.saldoMax(saldoMax));
        }

        Sort sort = direction.equalsIgnoreCase("desc") ?
            Sort.by(sortBy).descending() :
            Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Saldo> resultado = saldoRepository.findAll(spec, pageable);

        return ResponseEntity.ok(resultado);
    }
}