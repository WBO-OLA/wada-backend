package com.wada.ola.finance.service;

import com.wada.ola.common.exception.ResourceNotFoundException;
import com.wada.ola.finance.dto.BudgetActivationRequest;
import com.wada.ola.finance.dto.BudgetRequest;
import com.wada.ola.finance.entity.Budget;
import com.wada.ola.finance.entity.Budget.BudgetStatus;
import com.wada.ola.finance.entity.LedgerEntry;
import com.wada.ola.finance.entity.LedgerEntry.EntryType;
import com.wada.ola.finance.repository.BudgetRepository;
import com.wada.ola.finance.repository.LedgerEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public BudgetService(BudgetRepository budgetRepository, LedgerEntryRepository ledgerEntryRepository) {
        this.budgetRepository = budgetRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    public List<Budget> findAll(Integer fiscalYear, Long commandId) {
        if (commandId != null) return budgetRepository.findByCommandId(commandId);
        if (fiscalYear != null) return budgetRepository.findByFiscalYear(fiscalYear);
        return budgetRepository.findAll();
    }

    public Budget findById(Long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));
    }

    @Transactional
    public Budget create(BudgetRequest request) {
        Budget budget = new Budget();
        budget.setName(request.getName());
        budget.setFiscalYear(request.getFiscalYear());
        budget.setTotalAmount(request.getTotalAmount());
        budget.setDepartment(request.getDepartment());
        budget.setDescription(request.getDescription());
        budget.setCreatedBy(request.getCreatedBy());
        budget.setNotes(request.getNotes());
        budget.setCommandId(request.getCommandId());
        Budget saved = budgetRepository.save(budget);
        LedgerEntry entry = new LedgerEntry();
        entry.setType(EntryType.BUDGET_CREATED);
        entry.setAmount(saved.getTotalAmount());
        entry.setDescription("Budget created: " + saved.getName() + " (FY " + saved.getFiscalYear() + ")");
        entry.setRelatedEntityType("Budget");
        entry.setRelatedEntityId(saved.getId());
        entry.setCreatedBy(request.getCreatedBy());
        ledgerEntryRepository.save(entry);
        return saved;
    }

    @Transactional
    public Budget update(Long id, BudgetRequest request) {
        Budget budget = findById(id);
        if (budget.getStatus() != BudgetStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT budgets can be edited");
        }
        budget.setName(request.getName());
        budget.setFiscalYear(request.getFiscalYear());
        budget.setTotalAmount(request.getTotalAmount());
        budget.setDepartment(request.getDepartment());
        budget.setDescription(request.getDescription());
        budget.setNotes(request.getNotes());
        return budgetRepository.save(budget);
    }

    @Transactional
    public Budget activate(Long id, BudgetActivationRequest request) {
        Budget budget = findById(id);
        if (budget.getStatus() != BudgetStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT budgets can be activated");
        }
        budget.setStatus(BudgetStatus.ACTIVE);
        budget.setApprovedBy(request.getApprovedBy());
        budget.setApprovedAt(LocalDateTime.now());
        if (request.getNotes() != null) budget.setNotes(request.getNotes());
        Budget saved = budgetRepository.save(budget);
        LedgerEntry entry = new LedgerEntry();
        entry.setType(EntryType.BUDGET_ACTIVATED);
        entry.setAmount(saved.getTotalAmount());
        entry.setDescription("Budget activated: " + saved.getName());
        entry.setRelatedEntityType("Budget");
        entry.setRelatedEntityId(saved.getId());
        entry.setCreatedBy(request.getApprovedBy());
        ledgerEntryRepository.save(entry);
        return saved;
    }

    @Transactional
    public Budget close(Long id, String closedBy) {
        Budget budget = findById(id);
        if (budget.getStatus() != BudgetStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE budgets can be closed");
        }
        budget.setStatus(BudgetStatus.CLOSED);
        Budget saved = budgetRepository.save(budget);
        LedgerEntry entry = new LedgerEntry();
        entry.setType(EntryType.BUDGET_CLOSED);
        entry.setAmount(saved.getTotalAmount());
        entry.setDescription("Budget closed: " + saved.getName());
        entry.setRelatedEntityType("Budget");
        entry.setRelatedEntityId(saved.getId());
        entry.setCreatedBy(closedBy);
        ledgerEntryRepository.save(entry);
        return saved;
    }

    public void delete(Long id) {
        Budget budget = findById(id);
        if (budget.getStatus() != BudgetStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT budgets can be deleted");
        }
        budgetRepository.deleteById(id);
    }
}
