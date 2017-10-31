package com.qapital.savings.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qapital.savings.CustomizedJacksonMapper;
import com.qapital.savings.rule.SavingsRule;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.Assert.assertEquals;




public class SavingsEventTest {

    private final ObjectMapper mapper = CustomizedJacksonMapper.getMapper();

    private final SavingsEvent savingsEvent = new SavingsEvent(1000L,
            1L, 2L, 4L, SavingsRule.RuleType.roundup, SavingsEvent.EventName.rule_application,
            LocalDate.of(2017, 10, 30), new BigDecimal("10.25"), 123L, 111L, false,
            Date.from(LocalDateTime.of(2017, 10, 30, 21, 30, 14).atZone(ZoneId.systemDefault()).toInstant()));



    @Test
    public void shouldSerialize() throws Exception {

        String jsonStr = "{\n" +
                "  \"id\" : 1000,\n" +
                "  \"userId\" : 1,\n" +
                "  \"savingsGoalId\" : 2,\n" +
                "  \"savingsRuleId\" : 4,\n" +
                "  \"ruleType\" : \"roundup\",\n" +
                "  \"eventName\" : \"rule_application\",\n" +
                "  \"date\" : \"2017-10-30\",\n" +
                "  \"amount\" : 10.25,\n" +
                "  \"triggerId\" : 123,\n" +
                "  \"savingsTransferId\" : 111,\n" +
                "  \"cancelled\" : false,\n" +
                "  \"created\" : \"2017-10-30T20:30:14Z\"\n" +
                "}";
        assertEquals(jsonStr, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(savingsEvent));
    }

    @Test
    public void shouldDeserialize() throws Exception {
        String jsonStr = "{\n" +
                "  \"id\" : 1000,\n" +
                "  \"userId\" : 1,\n" +
                "  \"savingsGoalId\" : 2,\n" +
                "  \"savingsRuleId\" : 4,\n" +
                "  \"ruleType\" : \"roundup\",\n" +
                "  \"eventName\" : \"rule_application\",\n" +
                "  \"date\" : \"2017-10-30\",\n" +
                "  \"amount\" : 10.25,\n" +
                "  \"triggerId\" : 123,\n" +
                "  \"savingsTransferId\" : 12342,\n" +
                "  \"cancelled\" : false,\n" +
                "  \"created\" : \"2017-10-30T20:30:14Z\"\n" +
                "}\n";

        SavingsEvent fromJson = mapper.readValue(jsonStr, SavingsEvent.class);
        assertEquals(savingsEvent.withSavingsTransferId(12342L), fromJson);
    }
}