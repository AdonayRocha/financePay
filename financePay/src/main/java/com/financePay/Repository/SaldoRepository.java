package com.financePay.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.financePay.Model.Saldo;

@Repository
public interface SaldoRepository extends JpaRepository<Saldo, Long> {

}
