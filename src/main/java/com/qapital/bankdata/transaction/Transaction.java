package com.qapital.bankdata.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Transaction {

    private final Long id;
    private final Long userId;
    private final BigDecimal amount;
    private final String description;
    private final LocalDate date;

    public Transaction(Long id, Long userId, BigDecimal amount, String description, LocalDate date) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(description, that.description) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, amount, description, date);
    }

}
