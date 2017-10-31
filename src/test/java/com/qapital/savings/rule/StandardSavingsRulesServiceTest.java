package com.qapital.savings.rule;

import com.qapital.bankdata.transaction.StandardTransactionsService;
import com.qapital.bankdata.transaction.Transaction;
import com.qapital.bankdata.transaction.TransactionsService;
import com.qapital.savings.event.SavingsEvent;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class StandardSavingsRulesServiceTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    TransactionsService transactionsService;

    @Test
    public void shouldRoundUp() throws Exception {
        verifyRoundUp(new BigDecimal("-3.55"), new BigDecimal("2.00"), new BigDecimal("0.45"));
        verifyRoundUp(new BigDecimal("-2.55"), new BigDecimal("2.00"), new BigDecimal("1.45"));
        verifyRoundUp(new BigDecimal("-7.51"), new BigDecimal("1.5"), new BigDecimal("1.49"));
        verifyRoundUp(new BigDecimal("7.51"), new BigDecimal("1.5"), new BigDecimal("1.49"));
        verifyRoundUp(new BigDecimal("-1.33"), new BigDecimal("4.00"), new BigDecimal("2.67"));
    }

    private void verifyRoundUp(BigDecimal transactionAmount, BigDecimal ruleAmount, BigDecimal expected) {
        assertTrue(StandardSavingsRulesService.roundup(transactionAmount, ruleAmount).compareTo(expected) == 0);
    }


    @Test
    public void shouldDivideRoundup() throws Exception {
        assertTrue(StandardSavingsRulesService.divideToGoalIds(new BigDecimal("100.00"), 3)
                .compareTo(new BigDecimal("33.34")) == 0);
    }

    // TODO add more tests:
    // Apply only on negative transactions
    // Separate rules correctly
    // Rounding is correct
    // Apply rules for all goals, and make division correct
    // Guilty pleasure rule matches corectly


    @Test
    public void executeRuleShouldOnlyApplyToDebitTransaction() throws Exception {
        SavingsRule roundupRule = SavingsRule.createRoundupRule(1L, 2L, new BigDecimal("2.00"), 11L, 22L);

        when(transactionsService.latestTransactionsForUser(eq(roundupRule.getUserId()))).thenReturn(Arrays.asList(
                new Transaction(555L, 2L, BigDecimal.ONE.negate(), "test 1", LocalDate.now()),
                new Transaction(444L, 2L, BigDecimal.ONE, "test 2", LocalDate.now())
        ));

        List<SavingsEvent> savingsEvents = new StandardSavingsRulesService(transactionsService).executeRule(roundupRule);
        savingsEvents.forEach(savingsEvent -> assertTrue(savingsEvent.getTriggerId().equals(555L)));
    }
}