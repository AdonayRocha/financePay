package com.financePay.controller;

import com.financePay.model.UserBasic;
import com.financePay.repository.UserBasicRepository;
import com.financePay.service.EnderecoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UserBasicController {

    @Autowired
    private UserBasicRepository userRepo;

    @Autowired
    private EnderecoService enderecoService;

    @PostMapping
    @CacheEvict(value = "usuarios", allEntries = true)
    @Operation(summary = "Criar novo usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<UserBasic> criarUsuario(@RequestBody UserBasic usuario) {
        try {
            usuario = enderecoService.preencherEndereco(usuario);
            UserBasic salvo = userRepo.save(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Cacheable(value = "usuarios")
    @Operation(summary = "Buscar usuário por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserBasic> getUsuarioPorId(
            @Parameter(description = "ID do usuário") @PathVariable Long id) {
        UserBasic usuario = userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado com ID: " + id));
        return ResponseEntity.ok(usuario);
    }

    @PutMapping
    @CacheEvict(value = "usuarios", allEntries = true)
    @Operation(summary = "Atualizar usuário existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserBasic> atualizarUsuario(@RequestBody UserBasic usuarioAtualizado) {
        UserBasic usuarioExistente = userRepo.findById(usuarioAtualizado.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado para atualização"));
        usuarioAtualizado.setAtivo(true);
        usuarioAtualizado = enderecoService.preencherEndereco(usuarioAtualizado);
        return ResponseEntity.ok(userRepo.save(usuarioAtualizado));
    }

    @GetMapping("/lista")
    @Cacheable(value = "usuarios")
    @Operation(summary = "Listar todos os usuários")
    public ResponseEntity<List<UserBasic>> listarUsuarios() {
        List<UserBasic> usuarios = userRepo.findAll();
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usuarios);
    }

    @DeleteMapping("/desativa/{id}")
    @CacheEvict(value = "usuarios", allEntries = true)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desativar um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário desativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public void desativarUsuario(@PathVariable Long id) {
        UserBasic usuario = userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        usuario.setAtivo(false);
        userRepo.save(usuario);
    }
}
