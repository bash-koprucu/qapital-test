package com.qapital.savings.rule;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class SavingsRuleTest {

    static final ObjectMapper mapper;
    static {
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mapper.setDateFormat(new ISO8601DateFormat());
        mapper.registerModule(new JSR310Module());
    }

    private final SavingsRule roundUpRule;
    private final SavingsRule guiltyPleasureRule;

    public SavingsRuleTest() {
        roundUpRule = SavingsRule.createRoundupRule(1L, 100L, new BigDecimal("12.20"));
        roundUpRule.addSavingsGoal(12L);
        roundUpRule.addSavingsGoal(2342L);
        guiltyPleasureRule = SavingsRule.createGuiltyPleasureRule(2L, 100L, "Dorsia", new BigDecimal("0.10"));
    }

    @Test
    public void shouldSerializeRoundUpRule() throws Exception {
        String json = "{\n" +
                "  \"id\" : 1,\n" +
                "  \"userId\" : 100,\n" +
                "  \"ruleType\" : \"roundup\",\n" +
                "  \"amount\" : 12.20,\n" +
                "  \"savingsGoalIds\" : [ 12, 2342 ],\n" +
                "  \"status\" : \"active\"\n" +
                "}";

        assertEquals(json, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(roundUpRule));
    }

    @Test
    public void shouldSerializeGuiltyPleasureRule() throws Exception {
        String json = "{\n" +
                "  \"id\" : 2,\n" +
                "  \"userId\" : 100,\n" +
                "  \"ruleType\" : \"guiltypleasure\",\n" +
                "  \"placeDescription\" : \"Dorsia\",\n" +
                "  \"amount\" : 0.10,\n" +
                "  \"savingsGoalIds\" : [ ],\n" +
                "  \"status\" : \"active\"\n" +
                "}";
        assertEquals(json, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(guiltyPleasureRule));
    }

    @Test
    public void shouldDeserializeGuiltyPleasureRule() throws Exception {
        String json = "{\n" +
                "  \"id\" : 2,\n" +
                "  \"userId\" : 100,\n" +
                "  \"ruleType\" : \"guiltypleasure\",\n" +
                "  \"placeDescription\" : \"Dorsia\",\n" +
                "  \"amount\" : 0.10,\n" +
                "  \"savingsGoalIds\" : [ ],\n" +
                "  \"status\" : \"active\"\n" +
                "}";
        assertEquals(guiltyPleasureRule, mapper.readValue(json, SavingsRule.class));
    }

    @Test
    public void shouldDeserializeWithMinimalJson() throws Exception {
        String json = "{\n" +
                "  \"userId\" : 100,\n" +
                "  \"ruleType\" : \"roundup\",\n" +
                "  \"amount\" : 4.00\n" +
                "}";

        SavingsRule ruleFromMini = mapper.readValue(json, SavingsRule.class);
        assertEquals(0, ruleFromMini.getSavingsGoalIds().size());
        assertEquals(SavingsRule.Status.active, ruleFromMini.getStatus());
        assertEquals(SavingsRule.createRoundupRule(null, 100L, new BigDecimal("4.00")), ruleFromMini);
    }

}