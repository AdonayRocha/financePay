package com.financePay.config;

import org.springframework.data.jpa.domain.Specification;

import com.financePay.modela.Saldo;

public class SaldoSpecification {

    public static Specification<Saldo> ativo(Boolean ativo) {
        return (root, query, cb) -> cb.equal(root.get("ativo"), ativo);
    }

    public static Specification<Saldo> saldoMin(Double min) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("saldo"), min);
    }

    public static Specification<Saldo> saldoMax(Double max) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("saldo"), max);
    }

    public static Specification<Saldo> filtoParametrizado(Boolean ativo, Double saldoMin, Double saldoMax) {
        Specification<Saldo> spec = Specification.where(null);

        if (ativo != null) {
            spec = spec.and(ativo(ativo));
        }
        if (saldoMin != null) {
            spec = spec.and(saldoMin(saldoMin));
        }
        if (saldoMax != null) {
            spec = spec.and(saldoMax(saldoMax));
        }

        return spec;
    }
}
