package com.qapital.savings.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qapital.savings.rule.SavingsRule;
import com.qapital.savings.rule.SavingsRule.RuleType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

/**
 * A Savings Event represents an event in the history of a Savings Goal.
 * Events can be either monetary (triggered by the application of Savings Rules,
 * manual transfers, interest payments or incentive payouts), or other events
 * of significance in the history of the goal, such as pausing or unpausing
 * Savings Rules or other users joining or leaving a shared goal.
 */

//TODO clarify how id, savingsTransferId and cancelled will be used during the lifecycle of the object.

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SavingsEvent {
	
	private final Long id;
	private final Long userId;
	private final Long savingsGoalId;
	private final Long savingsRuleId;
	private final EventName eventName;

	private final LocalDate date;
	private final BigDecimal amount;
	private final Long triggerId;
	private final RuleType ruleType;
	private final Long savingsTransferId;
	private final Boolean cancelled;
	private final Date created;

    @JsonCreator
    SavingsEvent(@JsonProperty("id") Long id,
                 @JsonProperty("userId") Long userId,
                 @JsonProperty("savingsGoalId") Long savingsGoalId,
                 @JsonProperty("savingsRuleId") Long savingsRuleId,
                 @JsonProperty("ruleType") RuleType ruleType,
                 @JsonProperty("eventName") EventName eventName,
                 @JsonProperty("date") LocalDate date,
                 @JsonProperty("amount") BigDecimal amount,
                 @JsonProperty("triggerId") Long triggerId,
                 @JsonProperty("savingsTransferId") Long savingsTransferId,
                 @JsonProperty("cancelled") Boolean cancelled,
                 @JsonProperty("created") Date created) {
		this.id = id;
		this.userId = userId;
		this.savingsGoalId = savingsGoalId;
		this.savingsRuleId = savingsRuleId;
		this.eventName = eventName;
		this.date = date;
		this.amount = amount == null ? BigDecimal.ZERO : amount;
		this.triggerId = triggerId;
		this.savingsTransferId = savingsTransferId;
		this.ruleType = ruleType;
		this.cancelled = cancelled == null ? false : cancelled;
		this.created = created;
	}


	public SavingsEvent(Long userId, Long savingsGoalId, SavingsRule savingsRule, EventName eventName, LocalDate date, BigDecimal amount, Long triggerId) {
	    this(null,
				userId,
				savingsGoalId,
				savingsRule.getId(),
				savingsRule.getRuleType(),
				eventName,
				date,
				amount,
				triggerId,
				null,
				false,
				new Date());
	}


	/**
	 * Returns SavingsEvent instance with given Id
	 * @param id Id
	 * @return Immutable SavingsEvent with 'id' attribute set
	 */
	public SavingsEvent withId(Long id) {
		if(Objects.equals(id, this.id)) { //TODO maybe we should only allow setting the Id if it is not there. Is this the desired behavior
			return this;
		}
		return new SavingsEvent(id, userId, savingsGoalId, savingsRuleId, ruleType, eventName, date, amount, triggerId, savingsTransferId, cancelled, created);
	}

	/**
	 * @param cancelled cancelled value to set. Cannot be null
	 * @return Immutable SavingsEvent with 'cancelled' attribute set
	 */
	public SavingsEvent cancelled(Boolean cancelled) {
		Objects.requireNonNull(cancelled);
		if(Objects.equals(cancelled, this.cancelled)) {
			return this;
		}
		return new SavingsEvent(id, userId, savingsGoalId, savingsRuleId, ruleType, eventName, date, amount, triggerId, savingsTransferId, cancelled, created);
	}


	/**
	 * @param savingsTransferId  savingsTransferId to set. Can be null
	 * @return Immutable SavingsEvent with 'savingsTransferId' attribute set
	 */
	public SavingsEvent withSavingsTransferId(Long savingsTransferId) {
		if(Objects.equals(savingsTransferId, this.savingsTransferId)) {
			return this;
		}
		return new SavingsEvent(id, userId, savingsGoalId, savingsRuleId, ruleType, eventName, date, amount, triggerId, savingsTransferId, cancelled, created);
	}

	//TODO other modifiers or constructors?


	public Long getId() {
		return id;
	}

	public Long getUserId() {
		return userId;
	}

	public Long getSavingsGoalId() {
		return savingsGoalId;
	}


	public Long getSavingsRuleId() {
		return savingsRuleId;
	}
	

	public EventName getEventName() {
		return eventName;
	}


	public LocalDate getDate() {
		return date;
	}


	public BigDecimal getAmount() {
		return amount;
	}


	public Long getTriggerId() {
		return triggerId;
	}
	

	public RuleType getRuleType() {
		return ruleType;
	}
	

	public Long getSavingsTransferId() {
		return savingsTransferId;
	}
	

	public Boolean isCancelled() {
		return cancelled;
	}

	public Date getCreated() {
		return created;
	}


	public enum EventName {
		manual, started, stopped, rule_application, ifttt_transfer, joined, withdrawal, internal_transfer, cancellation, incentive_payout, interest
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SavingsEvent that = (SavingsEvent) o;
		return Objects.equals(id, that.id) &&
				Objects.equals(userId, that.userId) &&
				Objects.equals(savingsGoalId, that.savingsGoalId) &&
				Objects.equals(savingsRuleId, that.savingsRuleId) &&
				eventName == that.eventName &&
				Objects.equals(date, that.date) &&
				Objects.equals(amount, that.amount) &&
				Objects.equals(triggerId, that.triggerId) &&
				ruleType == that.ruleType &&
				Objects.equals(savingsTransferId, that.savingsTransferId) &&
				Objects.equals(cancelled, that.cancelled) &&
				Objects.equals(created, that.created);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, userId, savingsGoalId, savingsRuleId, eventName, date, amount, triggerId, ruleType, savingsTransferId, cancelled, created);
	}

    @Override
    public String toString() {
        return "SavingsEvent{" +
                "id=" + id +
                ", userId=" + userId +
                ", savingsGoalId=" + savingsGoalId +
                ", savingsRuleId=" + savingsRuleId +
                ", eventName=" + eventName +
                ", date=" + date +
                ", amount=" + amount +
                ", triggerId=" + triggerId +
                ", ruleType=" + ruleType +
                ", savingsTransferId=" + savingsTransferId +
                ", cancelled=" + cancelled +
                ", created=" + created +
                '}';
    }
}
