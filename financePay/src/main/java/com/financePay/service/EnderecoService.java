package com.financePay.service;

import com.financePay.model.EnderecoCep;
import com.financePay.model.UserBasic;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EnderecoService {

    private static final String VIA_CEP_URL = "https://viacep.com.br/ws/{cep}/json/";

    public EnderecoCep buscaEnderecoCep(String cep) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(VIA_CEP_URL, EnderecoCep.class, cep);
    }

    public UserBasic preencherEndereco(UserBasic user) {
        if (user.getCep() != null && !user.getCep().isEmpty()) {
            EnderecoCep endereco = buscaEnderecoCep(user.getCep());
            if (endereco == null || endereco.getLogradouro() == null || endereco.getLogradouro().isEmpty()
                    || endereco.getLocalidade() == null || endereco.getLocalidade().isEmpty()) {
                throw new IllegalArgumentException("Erro ao preencher endereço: dados incompletos para o CEP informado.");
            }
            user.setRua(endereco.getLogradouro());
            user.setCidade(endereco.getLocalidade());
        }
        return user;
    }
}
