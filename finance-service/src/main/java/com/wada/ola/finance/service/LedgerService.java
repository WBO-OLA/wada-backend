package com.wada.ola.finance.service;

import com.wada.ola.finance.entity.LedgerEntry;
import com.wada.ola.finance.entity.LedgerEntry.EntryType;
import com.wada.ola.finance.repository.LedgerEntryRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LedgerService {

    private final LedgerEntryRepository repository;

    public LedgerService(LedgerEntryRepository repository) {
        this.repository = repository;
    }

    public List<LedgerEntry> findAll(EntryType type) {
        if (type != null) return repository.findByTypeOrderByCreatedAtDesc(type);
        return repository.findAllByOrderByCreatedAtDesc();
    }

    public List<LedgerEntry> findByEntity(String entityType, Long entityId) {
        return repository.findByRelatedEntityTypeAndRelatedEntityIdOrderByCreatedAtDesc(entityType, entityId);
    }
}
