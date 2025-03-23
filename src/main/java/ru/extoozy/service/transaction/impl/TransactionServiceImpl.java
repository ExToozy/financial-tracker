package ru.extoozy.service.transaction.impl;

import lombok.RequiredArgsConstructor;
import ru.extoozy.context.UserContext;
import ru.extoozy.dto.transaction.CreateTransactionDto;
import ru.extoozy.dto.transaction.TransactionDto;
import ru.extoozy.dto.transaction.UpdateTransactionDto;
import ru.extoozy.entity.BudgetEntity;
import ru.extoozy.entity.TransactionEntity;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.enums.EmailType;
import ru.extoozy.enums.TransactionType;
import ru.extoozy.enums.UserRole;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.mapper.TransactionMapper;
import ru.extoozy.repository.budget.BudgetRepository;
import ru.extoozy.repository.transaction.TransactionRepository;
import ru.extoozy.service.email.EmailService;
import ru.extoozy.service.transaction.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final BudgetRepository budgetRepository;

    private final EmailService emailService;

    private boolean userDoesNotHaveAccessToTransaction(UserEntity user, TransactionEntity transaction) {
        return !user.getRole().equals(UserRole.ADMIN) &&
                !user.getUserProfile().getId().equals(transaction.getUserProfile().getId());
    }

    private boolean userDoesNotHaveAccessToUserProfile(UserEntity user, Long userProfileId) {
        return !user.getRole().equals(UserRole.ADMIN) &&
                !user.getUserProfile().getId().equals(userProfileId);
    }

    @Override
    public void create(CreateTransactionDto dto) {
        TransactionEntity transaction = TransactionMapper.INSTANCE.toEntity(dto);
        UserProfileEntity userProfile = UserContext.getUser().getUserProfile();
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUserProfile(userProfile);
        transactionRepository.save(transaction);

        BudgetEntity userBudget;
        try {
            userBudget = budgetRepository.findByUserProfileIdAndCurrentMonth(userProfile.getId());
        } catch (ResourceNotFoundException e) {
            userBudget = BudgetEntity.builder()
                    .maxAmount(BigDecimal.ZERO)
                    .currentAmount(BigDecimal.ZERO)
                    .period(YearMonth.now())
                    .userProfile(userProfile)
                    .build();
            budgetRepository.save(userBudget);
        }

        updateBudget(transaction, userBudget);
        if (!userBudget.getMaxAmount().equals(BigDecimal.ZERO)) {
            sendEmailIfBudgetExceed(userBudget, userProfile);
        }
    }

    @Override
    public void update(UpdateTransactionDto dto) {
        TransactionEntity transaction = transactionRepository.findById(dto.getId());
        if (userDoesNotHaveAccessToTransaction(UserContext.getUser(), transaction)) {
            throw new ResourceNotFoundException(
                    "The user with id=%s does not have access to transaction with id=%s".formatted(
                            UserContext.getUser().getId(),
                            transaction.getId()
                    )
            );
        }
        TransactionEntity entity = TransactionMapper.INSTANCE.toEntity(dto);
        transactionRepository.update(entity);
    }

    @Override
    public TransactionDto get(Long id) {
        TransactionEntity transaction = transactionRepository.findById(id);
        if (userDoesNotHaveAccessToTransaction(UserContext.getUser(), transaction)) {
            throw new ResourceNotFoundException(
                    "The user with id=%s does not have access to transaction with id=%s".formatted(
                            UserContext.getUser().getId(),
                            transaction.getId()
                    )
            );
        }
        return TransactionMapper.INSTANCE.toDto(transaction);
    }

    @Override
    public List<TransactionDto> getAllByUserProfileId(Long userProfileId) {
        if (userDoesNotHaveAccessToUserProfile(UserContext.getUser(), userProfileId)) {
            throw new ResourceNotFoundException(
                    "The user with id=%s does not have access to user profile with id=%s".formatted(
                            UserContext.getUser().getId(),
                            userProfileId
                    )
            );
        }
        List<TransactionEntity> userTransactions = transactionRepository.findAllByUserProfileId(userProfileId);
        return TransactionMapper.INSTANCE.toDto(userTransactions);
    }

    @Override
    public void delete(Long id) {
        TransactionEntity transaction = transactionRepository.findById(id);
        if (userDoesNotHaveAccessToTransaction(UserContext.getUser(), transaction)) {
            throw new ResourceNotFoundException(
                    "The user with id=%s does not have access to transaction with id=%s".formatted(
                            UserContext.getUser().getId(),
                            transaction.getId()
                    )
            );
        }
        boolean deleted = transactionRepository.delete(id);
        if (!deleted) {
            throw new ResourceNotFoundException("Transaction with id=%s not found".formatted(id));
        }
    }

    private void updateBudget(TransactionEntity transaction, BudgetEntity userBudget) {
        if (transaction.getTransactionType() == TransactionType.WITHDRAWAL) {
            userBudget.setCurrentAmount(userBudget.getCurrentAmount().add(transaction.getAmount()));
            budgetRepository.update(userBudget);
        }
    }

    private void sendEmailIfBudgetExceed(BudgetEntity userBudget, UserProfileEntity userProfile) {
        if (userBudget.getCurrentAmount().compareTo(userBudget.getMaxAmount()) > 0) {
            emailService.sendEmail(EmailType.OVER_BUDGET, userProfile);
        }
    }
}
