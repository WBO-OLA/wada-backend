package com.wada.ola.finance.repository;

import com.wada.ola.finance.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findBySource(String source);
    List<Income> findByCategory(String category);
    List<Income> findByRecordedBy(String recordedBy);
    List<Income> findByCommunityGroup(String communityGroup);
    List<Income> findByCountry(String country);
}
