package com.qapital.savings.rule;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertTrue;

public class StandardSavingsRulesServiceTest {
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
                .compareTo(new BigDecimal("33.3333")) == 0);
    }
}