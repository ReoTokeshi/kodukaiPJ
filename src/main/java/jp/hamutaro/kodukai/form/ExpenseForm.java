package jp.hamutaro.kodukai.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jp.hamutaro.kodukai.entity.Expense;
import lombok.Data;

@Data
public class ExpenseForm {
	private Long id;
    private String category;
    private String description;
    private int amount;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private String payMethod;
    
    public Expense toEntity() {
    	
    	Expense expense = new Expense();
    	expense.setId(this.getId());
    	expense.setCategory(this.getCategory());
        expense.setDescription(this.getDescription());
        expense.setAmount(this.getAmount());
        expense.setDate(this.getDate());
        expense.setPayMethod(this.getPayMethod());
    	
    	return expense;
    }
    
    public static ExpenseForm fromEntity(Expense expense) {
    	
    	ExpenseForm form = new ExpenseForm();
    	form.setId(expense.getId());
    	form.setCategory(expense.getCategory());
        form.setDescription(expense.getDescription());
        form.setAmount(expense.getAmount());
        form.setDate(expense.getDate());
        form.setPayMethod(expense.getPayMethod());
    	
    	return form;
    }
}
