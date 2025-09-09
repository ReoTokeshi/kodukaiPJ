package tks.dev.kodukai.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tks.dev.kodukai.entity.PayMethod;

public interface PayMethodRepository  extends JpaRepository<PayMethod, Long> {

}
