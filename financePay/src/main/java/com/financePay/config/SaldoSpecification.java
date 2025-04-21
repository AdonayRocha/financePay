package com.financePay.config;

import org.springframework.data.jpa.domain.Specification;
import com.financePay.model.Saldo;

public class SaldoSpecification {

    public static Specification<Saldo> ativo(Boolean ativo) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("ativo"), ativo);
    }

    public static Specification<Saldo> saldoMin(Double saldoMin) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.greaterThanOrEqualTo(root.get("saldo"), saldoMin);
    }

    public static Specification<Saldo> saldoMax(Double saldoMax) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.lessThanOrEqualTo(root.get("saldo"), saldoMax);
    }
}