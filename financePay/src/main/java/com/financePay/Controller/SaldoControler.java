package com.financePay.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("saldo")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Saldos", description = "API para gestão de saldos financeiros")
public class SaldoControler {

    @Autowired
    private SaldoRepository saldoRepository;

    // Conta {id}
    @Operation(
        summary = "Buscar saldo por ID",
        description = "Recupera um saldo específico pelo seu identificador único"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Saldo encontrado"),
        @ApiResponse(responseCode = "400", description = "ID inválido"),
        @ApiResponse(responseCode = "404", description = "Saldo não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @Cacheable(value = "saldo")
    @GetMapping("/conta/{id}")
    public ResponseEntity<Saldo> getSaldoPorId(
        @Parameter(description = "ID do saldo", example = "1")
        @PathVariable Long id) {
        
        try {
            Saldo saldo = saldoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, 
                    "Registro financeiro não localizado para o ID: " + id
                ));

            saldo.taskValidarId();
            return ResponseEntity.ok(saldo);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Falha na comunicação com o banco de dados: " + e.getMessage()
            );
        }
    }

    // Conta
    @Operation(
        summary = "Criar novo saldo",
        description = "Cadastra um novo registro de saldo financeiro"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Saldo criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @CacheEvict(value = "saldo", allEntries = true)
    @PostMapping("/conta")
    public ResponseEntity<Saldo> createSaldo(
        @Parameter(description = "Objeto Saldo para criação", required = true)
        @RequestBody Saldo saldo) {
        
        try {
            saldo.taskValidarSaldo();
            Saldo novoSaldo = saldoRepository.save(saldo);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoSaldo);
        } catch (Exception e) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro ao criar o saldo: " + e.getMessage()
            );
        }
    }

    // Movimentacao
    @Operation(
        summary = "Atualizar saldo",
        description = "Atualiza os dados de um saldo existente"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Saldo atualizado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou conta inativa"),
        @ApiResponse(responseCode = "404", description = "Saldo não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @CacheEvict(value = "saldo", allEntries = true)
    @PutMapping("/movimentacao")
    public ResponseEntity<Saldo> updateSaldo(
        @Parameter(description = "Objeto Saldo atualizado", required = true)
        @RequestBody Saldo saldoAtualizado) {
        
        try {
            Saldo contaExistente = saldoRepository.findById(saldoAtualizado.getId())
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Conta não encontrada para atualização"
                ));
                
            if (!contaExistente.getAtivo()) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Conta inativa - Não pode ser atualizada"
                );
            }

            saldoAtualizado.taskValidar();
            saldoAtualizado.setAtivo(true);
            return ResponseEntity.ok(saldoRepository.save(saldoAtualizado));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro ao atualizar saldo: " + e.getMessage()
            );
        }
    }

    // Lista
    @Operation(
        summary = "Listar todos os saldos",
        description = "Retorna todos os saldos cadastrados no sistema"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de saldos retornada"),
        @ApiResponse(responseCode = "204", description = "Nenhum saldo encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @Cacheable(value = "saldo")
    @GetMapping("/lista")
    public ResponseEntity<List<Saldo>> getAllSaldos() {
        try {
            List<Saldo> saldos = saldoRepository.findAll();

            if (saldos.isEmpty()) {
                throw new ResponseStatusException(
                    HttpStatus.NO_CONTENT,
                    "Nenhum registro de saldo encontrado no sistema"
                );
            }

            return ResponseEntity.ok(saldos);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro na recuperação de registros financeiros: " + e.getMessage()
            );
        }
    }

    // Desativa
    @Operation(
        summary = "Desativar conta",
        description = "Desativa uma conta existente (exclusão lógica)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Conta desativada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Conta não encontrada"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @CacheEvict(value = "saldo", allEntries = true)
    @DeleteMapping("/desativa/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSaldo(
        @Parameter(description = "ID da conta para desativação", example = "1")
        @PathVariable Long id) {
        
        try {
            Saldo saldo = saldoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Não foi possível localizar o saldo para exclusão com ID: " + id
                ));

            saldo.setAtivo(false);
            saldo.taskValidarStatus();
            saldoRepository.save(saldo);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Falha crítica ao executar operação de exclusão: " + e.getMessage()
            );
        }
    }

    // Ativa
    @Operation(
        summary = "Reativar conta",
        description = "Reativa uma conta previamente desativada"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Conta reativada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Conta não encontrada"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @CacheEvict(value = "saldo", allEntries = true)
    @PutMapping("/ativa/{id}")
    public ResponseEntity<Saldo> ativarConta(
        @Parameter(description = "ID da conta para reativação", example = "1")
        @PathVariable Long id) {
        
        try {
            Saldo saldo = saldoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Conta não encontrada com ID: " + id
                ));

            saldo.setAtivo(true);
            saldo.taskValidarStatus();
            Saldo contaAtivada = saldoRepository.save(saldo);
            return ResponseEntity.ok(contaAtivada);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro ao ativar a conta: " + e.getMessage()
            );
        }
    }
}
