package com.qapital.savings.rule;

import com.qapital.bankdata.transaction.Transaction;
import com.qapital.bankdata.transaction.TransactionsService;
import com.qapital.savings.event.SavingsEvent;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.qapital.savings.event.SavingsEvent.EventName.rule_application;
import static com.qapital.savings.rule.SavingsRule.RuleType.guiltypleasure;
import static com.qapital.savings.rule.SavingsRule.RuleType.roundup;

@Service
public class StandardSavingsRulesService implements SavingsRulesService {

    private final TransactionsService transactionsService;

    @Autowired
    public StandardSavingsRulesService(TransactionsService transactionsService) {
        this.transactionsService = transactionsService;
    }

    @Override
    public List<SavingsRule> activeRulesForUser(Long userId) {
        SavingsRule guiltyPleasureRule = SavingsRule.createGuiltyPleasureRule(1L, userId, "Starbucks", new BigDecimal("3.00"));
        guiltyPleasureRule.addSavingsGoal(1L);
        guiltyPleasureRule.addSavingsGoal(2L);
        SavingsRule roundupRule = SavingsRule.createRoundupRule(2L, userId, new BigDecimal("2.00"));
        roundupRule.addSavingsGoal(1L);

        return Collections.unmodifiableList(Arrays.asList(guiltyPleasureRule, roundupRule));
    }

    @Override
    public List<SavingsEvent> executeRule(SavingsRule savingsRule) {
        List<Transaction> transactions = transactionsService.latestTransactionsForUser(savingsRule.getUserId());
        if(transactions.isEmpty()) {
            return Collections.emptyList();
        }
        List<SavingsEvent> savingsEvents = new ArrayList<>();
        transactions.stream()
            .filter(transaction -> transaction.getAmount().signum() == -1) // Apply only to expense transactions
            .forEach(transaction -> {
                if(savingsRule.isActive()) {
                    LocalDate eventDate = LocalDate.now();
                    List<Long> savingsGoalIds = savingsRule.getSavingsGoalIds(); // TODO validation?
                    if(roundup == savingsRule.getRuleType()) { // operator == is safe with enum
                        BigDecimal roundUpAmount  = divideToGoalIds(roundup(savingsRule.getAmount(), transaction.getAmount()), savingsGoalIds.size());
                        savingsGoalIds.forEach(goalId -> savingsEvents.add(
                                new SavingsEvent(savingsRule.getUserId(), goalId, savingsRule.getId(),
                                        rule_application, eventDate, roundUpAmount, transaction.getId(), savingsRule)
                        ));
                    } else if (guiltypleasure == savingsRule.getRuleType()) {
                        // TODO validation, case sensitivity of description?
                        if (savingsRule.getPlaceDescription().equalsIgnoreCase(transaction.getDescription())) {
                            BigDecimal roundUpAmount = divideToGoalIds(savingsRule.getAmount(), savingsGoalIds.size());
                            savingsGoalIds.forEach(goalId -> savingsEvents.add(
                                    new SavingsEvent(savingsRule.getUserId(), goalId, savingsRule.getId(),
                                            rule_application, eventDate, roundUpAmount, transaction.getId(), savingsRule))
                            );
                        }
                    }
                }
            });
        return Collections.unmodifiableList(savingsEvents);
    }

    static BigDecimal roundup(BigDecimal transactionAmount, BigDecimal ruleAmount) {
        BigDecimal trAmount = transactionAmount.abs();
        return trAmount
                .divide(ruleAmount, 0, BigDecimal.ROUND_CEILING) // how many "rule amount" is in the transaction
                .multiply(ruleAmount)  // Round up to this
                .subtract(trAmount);   // Amount needed to round up
    }

    static BigDecimal divideToGoalIds(BigDecimal amount, int numberOfGoalIds) {
        if(numberOfGoalIds == 1) return amount;
        return amount.divide(new BigDecimal(numberOfGoalIds), 4,  // TODO we need to decide on scale based on business requirements)
                                  BigDecimal.ROUND_HALF_EVEN);
    }

}
