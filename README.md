
### Solution by Ba?ar K?pr?c?


### Changes

    - Used BigDecimal instead of Double for the amounts. We have to decide on the scale, and rounding strategy

    - Value objects are made immutable, though SavingsRule.addSavingsGoal() and SavingsRule.removeSavingsGoal()
      still needs work. We can talk about different solutions.

    - Uncertain points, and some ideas marked with "// TODO" comments

    - Joda time replaced with Java 8 time objects, Spring-boot version updated


### Missing

    - Test coverage

    - Input validation

    - Exception handling

    - Logging

