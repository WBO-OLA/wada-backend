package com.wada.ola.finance.service;

import com.wada.ola.common.exception.ResourceNotFoundException;
import com.wada.ola.finance.dto.ExpenseApprovalRequest;
import com.wada.ola.finance.dto.ExpenseRequest;
import com.wada.ola.finance.entity.Budget;
import com.wada.ola.finance.entity.Expense;
import com.wada.ola.finance.entity.Expense.ExpenseStatus;
import com.wada.ola.finance.entity.LedgerEntry;
import com.wada.ola.finance.entity.LedgerEntry.EntryType;
import com.wada.ola.finance.repository.BudgetRepository;
import com.wada.ola.finance.repository.ExpenseRepository;
import com.wada.ola.finance.repository.LedgerEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public ExpenseService(ExpenseRepository expenseRepository,
                          BudgetRepository budgetRepository,
                          LedgerEntryRepository ledgerEntryRepository) {
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    public List<Expense> findAll(String status) {
        if (status != null && !status.isBlank()) {
            return expenseRepository.findByStatus(ExpenseStatus.valueOf(status.toUpperCase()));
        }
        return expenseRepository.findAll();
    }

    public Expense findById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));
    }

    @Transactional
    public Expense create(ExpenseRequest request) {
        Expense expense = new Expense();
        expense.setTitle(request.getTitle());
        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setSubmittedBy(request.getSubmittedBy());
        expense.setReference(request.getReference());
        expense.setNotes(request.getNotes());
        expense.setCommandId(request.getCommandId());
        if (request.getBudgetId() != null) {
            Budget budget = budgetRepository.findById(request.getBudgetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + request.getBudgetId()));
            expense.setBudget(budget);
        }
        Expense saved = expenseRepository.save(expense);
        LedgerEntry entry = new LedgerEntry();
        entry.setType(EntryType.EXPENSE_SUBMITTED);
        entry.setAmount(saved.getAmount());
        entry.setDescription("Expense submitted: " + saved.getTitle());
        entry.setRelatedEntityType("Expense");
        entry.setRelatedEntityId(saved.getId());
        entry.setCreatedBy(request.getSubmittedBy());
        ledgerEntryRepository.save(entry);
        return saved;
    }

    @Transactional
    public Expense approve(Long id, ExpenseApprovalRequest request) {
        Expense expense = findById(id);
        if (expense.getStatus() != ExpenseStatus.PENDING) {
            throw new IllegalStateException("Only PENDING expenses can be approved");
        }
        expense.setStatus(ExpenseStatus.APPROVED);
        expense.setApprovedBy(request.getApprovedBy());
        expense.setApprovedAt(LocalDateTime.now());
        if (request.getNotes() != null) expense.setNotes(request.getNotes());
        if (expense.getBudget() != null) {
            Budget budget = expense.getBudget();
            budget.setAllocatedAmount(budget.getAllocatedAmount().add(expense.getAmount()));
            budgetRepository.save(budget);
        }
        Expense saved = expenseRepository.save(expense);
        LedgerEntry entry = new LedgerEntry();
        entry.setType(EntryType.EXPENSE_APPROVED);
        entry.setAmount(saved.getAmount());
        entry.setDescription("Expense approved: " + saved.getTitle());
        entry.setRelatedEntityType("Expense");
        entry.setRelatedEntityId(saved.getId());
        entry.setCreatedBy(request.getApprovedBy());
        ledgerEntryRepository.save(entry);
        return saved;
    }

    @Transactional
    public Expense reject(Long id, ExpenseApprovalRequest request) {
        Expense expense = findById(id);
        if (expense.getStatus() != ExpenseStatus.PENDING) {
            throw new IllegalStateException("Only PENDING expenses can be rejected");
        }
        expense.setStatus(ExpenseStatus.REJECTED);
        expense.setApprovedBy(request.getApprovedBy());
        expense.setApprovedAt(LocalDateTime.now());
        expense.setRejectionReason(request.getRejectionReason());
        Expense saved = expenseRepository.save(expense);
        LedgerEntry entry = new LedgerEntry();
        entry.setType(EntryType.EXPENSE_REJECTED);
        entry.setAmount(saved.getAmount());
        entry.setDescription("Expense rejected: " + saved.getTitle());
        entry.setRelatedEntityType("Expense");
        entry.setRelatedEntityId(saved.getId());
        entry.setCreatedBy(request.getApprovedBy());
        ledgerEntryRepository.save(entry);
        return saved;
    }

    @Transactional
    public Expense cancel(Long id) {
        Expense expense = findById(id);
        if (expense.getStatus() != ExpenseStatus.PENDING) {
            throw new IllegalStateException("Only PENDING expenses can be cancelled");
        }
        expense.setStatus(ExpenseStatus.CANCELLED);
        return expenseRepository.save(expense);
    }
}
