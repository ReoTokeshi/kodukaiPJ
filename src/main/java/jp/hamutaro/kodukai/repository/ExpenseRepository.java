package jp.hamutaro.kodukai.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import jp.hamutaro.kodukai.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
	Page<Expense> findAll(Pageable pageable);
}
