package jp.hamutaro.kodukai.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.hamutaro.kodukai.entity.Category;

public interface CategoryRepository  extends JpaRepository<Category, Long> {

}
