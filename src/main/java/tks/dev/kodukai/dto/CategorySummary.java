package tks.dev.kodukai.dto;

import lombok.Data;

@Data
public class CategorySummary {
	private String category;
	private Long total;
	
	public CategorySummary(String category, Long total) {
		this.category = category;
		this.total = total;
	}
}
