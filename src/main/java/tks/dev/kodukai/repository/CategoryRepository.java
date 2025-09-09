package tks.dev.kodukai.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tks.dev.kodukai.entity.Category;

public interface CategoryRepository  extends JpaRepository<Category, Long> {

}
