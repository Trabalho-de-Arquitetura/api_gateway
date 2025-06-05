package com.api_gateway.dto;

import java.util.List;
import java.util.UUID;

public class Group {

    private UUID id;
    private String name;
    private boolean availableForProjects;
    private User coordinator; // Armazena o ID do User coordenador
    private List<User> students;

    public Group() {}
    public Group(UUID id, String name, boolean availableForProjects, User coordinator, List<User> students) {
        this.id = id;
        this.name = name;
        this.availableForProjects = availableForProjects;
        this.coordinator = coordinator;
        this.students = students;
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

    public User getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(User coordinatorId) {
        this.coordinator = coordinatorId;
    }

    public List<User> getStudents() {
        return students;
    }

    public void setStudents(List<User> students) {
        this.students = students;
    }
}
