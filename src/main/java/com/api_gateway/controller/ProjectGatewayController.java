package com.api_gateway.controller;

import com.api_gateway.dto.Group;
import com.api_gateway.dto.Project;
import com.api_gateway.dto.User;
import com.api_gateway.dto.input.project.CreateProjectInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Controller
public class ProjectGatewayController {

    private final HttpGraphQlClient projectsClient;
    private final HttpGraphQlClient groupsClient;
    private final HttpGraphQlClient usersClient;

    @Autowired
    public ProjectGatewayController(@Qualifier("projectsGraphQlClient") HttpGraphQlClient projectsClient,
                                    @Qualifier("groupsGraphQlClient") HttpGraphQlClient groupsClient,
                                    @Qualifier("usersGraphQlClient") HttpGraphQlClient usersClient) {
        this.projectsClient = projectsClient;
        this.groupsClient = groupsClient;
        this.usersClient = usersClient;
    }

    @QueryMapping
    public Mono<Project> projectById(@Argument UUID id) {
        String document = """
                query ProjectById($id: ID!){
                    projectById(id: $id) {
                        id
                        name
                        objective
                        summaryScope
                        targetAudience
                        expectedStartDate
                        status
                        requester { id }
                        group { id }
                    }
                }
            """;
        return projectsClient.document(document)
                .variable("id", id)
                .retrieve("projectById")
                .toEntity(Project.class);
    }

    @QueryMapping
    public Flux<Project> allProjects() {
        String document = """
                query AllProjects {
                    allProjects {
                        id
                        name
                        objective
                        summaryScope
                        targetAudience
                        expectedStartDate
                        status
                        requester { id }
                        group { id }
                    }
                }
            """;
        return projectsClient.document(document)
                .retrieve("allProjects")
                .toEntityList(Project.class)
                .flatMapMany(Flux::fromIterable);
    }

    @SchemaMapping(typeName = "Project", field = "requester")
    public Mono<User> getRequester(Project project) {
        if (project.getRequesterId() == null || project.getRequesterId().getId() == null) {
            return Mono.empty();
        }
        UUID requesterId = project.getRequesterId().getId();

        String userDocument = """
            query UserById($userId: ID!) {
                userById(id: $userId) {
                    id name email affiliatedSchool role
                }
            }
        """;
        return usersClient.document(userDocument)
                .variable("userId", requesterId)
                .retrieve("userById")
                .toEntity(User.class);
    }

    @SchemaMapping(typeName = "Project", field = "group")
    public Mono<Group> getGroup(Project project) {
        if (project.getGroupId() == null || project.getGroupId().getId() == null) {
            return Mono.empty();
        }
        UUID groupId = project.getGroupId().getId();

        String groupDocument = """
            query GroupById($groupId: ID!) {
                groupById(id: $groupId) {
                    id
                    name
                    availableForProjects
                    coordinator { id }
                    students { id }
                }
            }
        """;
        return groupsClient.document(groupDocument)
                .variable("groupId", groupId)
                .retrieve("groupById")
                .toEntity(Group.class);
    }

    @MutationMapping
    public Mono<Project> createProject(@Argument CreateProjectInput input) {
        String document = """
            mutation CreateProject($input: CreateProjectInput!) {
                createProject(input: $input!) {
                    id
                    name
                    objective
                    summaryScope
                    targetAudience
                    expectedStartDate
                    status
                    requester { id }
                    group { id }
                }
            }
        """;
        return projectsClient.document(document)
                .variable("input", input)
                .retrieve("createProject")
                .toEntity(Project.class);
    }
}
