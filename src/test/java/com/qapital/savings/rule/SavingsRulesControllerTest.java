package com.qapital.savings.rule;

import com.qapital.savings.event.SavingsEvent;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(SavingsRulesController.class)
@Ignore // SavingsEvent.date is dynamic, which makes t
// TODO implement all tests
public class SavingsRulesControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SavingsRulesService savingsRulesService;





    @Test
    public void executeRule() throws Exception {
        Date created = Date.from(LocalDateTime.of(2017, 10, 30, 21, 30, 14).atZone(ZoneId.systemDefault()).toInstant());

        when(savingsRulesService.executeRule(any(SavingsRule.class))).thenReturn(Arrays.asList(
                new SavingsEvent(1L, 2L,
                        SavingsRule.createGuiltyPleasureRule(10L, 1L, "Dorsia", BigDecimal.ONE),
                        SavingsEvent.EventName.rule_application,
                        LocalDate.of(2017,10, 30), new BigDecimal("15.40"), 111L, created),
                new SavingsEvent(1L, 3L,
                        SavingsRule.createRoundupRule(11L, 1L, new BigDecimal("2.00")),
                        SavingsEvent.EventName.rule_application,
                        LocalDate.of(2017,10, 30), new BigDecimal("4.60"), 111L, created)
                ));

        mvc.perform(post("/api/savings/rule/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"userId\" : 100,\n" +
                        "  \"ruleType\" : \"roundup\",\n" +
                        "  \"amount\" : 2.00,\n" +
                        "  \"savingsGoalIds\" : [1]\n" +
                        "}\n"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.content().json("[{\"userId\":1,\"savingsGoalId\":2,\"savingsRuleId\":10,\"ruleType\":\"guiltypleasure\",\"eventName\":\"rule_application\",\"date\":\"2017-10-30\",\"amount\":15.40,\"triggerId\":111,\"cancelled\":false,\"created\":\"2017-10-30T20:30:14Z\"},{\"userId\":1,\"savingsGoalId\":3,\"savingsRuleId\":11,\"ruleType\":\"roundup\",\"eventName\":\"rule_application\",\"date\":\"2017-10-30\",\"amount\":4.60,\"triggerId\":111,\"cancelled\":false,\"created\":\"2017-10-30T20:30:14Z\"}]"));

    }

}