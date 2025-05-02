package jp.hamutaro.kodukai.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.hamutaro.kodukai.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
}
