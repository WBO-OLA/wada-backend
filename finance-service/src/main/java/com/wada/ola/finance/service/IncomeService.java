package com.wada.ola.finance.service;

import com.wada.ola.common.exception.ResourceNotFoundException;
import com.wada.ola.finance.dto.IncomeRequest;
import com.wada.ola.finance.entity.Income;
import com.wada.ola.finance.entity.LedgerEntry;
import com.wada.ola.finance.entity.LedgerEntry.EntryType;
import com.wada.ola.finance.repository.IncomeRepository;
import com.wada.ola.finance.repository.LedgerEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public IncomeService(IncomeRepository incomeRepository, LedgerEntryRepository ledgerEntryRepository) {
        this.incomeRepository = incomeRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    public List<Income> findAll() {
        return incomeRepository.findAll();
    }

    public Income findById(Long id) {
        return incomeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income not found with id: " + id));
    }

    @Transactional
    public Income create(IncomeRequest request) {
        Income income = new Income();
        income.setTitle(request.getTitle());
        income.setAmount(request.getAmount());
        income.setSource(request.getSource());
        income.setCategory(request.getCategory());
        income.setReceivedDate(request.getReceivedDate());
        income.setReference(request.getReference());
        income.setRecordedBy(request.getRecordedBy());
        income.setNotes(request.getNotes());
        Income saved = incomeRepository.save(income);
        LedgerEntry entry = new LedgerEntry();
        entry.setType(EntryType.INCOME_RECEIVED);
        entry.setAmount(saved.getAmount());
        entry.setDescription("Income received: " + saved.getTitle());
        entry.setRelatedEntityType("Income");
        entry.setRelatedEntityId(saved.getId());
        entry.setCreatedBy(request.getRecordedBy());
        ledgerEntryRepository.save(entry);
        return saved;
    }

    public void delete(Long id) {
        if (!incomeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Income not found with id: " + id);
        }
        incomeRepository.deleteById(id);
    }
}
