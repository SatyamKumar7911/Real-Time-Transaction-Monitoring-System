package com.example.accountservice.repository;
import com.example.accountservice.entity.BankAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface BankAccountRepository extends JpaRepository<BankAccountEntity, Long> {
  Optional<BankAccountEntity> findByAccountNumber(String accountNumber);
  List<BankAccountEntity> findByUserId(Long userId);
}
