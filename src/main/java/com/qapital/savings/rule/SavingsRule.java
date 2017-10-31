package com.qapital.savings.rule;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The core configuration object for a Savings Rule.
 */
public class SavingsRule {
	
	private final Long id;
	private final Long userId;

    private final RuleType ruleType;
    private final String placeDescription;
    private final BigDecimal amount;
    private final List<Long> savingsGoalIds;
	private final Status status;

    @JsonCreator
	public SavingsRule(@JsonProperty("id") Long id,
                       @JsonProperty("userId") @NotNull Long userId,
                       @JsonProperty("ruleType") @NotNull RuleType ruleType,
                       @JsonProperty("placeDescription") String placeDescription,
                       @JsonProperty("amount") @NotNull BigDecimal amount,
                       @JsonProperty("savingsGoalIds") List<Long> savingsGoalIds,
                       @JsonProperty("status") Status status) {
		this.id = id;
		this.userId = userId;
		this.ruleType = ruleType;
		this.placeDescription = placeDescription;
		this.amount = amount;
		this.savingsGoalIds = savingsGoalIds == null ? new ArrayList<>() : savingsGoalIds;
		this.status = status == null ? Status.active : status;
	}


    public static SavingsRule createGuiltyPleasureRule(Long id, Long userId, String placeDescription, BigDecimal penaltyAmount) {
		return  new SavingsRule(id, userId, RuleType.guiltypleasure, placeDescription, penaltyAmount, new ArrayList<>(), Status.active);
	}
	
	public static SavingsRule createRoundupRule(Long id, Long userId, BigDecimal roundupToNearest) {
		return new SavingsRule(id, userId, RuleType.roundup, null, roundupToNearest, new ArrayList<>(), Status.active);
	}


	// TODO this is not thread safe
	public void addSavingsGoal(Long savingsGoalId) {
		if (!savingsGoalIds.contains(savingsGoalId)) {
			savingsGoalIds.add(savingsGoalId);
		}
	}

	public void removeSavingsGoal(Long savingsGoalId) {
		savingsGoalIds.remove(savingsGoalId);
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

	public List<Long> getSavingsGoalIds() {
		return Collections.unmodifiableList(savingsGoalIds);
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
    // TODO AtomicReference could also work
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
