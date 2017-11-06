package com.qapital.savings.rule;

import com.qapital.bankdata.transaction.Transaction;
import com.qapital.bankdata.transaction.TransactionsService;
import com.qapital.savings.event.SavingsEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.validation.constraints.AssertTrue;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.qapital.savings.event.SavingsEvent.EventName.rule_application;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class StandardSavingsRulesServiceTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private TransactionsService transactionsService;

    private SavingsRulesService savingsRulesService;


    @Before
    public void setUp() throws Exception {
        savingsRulesService = new StandardSavingsRulesService(transactionsService);
    }

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

    @Test
    public void executeRuleShouldOnlyApplyToDebitTransaction() throws Exception {
        SavingsRule roundupRule = SavingsRule.createRoundupRule(1L, 2L, new BigDecimal("2.00"), 11L, 22L);

        when(transactionsService.latestTransactionsForUser(eq(roundupRule.getUserId()))).thenReturn(Arrays.asList(
                new Transaction(101L, 2L, new BigDecimal("-30.00"), "Coffee", LocalDate.now()),
                new Transaction(102L, 2L, new BigDecimal("100.00"), "Salary", LocalDate.now())
        ));

        List<SavingsEvent> savingsEvents = savingsRulesService.executeRule(roundupRule);
        savingsEvents.forEach(savingsEvent -> assertTrue(savingsEvent.getTriggerId().equals(101L)));
    }

    @Test
    public void executeRuleShouldReturnEmptyForSavingsRuleWithoutGoals() throws Exception {
        SavingsRule roundupRule = SavingsRule.createRoundupRule(1L, 2L, new BigDecimal("2.00"));
        when(transactionsService.latestTransactionsForUser(eq(roundupRule.getUserId()))).thenReturn(Arrays.asList(
                new Transaction(101L, 2L, new BigDecimal("-30.00"), "Coffee", LocalDate.now())
        ));
        List<SavingsEvent> savingsEvents = savingsRulesService.executeRule(roundupRule);
        assertTrue(savingsEvents.isEmpty());
    }

    @Test
    public void executeRuleShouldReturnEmptyForNullOrNoTransactions() throws Exception {
        SavingsRule roundupRule = SavingsRule.createRoundupRule(1L, 2L, new BigDecimal("2.00"), 11L, 22L, 33L);
        when(transactionsService.latestTransactionsForUser(eq(roundupRule.getUserId()))).thenReturn(null);
        assertTrue(savingsRulesService.executeRule(roundupRule).isEmpty());
        when(transactionsService.latestTransactionsForUser(eq(roundupRule.getUserId()))).thenReturn(new ArrayList<>());
        assertTrue(savingsRulesService.executeRule(roundupRule).isEmpty());
    }

    @Test
    public void executeShouldMatchGuiltyPleasureDescriptionAndProcessCorrectly() throws Exception {
        SavingsRule guiltyPleasureRule = SavingsRule.createGuiltyPleasureRule(1L, 100L, "espresso house", new BigDecimal("10.00"), 11L, 22L, 33L);
        assertTrue(guiltyPleasureRule.getSavingsGoalIds().size() > 1);
        LocalDate transactionDate = LocalDate.now().minusDays(5);
        when(transactionsService.latestTransactionsForUser(eq(guiltyPleasureRule.getUserId()))).thenReturn(Arrays.asList(
                new Transaction(1L, 100L, new BigDecimal("-2000.00"), "Travel to Greece", transactionDate),
                new Transaction(2L, 100L, new BigDecimal("-30.00"), "Espresso House", transactionDate)
        ));
        List<SavingsEvent> savingsEvents = savingsRulesService.executeRule(guiltyPleasureRule);

        assertEquals(guiltyPleasureRule.getSavingsGoalIds().size(), savingsEvents.size());
        guiltyPleasureRule.getSavingsGoalIds().forEach(savingsGoalId ->
                assertTrue(savingsEvents.contains(new SavingsEvent(100L, savingsGoalId, guiltyPleasureRule, rule_application, transactionDate, new BigDecimal("3.34"), 2L)))
        );
    }

    @Test
    public void roundUpRuleShouldNotGenerateEventForZeroRoundupAmount() throws Exception {
        SavingsRule roundUpRule = SavingsRule.createRoundupRule(1L, 100L,  new BigDecimal("5.00"),11L);
        when(transactionsService.latestTransactionsForUser(eq(roundUpRule.getUserId()))).thenReturn(Arrays.asList(
                new Transaction(1L, 100L, new BigDecimal("-15.00"), "Lunch", LocalDate.now())
        ));
        assertTrue(savingsRulesService.executeRule(roundUpRule).isEmpty());
    }

    @Test
    public void executeShouldProcessRoundupRuleCorrectly() throws Exception {
        SavingsRule roundUpRule = SavingsRule.createRoundupRule(1L, 100L,  new BigDecimal("5.00"),11L, 22L);
        assertTrue(roundUpRule.getSavingsGoalIds().size() >= 1);
        LocalDate transactionDate = LocalDate.now().minusDays(5);
        when(transactionsService.latestTransactionsForUser(eq(roundUpRule.getUserId()))).thenReturn(Arrays.asList(
                new Transaction(1L, 100L, new BigDecimal("-95.50"), "Lunch", transactionDate),
                new Transaction(2L, 100L, new BigDecimal("15000.00"), "Salary", transactionDate),
                new Transaction(3L, 100L, new BigDecimal("-30.00"), "Coffee", transactionDate),
                new Transaction(4L, 100L, new BigDecimal("-12.00"), "Banana for scale", transactionDate)
        ));
        List<SavingsEvent> savingsEvents = savingsRulesService.executeRule(roundUpRule);

        assertEquals(4, savingsEvents.size());
        assertTrue(savingsEvents.contains(new SavingsEvent(100L, 11L, roundUpRule, rule_application, transactionDate, new BigDecimal("2.25"), 1L)));
        assertTrue(savingsEvents.contains(new SavingsEvent(100L, 22L, roundUpRule, rule_application, transactionDate, new BigDecimal("2.25"), 1L)));
        assertTrue(savingsEvents.contains(new SavingsEvent(100L, 11L, roundUpRule, rule_application, transactionDate, new BigDecimal("1.50"), 4L)));
        assertTrue(savingsEvents.contains(new SavingsEvent(100L, 22L, roundUpRule, rule_application, transactionDate, new BigDecimal("1.50"), 4L)));
    }
}