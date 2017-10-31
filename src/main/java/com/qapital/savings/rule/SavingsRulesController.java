package com.qapital.savings.rule;

import com.qapital.savings.event.SavingsEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/api/savings/rule",
        produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
public class SavingsRulesController {

    private final SavingsRulesService savingsRulesService;

    @Autowired
    public SavingsRulesController(SavingsRulesService savingsRulesService) {
        this.savingsRulesService = savingsRulesService;
    }

    @RequestMapping(value = "/active/{userId}", method = GET)
    public List<SavingsRule> activeRulesForUser(@PathVariable Long userId) {
        return savingsRulesService.activeRulesForUser(userId);
    }

    @RequestMapping(value = "/execute", method = POST)
    public List<SavingsEvent> executeRule(@RequestBody @Valid SavingsRule savingsRule) {
        return savingsRulesService.executeRule(savingsRule);
    }

}
