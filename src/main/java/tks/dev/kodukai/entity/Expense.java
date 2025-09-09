package tks.dev.kodukai.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "expenses")
@Data
public class Expense {
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
    @ManyToOne
    @JoinColumn(name = "category_id")
	private Category category;
	
	private String description;
	
	private Integer amount;
	
	private LocalDate date;
	
    @ManyToOne
    @JoinColumn(name = "pay_method_id")
	private PayMethod payMethod;
}
