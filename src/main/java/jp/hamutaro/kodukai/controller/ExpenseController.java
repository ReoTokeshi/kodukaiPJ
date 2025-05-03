package jp.hamutaro.kodukai.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.hamutaro.kodukai.entity.Expense;
import jp.hamutaro.kodukai.form.ExpenseForm;
import jp.hamutaro.kodukai.repository.ExpenseRepository;

@Controller
public class ExpenseController {
	
	@Autowired
	private ExpenseRepository expenseRepository;
	
	@GetMapping("/expenses/new")
	public String showForm(Model model) {
		model.addAttribute("expenseForm", new ExpenseForm());
		return "expense_form";
	}
	
    @PostMapping("/expenses")
    public String submitExpense(@Valid @ModelAttribute ExpenseForm form,
    		                    BindingResult result,
    		                    Model model) {
        
    	if(result.hasErrors()) {
    		return "expense_form";
    	}
    	
    	Expense expense = form.toEntity();
    	
        System.out.println("【支出登録フォームの入力内容】");
        System.out.println("カテゴリー: " + form.getCategory());
        System.out.println("内容: " + form.getDescription());
        System.out.println("金額: " + form.getAmount());
        System.out.println("日付: " + form.getDate());
        System.out.println("支払方法: " + form.getPayMethod());
        
        expenseRepository.save(expense);
    	
        return "redirect:/expenses";
    }
    
    @GetMapping("/expenses")
    public String listExpenses(@RequestParam(defaultValue = "date_desc") String sort, Model model) {
    	Sort sorting;
    	
    	switch(sort) {
    	case "date_asc":
    		sorting = Sort.by(Sort.Direction.ASC, "date");
    		break;
    	case "amount_desc":
    		sorting = Sort.by(Sort.Direction.DESC, "amount");
    		break;
    	case "amount_asc":
    		sorting = Sort.by(Sort.Direction.ASC, "amount");
    		break;
    	default:
    		sorting = Sort.by(Sort.Direction.DESC, "date");
    	}
    	
    	List<Expense> expenses = expenseRepository.findAll(sorting);
    	
    	int totalAmount = expenses.stream()
    			                  .mapToInt(Expense::getAmount)
    			                  .sum();
    	
    	model.addAttribute("expenses", expenses);
    	model.addAttribute("totalAmount", totalAmount);
    	model.addAttribute("sort", sort);
    	
    	return "expense_list";
    }
    
    @PostMapping("/expenses/{id}/delete")
    public String deleteExpense(@PathVariable Long id) {
    	expenseRepository.deleteById(id);
    	return "redirect:/expenses";
    }
    
    @GetMapping("/expenses/{id}/edit")
    public String editExpense(@PathVariable Long id, Model model) {
    	Expense expense = expenseRepository.findById(id).orElseThrow();
    	model.addAttribute("expenseForm", ExpenseForm.fromEntity(expense));
    	
    	return "expense_form";
    }

    @PostMapping("/expenses/{id}/update")
    public String updateExpense(@PathVariable Long id, @Valid @ModelAttribute ExpenseForm form,
    		                    BindingResult result, Model model) {
    	
    	if(result.hasErrors()) {
    		return "expense_form";
    	}
    	
    	Expense expense = form.toEntity();
    	expense.setId(id);
        expenseRepository.save(expense);
    	
    	return "redirect:/expenses";
    }
    
}