package jp.hamutaro.kodukai.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.hamutaro.kodukai.entity.PayMethod;

public interface PayMethodRepository  extends JpaRepository<PayMethod, Long> {

}
