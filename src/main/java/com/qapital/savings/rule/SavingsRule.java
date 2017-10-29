package com.qapital.savings.rule;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * The core configuration object for a Savings Rule.
 */
public class SavingsRule {
	
	private final Long id;
	private final Long userId;
	
	private final String placeDescription;
	private final BigDecimal amount;
	private final List<Long> savingsGoalIds;
	private final RuleType ruleType;
	private final Status status;
	


	private SavingsRule(Long id,
						Long userId,
						RuleType ruleType,
						String placeDescription,
						BigDecimal amount,
						List<Long> savingsGoalIds,
						Status status) {
		this.id = id;
		this.userId = userId;
		this.ruleType = ruleType;
		this.placeDescription = placeDescription;
		this.amount = amount;
		this.savingsGoalIds = savingsGoalIds;
		this.status = status;
	}

	public static SavingsRule createGuiltyPleasureRule(Long id, Long userId, String placeDescription, BigDecimal penaltyAmount) {
		return  new SavingsRule(id, userId, RuleType.guiltypleasure, placeDescription, penaltyAmount, new ArrayList<>(), Status.active);
	}
	
	public static SavingsRule createRoundupRule(Long id, Long userId, BigDecimal roundupToNearest) {
		return new SavingsRule(id, userId, RuleType.roundup, null, roundupToNearest, new ArrayList<>(), Status.active);
	}

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
		return savingsGoalIds;
	}

	public RuleType getRuleType() {
		return ruleType;
	}

	public Status getStatus() {
		return status;
	}
	
	public SavingsRule setStatus(Status status) {
		return status == this.status ? this :
				new SavingsRule(id, userId, ruleType, placeDescription, amount, savingsGoalIds, status);
	}

	public boolean isActive() {
		return Status.active.equals(getStatus());
	}
	
	public enum RuleType {
        guiltypleasure, roundup
	}
	
	public enum Status {
		active, deleted, paused
	}

}
