package com.api_gateway.dto;

import java.util.List;
import java.util.UUID;

public class Group {

    private UUID id;
    private String name;
    private boolean availableForProjects;
    private User coordinatorId; // Armazena o ID do User coordenador
    private List<User> studentIds;

    public Group() {}
    public Group(UUID id, String name, boolean availableForProjects, User coordinatorId, List<User> studentIds) {
        this.id = id;
        this.name = name;
        this.availableForProjects = availableForProjects;
        this.coordinatorId = coordinatorId;
        this.studentIds = studentIds;
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

    public boolean isAvailableForProjects() {
        return availableForProjects;
    }

    public void setAvailableForProjects(boolean availableForProjects) {
        this.availableForProjects = availableForProjects;
    }

    public User getCoordinatorId() {
        return coordinatorId;
    }

    public void setCoordinatorId(User coordinatorId) {
        this.coordinatorId = coordinatorId;
    }

    public List<User> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<User> studentIds) {
        this.studentIds = studentIds;
    }
}
