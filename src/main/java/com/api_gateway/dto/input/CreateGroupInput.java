package com.api_gateway.dto.input;

import java.util.List;
import java.util.UUID;

public class CreateGroupInput {
    public String name;
    public boolean availableForProjects;
    public UUID coordinator;
    public List<UUID> students;

    public CreateGroupInput() {}
    public CreateGroupInput(String name, boolean availableForProjects, UUID coordinator, List<UUID> students) {
        this.name = name;
        this.availableForProjects = availableForProjects;
        this.coordinator = coordinator;
        this.students = students;
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

    public UUID getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(UUID coordinator) {
        this.coordinator = coordinator;
    }

    public List<UUID> getStudents() {
        return students;
    }

    public void setStudents(List<UUID> students) {
        this.students = students;
    }
}
