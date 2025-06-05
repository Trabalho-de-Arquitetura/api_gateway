package com.api_gateway.dto.input.project;

import com.api_gateway.dto.type.ProjectStatus;

import java.time.LocalDate;
import java.util.UUID;

public class CreateProjectInput {
    public String name;
    public String objective;
    public String summaryScope;
    public String targetAudience;
    public LocalDate expectedStartDate;
    public UUID requesterId;
    public UUID groupId; // pode ser nulo

    public CreateProjectInput() {}
    public CreateProjectInput(String name, String objective, String summaryScope, String targetAudience, LocalDate expectedStartDate, UUID requesterId, UUID groupId) {
        this.name = name;
        this.objective = objective;
        this.summaryScope = summaryScope;
        this.targetAudience = targetAudience;
        this.expectedStartDate = expectedStartDate;
        this.requesterId = requesterId;
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getSummaryScope() {
        return summaryScope;
    }

    public void setSummaryScope(String summaryScope) {
        this.summaryScope = summaryScope;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public LocalDate getExpectedStartDate() {
        return expectedStartDate;
    }

    public void setExpectedStartDate(LocalDate expectedStartDate) {
        this.expectedStartDate = expectedStartDate;
    }

    public UUID getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(UUID requesterId) {
        this.requesterId = requesterId;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }
}
