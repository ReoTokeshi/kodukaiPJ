package jp.hamutaro.kodukai.form;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import jp.hamutaro.kodukai.entity.Category;
import jp.hamutaro.kodukai.entity.Expense;
import jp.hamutaro.kodukai.entity.PayMethod;
import lombok.Data;

@Data
public class ExpenseForm {
	
	private Long id;
	
	@NotNull(message = "カテゴリーは必須です")
    private Long categoryId;
	
	@NotBlank(message = "支払内容は必須です")
    private String description;
	
	@NotNull(message = "金額は必須です")
	@Min(value = 1, message = "金額は1円以上で入力してください")
    private Integer amount;
	
	@NotNull(message = "日付は必須です")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
	
	@NotNull(message = "支払方法は必須です")
    private Long payMethodId;
    
    public Expense toEntity(Category category, PayMethod payMethod) {
    	
    	Expense expense = new Expense();
        
    	expense.setId(this.getId());
        expense.setDescription(this.getDescription());
        expense.setAmount(this.getAmount());
        expense.setDate(this.getDate());
    	expense.setCategory(category);
        expense.setPayMethod(payMethod);
    	
    	return expense;
    }
    
    public static ExpenseForm fromEntity(Expense expense) {
    	
    	ExpenseForm form = new ExpenseForm();
    	form.setId(expense.getId());
    	form.setCategoryId(expense.getCategory().getId());
        form.setDescription(expense.getDescription());
        form.setAmount(expense.getAmount());
        form.setDate(expense.getDate());
        form.setPayMethodId(expense.getPayMethod().getId());
    	
    	return form;
    }
}
