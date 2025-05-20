package com.financePay.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.financePay.repository.UserBasicRepository;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private UserBasicRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByEmail(username).orElseThrow(
            () -> new UsernameNotFoundException("usuário não encontrado")
        );
    }
    
}