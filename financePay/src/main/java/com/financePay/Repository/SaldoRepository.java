package com.financePay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.financePay.model.Saldo;

@Repository
public interface SaldoRepository extends JpaRepository<Saldo, Long>, JpaSpecificationExecutor<Saldo> {

}
