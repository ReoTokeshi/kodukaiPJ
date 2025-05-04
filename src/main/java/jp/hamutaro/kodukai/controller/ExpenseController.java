package jp.hamutaro.kodukai.controller;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.hamutaro.kodukai.dto.CategorySummary;
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
    public String listExpenses(@RequestParam(defaultValue = "date_desc") String sort,
    		                   @RequestParam(required = false) Integer year,
    		                   @RequestParam(required = false) Integer month,
    		                   @RequestParam(defaultValue = "0") int page,
    		                   Model model) {
    	
    	Page<Expense> expensesPage;
    	Sort sorting;
    	int pageSize = 10;
    	Integer monthlyTotal = 0;
        // 現在の年月
        LocalDate now = LocalDate.now();
    	
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
    	
    	PageRequest paging = PageRequest.of(page, pageSize, sorting);
    	
		if(year == null) {
			year = now.getYear();
		}
		if(month == null) {
			month = now.getMonthValue();
		}
    	
    	LocalDate start = LocalDate.of(year, month, 1);
    	LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
    	monthlyTotal = expenseRepository.findTotalAmountByMonth(start, end);
    	expensesPage = expenseRepository.findByDateBetween(start, end, paging);
    	
    	model.addAttribute("expensesPage", expensesPage);
    	model.addAttribute("monthlyTotal", monthlyTotal);
    	model.addAttribute("sort", sort);
    	model.addAttribute("year", year);
    	model.addAttribute("month", month);
    	
    	//プルダウン要素取得：年月指定
    	int currentYear = now.getYear();
    	List<Integer> years = IntStream.rangeClosed(currentYear - 4, currentYear)
    			                       .boxed()
    			                       .sorted(Comparator.reverseOrder())
    			                       .toList();
    	List<Integer> months = IntStream.rangeClosed(1, 12)
                                       .boxed()
                                       .toList();
    	model.addAttribute("years", years);
    	model.addAttribute("months", months);
    	
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
    
    //未使用
    @GetMapping("/expenses/summary")
    public String showSummary(
    		@RequestParam(defaultValue = "2025") int year,
    		@RequestParam(defaultValue = "4") int month,
    		Model model) {
    	
    	List<Object[]> rawData = expenseRepository.findCategoryTotalsByYearAndMonth(year, month);
    	
    	List<CategorySummary> summaryList = rawData.stream()
    			                                   .map(row -> new CategorySummary((String) row[0], ((Number) row[1]).longValue()))
    			                                   .toList();
    	
    	long monthlyTotal = summaryList.stream()
                                      .mapToLong(CategorySummary::getTotal)
                                      .sum();
    	
    	model.addAttribute("summaryList", summaryList);
    	model.addAttribute("year", year);
    	model.addAttribute("month", month);
    	model.addAttribute("monthlyTotal", monthlyTotal);
    	
    	return "expense_summary";
    }
}