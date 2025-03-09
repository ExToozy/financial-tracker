package ru.extoozy.service.transaction.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
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
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.repository.budget.BudgetRepository;
import ru.extoozy.repository.transaction.TransactionRepository;
import ru.extoozy.service.email.EmailService;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Создание транзакции, когда бюджет существует")
    void create_whenBudgetExists_thenSaveTransactionAndUpdateBudget() {
        CreateTransactionDto dto = new CreateTransactionDto();
        dto.setAmount(BigDecimal.valueOf(100));
        dto.setTransactionType(TransactionType.WITHDRAWAL);

        UserProfileEntity userProfile = new UserProfileEntity();
        userProfile.setId(1L);

        try (MockedStatic<UserContext> mocked = mockStatic(UserContext.class)) {
            mocked.when(UserContext::getUser).thenReturn(UserEntity.builder().userProfile(userProfile).build());

            BudgetEntity budget = new BudgetEntity();
            budget.setMaxAmount(BigDecimal.valueOf(500));
            budget.setCurrentAmount(BigDecimal.ZERO);
            when(budgetRepository.findByUserProfileIdAndCurrentMonth(userProfile.getId())).thenReturn(budget);

            transactionService.create(dto);

            verify(transactionRepository).save(any(TransactionEntity.class));
            verify(budgetRepository).update(budget);
            verify(emailService, never()).sendEmail(any(), any());
        }
    }

    @Test
    @DisplayName("Создание транзакции, когда транзакция превышает бюджет")
    void create_whenTransactionExceedsBudget_thenSendEmail() {
        CreateTransactionDto dto = new CreateTransactionDto();
        dto.setAmount(BigDecimal.valueOf(600));
        dto.setTransactionType(TransactionType.WITHDRAWAL);

        UserProfileEntity userProfile = new UserProfileEntity();
        userProfile.setId(1L);

        try (MockedStatic<UserContext> mocked = mockStatic(UserContext.class)) {
            mocked.when(UserContext::getUser).thenReturn(UserEntity.builder().userProfile(userProfile).build());

            BudgetEntity budget = new BudgetEntity();
            budget.setMaxAmount(BigDecimal.valueOf(500));
            budget.setCurrentAmount(BigDecimal.valueOf(400));

            when(budgetRepository.findByUserProfileIdAndCurrentMonth(userProfile.getId())).thenReturn(budget);

            transactionService.create(dto);

            verify(emailService).sendEmail(EmailType.OVER_BUDGET, userProfile);
        }
    }

    @Test
    @DisplayName("Создание транзакции, когда бюджет равен максимальному значению")
    void create_whenTransactionEqualsBudget_thenDoNotSendEmail() {
        CreateTransactionDto dto = new CreateTransactionDto();
        dto.setAmount(BigDecimal.valueOf(500));
        dto.setTransactionType(TransactionType.WITHDRAWAL);

        UserProfileEntity userProfile = new UserProfileEntity();
        userProfile.setId(1L);

        try (MockedStatic<UserContext> mocked = mockStatic(UserContext.class)) {
            mocked.when(UserContext::getUser).thenReturn(UserEntity.builder().userProfile(userProfile).build());

            BudgetEntity budget = new BudgetEntity();
            budget.setMaxAmount(BigDecimal.valueOf(500));
            budget.setCurrentAmount(BigDecimal.valueOf(0));

            when(budgetRepository.findByUserProfileIdAndCurrentMonth(userProfile.getId())).thenReturn(budget);

            transactionService.create(dto);

            verify(emailService, never()).sendEmail(any(), any());
        }
    }

    @Test
    @DisplayName("Создание транзакции, когда возникает исключение при получении бюджета")
    void create_whenBudgetNotFound_thenCreateNewBudget() {
        CreateTransactionDto dto = new CreateTransactionDto();
        dto.setAmount(BigDecimal.valueOf(100));
        dto.setTransactionType(TransactionType.WITHDRAWAL);

        UserProfileEntity userProfile = new UserProfileEntity();
        userProfile.setId(1L);

        try (MockedStatic<UserContext> mocked = mockStatic(UserContext.class)) {
            mocked.when(UserContext::getUser).thenReturn(UserEntity.builder().userProfile(userProfile).build());

            when(budgetRepository.findByUserProfileIdAndCurrentMonth(userProfile.getId()))
                    .thenThrow(new ResourceNotFoundException("Budget not found"));

            transactionService.create(dto);

            verify(budgetRepository).save(any(BudgetEntity.class));
        }
    }

    @Test
    @DisplayName("Создание транзакции, когда бюджет не существует")
    void create_whenBudgetDoesNotExist_thenCreateBudgetAndSaveTransaction() {
        CreateTransactionDto dto = new CreateTransactionDto();
        dto.setAmount(BigDecimal.valueOf(100));
        dto.setTransactionType(TransactionType.WITHDRAWAL);

        UserProfileEntity userProfile = new UserProfileEntity();
        userProfile.setId(1L);

        try (MockedStatic<UserContext> mocked = mockStatic(UserContext.class)) {
            mocked.when(UserContext::getUser).thenReturn(UserEntity.builder().userProfile(userProfile).build());

            when(budgetRepository.findByUserProfileIdAndCurrentMonth(userProfile.getId())).thenThrow(new ResourceNotFoundException("Budget not found"));

            transactionService.create(dto);

            verify(transactionRepository).save(any(TransactionEntity.class));
            verify(budgetRepository).save(any(BudgetEntity.class));
        }
    }

    @Test
    @DisplayName("Обновление транзакции")
    void update_whenTransactionExists_thenUpdateTransaction() {
        UpdateTransactionDto dto = new UpdateTransactionDto();
        dto.setId(1L);
        dto.setAmount(BigDecimal.valueOf(150));

        transactionService.update(dto);

        verify(transactionRepository).update(any(TransactionEntity.class));
    }

    @Test
    @DisplayName("Получение транзакции по ID")
    void get_whenTransactionExists_thenReturnTransactionDto() {
        Long transactionId = 1L;
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setId(transactionId);
        transactionEntity.setAmount(BigDecimal.valueOf(100));

        when(transactionRepository.findById(transactionId)).thenReturn(transactionEntity);

        TransactionDto result = transactionService.get(transactionId);

        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(BigDecimal.valueOf(100));
    }

    @Test
    @DisplayName("Получение всех транзакций пользователя по ID профиля")
    void getAllByUserProfileId_whenTransactionsExist_thenReturnTransactionDtos() {
        Long userProfileId = 1L;
        TransactionEntity transaction1 = new TransactionEntity();
        transaction1.setId(1L);
        transaction1.setAmount(BigDecimal.valueOf(100));

        TransactionEntity transaction2 = new TransactionEntity();
        transaction2.setId(2L);
        transaction2.setAmount(BigDecimal.valueOf(200));

        when(transactionRepository.findAllByUserProfileId(userProfileId)).thenReturn(List.of(transaction1, transaction2));

        List<TransactionDto> result = transactionService.getAllByUserProfileId(userProfileId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getAmount()).isEqualTo(BigDecimal.valueOf(100));
        assertThat(result.get(1).getAmount()).isEqualTo(BigDecimal.valueOf(200));
    }

    @Test
    @DisplayName("Удаление транзакции, когда она существует")
    void delete_whenTransactionExists_thenDeleteTransaction() {
        Long transactionId = 1L;

        when(transactionRepository.delete(transactionId)).thenReturn(true);

        transactionService.delete(transactionId);

        verify(transactionRepository).delete(transactionId);
    }

    @Test
    @DisplayName("Удаление транзакции, когда она не существует")
    void delete_whenTransactionDoesNotExist_thenThrowResourceNotFoundException() {
        Long transactionId = 1L;

        when(transactionRepository.delete(transactionId)).thenReturn(false);

        assertThatThrownBy(() -> transactionService.delete(transactionId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Transaction with id=1 not found");
    }
}

