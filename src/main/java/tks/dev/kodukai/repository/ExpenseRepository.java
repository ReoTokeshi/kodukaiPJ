package tks.dev.kodukai.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tks.dev.kodukai.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
	
	Page<Expense> findAll(Pageable pageable);
	Page<Expense> findByDateBetween(LocalDate start, LocalDate end, Pageable pageable);
	
	@Query(value = """
			SELECT category, SUM(amount) AS total
			FROM expenses
			WHERE YEAR(date) = :year AND MONTH(date) = :month
			GROUP BY category
			""", nativeQuery = true)
	List<Object[]> findCategoryTotalsByYearAndMonth(int year, int month);
	
	@Query(value = """
			SELECT COALESCE(SUM(amount), 0)
			FROM expenses
			""", nativeQuery = true)
	Integer findTotalAmount();
	
	@Query(value = """
			SELECT COALESCE(SUM(amount), 0)
			FROM expenses
			WHERE date between :start AND :end
			""", nativeQuery = true)
	Integer findTotalAmountByMonth(LocalDate start, LocalDate end);
}
