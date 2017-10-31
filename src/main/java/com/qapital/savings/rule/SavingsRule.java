package com.qapital.savings.rule;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * The core configuration object for a Savings Rule.
 */

public class SavingsRule {

	private final Long id;
	@NotNull
    private final Long userId;
    @NotNull
	private final RuleType ruleType;
    private final String placeDescription;
    @NotNull
    private final BigDecimal amount;
    private final Set<Long> savingsGoalIds;
	private final Status status;

    @JsonCreator
	public SavingsRule(@JsonProperty("id") Long id,
                       @JsonProperty("userId")  Long userId,
                       @JsonProperty("ruleType") RuleType ruleType,
                       @JsonProperty("placeDescription") String placeDescription,
                       @JsonProperty("amount") BigDecimal amount,
                       @JsonProperty("savingsGoalIds") Set<Long> savingsGoalIds,
                       @JsonProperty("status") Status status) {
		this.id = id;
		this.userId = userId;
		this.ruleType = ruleType;
		this.placeDescription = placeDescription;
		this.amount = amount;
		this.savingsGoalIds = savingsGoalIds == null ? Collections.EMPTY_SET : Collections.unmodifiableSet(savingsGoalIds);
		this.status = status == null ? Status.active : status;
	}


    public static SavingsRule createGuiltyPleasureRule(Long id, Long userId, String placeDescription, BigDecimal penaltyAmount, Long... savingsGoalIds) {
		return  new SavingsRule(id, userId, RuleType.guiltypleasure, placeDescription, penaltyAmount,  new HashSet<>(Arrays.asList(savingsGoalIds)),  Status.active);
	}
	
	public static SavingsRule createRoundupRule(Long id, Long userId, BigDecimal roundupToNearest, Long... savingsGoalIds) {
		return new SavingsRule(id, userId, RuleType.roundup, null, roundupToNearest, new HashSet<>(Arrays.asList(savingsGoalIds)), Status.active);
	}

	public Long getId() {
		return id;
	}

	public Long getUserId() {
		return userId;
	}

    public String getPlaceDescription() {
        return placeDescription;
    }

    public BigDecimal getAmount() {
		return amount;
	}

	public Set<Long> getSavingsGoalIds() {
		return savingsGoalIds;
	}

	public RuleType getRuleType() {
		return ruleType;
	}

	public Status getStatus() {
		return status;
	}

    /**
     * @param status new Status
     * @return SavingsRule with new Status
     */
	public SavingsRule withStatus(Status status) {
	    Objects.requireNonNull(status);
	    if(status == this.status) {
	        return this;
        }
		return new SavingsRule(id, userId, ruleType, placeDescription, amount, savingsGoalIds, status);
	}

	@JsonIgnore
	public boolean isActive() {
		return Status.active.equals(getStatus());
	}
	
	public enum RuleType {
        guiltypleasure, roundup
	}
	
	public enum Status {
		active, deleted, paused
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SavingsRule that = (SavingsRule) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userId, that.userId) &&
                ruleType == that.ruleType &&
                Objects.equals(placeDescription, that.placeDescription) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(savingsGoalIds, that.savingsGoalIds) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, ruleType, placeDescription, amount, savingsGoalIds, status);
    }

    @Override
    public String toString() {
        return "SavingsRule{" +
                "id=" + id +
                ", userId=" + userId +
                ", ruleType=" + ruleType +
                ", placeDescription='" + placeDescription + '\'' +
                ", amount=" + amount +
                ", savingsGoalIds=" + savingsGoalIds +
                ", status=" + status +
                '}';
    }
}
