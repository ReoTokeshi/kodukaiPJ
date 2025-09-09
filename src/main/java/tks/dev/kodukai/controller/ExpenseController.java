package tks.dev.kodukai.controller;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tks.dev.kodukai.entity.Category;
import tks.dev.kodukai.entity.Expense;
import tks.dev.kodukai.entity.PayMethod;
import tks.dev.kodukai.form.ExpenseForm;
import tks.dev.kodukai.repository.CategoryRepository;
import tks.dev.kodukai.repository.ExpenseRepository;
import tks.dev.kodukai.repository.PayMethodRepository;

@Controller
public class ExpenseController {
	
	@Autowired
	private ExpenseRepository expenseRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private PayMethodRepository payMethodRepository;
	
	@GetMapping("/expenses/new")
	public String showForm(Model model) {
		
    	List<Category> categories = categoryRepository.findAll(Sort.by("id").ascending());
    	List<PayMethod> payMethods = payMethodRepository.findAll(Sort.by("id").ascending());

		model.addAttribute("expenseForm", new ExpenseForm());
    	model.addAttribute("categories", categories);
    	model.addAttribute("payMethods", payMethods);
		
		return "expense_form";
	}
	
    @PostMapping("/expenses")
    public String submitExpense(@Valid @ModelAttribute ExpenseForm form,
    		                    BindingResult result,
    		                    Model model,
    		                    RedirectAttributes redirectAttributes) {
        
    	if(result.hasErrors()) {
        	List<Category> categories = categoryRepository.findAll(Sort.by("id").ascending());
        	List<PayMethod> payMethods = payMethodRepository.findAll(Sort.by("id").ascending());
        	
        	model.addAttribute("categories", categories);
        	model.addAttribute("payMethods", payMethods);
        	
    		return "expense_form";
    	}
        Category category = categoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("無効なカテゴリーIDです"));
        PayMethod payMethod = payMethodRepository.findById(form.getPayMethodId())
                .orElseThrow(() -> new IllegalArgumentException("無効な支払方法IDです"));
    	
    	Expense expense = form.toEntity(category, payMethod);
    	
        System.out.println("【支出登録フォームの入力内容】");
        System.out.println("カテゴリー: " + form.getCategoryId());
        System.out.println("内容: " + form.getDescription());
        System.out.println("金額: " + form.getAmount());
        System.out.println("日付: " + form.getDate());
        System.out.println("支払方法: " + form.getPayMethodId());
        
        expenseRepository.save(expense);
        redirectAttributes.addFlashAttribute("successMessage", "登録が完了しました。");
    	
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
    public String deleteExpense(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    	
    	expenseRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "削除が完了しました。");
        
    	return "redirect:/expenses";
    }
    
    @GetMapping("/expenses/{id}/edit")
    public String editExpense(@PathVariable Long id, Model model) {
    	Expense expense = expenseRepository.findById(id).orElseThrow();
    	List<Category> categories = categoryRepository.findAll(Sort.by("id").ascending());
    	List<PayMethod> payMethods = payMethodRepository.findAll(Sort.by("id").ascending());
    	
    	model.addAttribute("expenseForm", ExpenseForm.fromEntity(expense));
    	model.addAttribute("categories", categories);
    	model.addAttribute("payMethods", payMethods);
    	
    	return "expense_form";
    }

    @PostMapping("/expenses/{id}/update")
    public String updateExpense(@PathVariable Long id, @Valid @ModelAttribute ExpenseForm form,
    		                    BindingResult result, Model model,
    		                    RedirectAttributes redirectAttributes) {
    	
    	if(result.hasErrors()) {
        	List<Category> categories = categoryRepository.findAll(Sort.by("id").ascending());
        	List<PayMethod> payMethods = payMethodRepository.findAll(Sort.by("id").ascending());
        	
        	model.addAttribute("categories", categories);
        	model.addAttribute("payMethods", payMethods);
        	
    		return "expense_form";
    	}
    	
        Category category = categoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("無効なカテゴリーIDです"));
        PayMethod payMethod = payMethodRepository.findById(form.getPayMethodId())
                .orElseThrow(() -> new IllegalArgumentException("無効な支払方法IDです"));
    	
    	Expense expense = form.toEntity(category, payMethod);
    	
    	expense.setId(id);
        expenseRepository.save(expense);
        redirectAttributes.addFlashAttribute("successMessage", "編集が完了しました。");
    	
    	return "redirect:/expenses";
    }
    
    //未使用
    //@GetMapping("/expenses/summary")
    //public String showSummary(
    //		@RequestParam(defaultValue = "2025") int year,
    //		@RequestParam(defaultValue = "4") int month,
    //		Model model) {
    //	
    //	List<Object[]> rawData = expenseRepository.findCategoryTotalsByYearAndMonth(year, month);
    //	
    //	List<CategorySummary> summaryList = rawData.stream()
    //			                                   .map(row -> new CategorySummary((String) row[0], ((Number) row[1]).longValue()))
    //			                                   .toList();
    //	
    //	long monthlyTotal = summaryList.stream()
    //                                  .mapToLong(CategorySummary::getTotal)
    //                                  .sum();
    //	
    //	model.addAttribute("summaryList", summaryList);
    //	model.addAttribute("year", year);
    //	model.addAttribute("month", month);
    //	model.addAttribute("monthlyTotal", monthlyTotal);
    //	
    //	return "expense_summary";
    //}
}