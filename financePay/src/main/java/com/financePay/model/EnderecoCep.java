package com.financePay.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnderecoCep {
    private String cep;
    private String logradouro; // Rua
    private String complemento;
    private String bairro;
    private String localidade; // Cidade
    private String uf;
}