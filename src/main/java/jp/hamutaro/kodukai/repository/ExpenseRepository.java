package jp.hamutaro.kodukai.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import jp.hamutaro.kodukai.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
	
	Page<Expense> findAll(Pageable pageable);
	
	@Query(value = """
			SELECT category, SUM(amount) AS total
			FROM expenses
			WHERE YEAR(date) = :year AND MONTH(date) = :month
			GROUP BY category
			""", nativeQuery = true)
	List<Object[]> findCategoryTotalsByYearAndMonth(int year, int month);
	
}
