package com.api_gateway.dto;

import com.api_gateway.dto.type.ProjectStatus;

import java.time.LocalDate;
import java.util.UUID;

public class Project {

    private UUID id;

    private String name;

    private String objective;

    private String summaryScope;

    private String targetAudience;

    private LocalDate expectedStartDate;

    private ProjectStatus status;

    private User requesterId; // ID do User requisitante

    private Group groupId;

    public Project() {}
    public Project(UUID id, String name, String objective, String summaryScope, String targetAudience, LocalDate expectedStartDate, ProjectStatus status, User requesterId, Group groupId) {
        this.id = id;
        this.name = name;
        this.objective = objective;
        this.summaryScope = summaryScope;
        this.targetAudience = targetAudience;
        this.expectedStartDate = expectedStartDate;
        this.status = status;
        this.requesterId = requesterId;
        this.groupId = groupId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public User getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(User requesterId) {
        this.requesterId = requesterId;
    }

    public Group getGroupId() {
        return groupId;
    }

    public void setGroupId(Group groupId) {
        this.groupId = groupId;
    }
}
