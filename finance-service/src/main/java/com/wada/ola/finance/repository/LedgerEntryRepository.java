package com.wada.ola.finance.repository;

import com.wada.ola.finance.entity.LedgerEntry;
import com.wada.ola.finance.entity.LedgerEntry.EntryType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
    List<LedgerEntry> findByRelatedEntityTypeAndRelatedEntityIdOrderByCreatedAtDesc(
            String relatedEntityType, Long relatedEntityId);
    List<LedgerEntry> findByTypeOrderByCreatedAtDesc(EntryType type);
    List<LedgerEntry> findAllByOrderByCreatedAtDesc();
}
