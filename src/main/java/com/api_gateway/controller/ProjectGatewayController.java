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
    public Flux<Project> findAllProjectsByRequester(@Argument UUID requester_id) {
        String document = """
                query findAllProjectsByRequester($requester_id: ID!){
                    findAllProjectsByRequester(requester_id: $requester_id) {
                        id
                        name
                        objective
                        summaryScope
                        targetAudience
                        expectedStartDate
                        status
                        requesterId { id }
                        groupId { id }
                    }
                }
            """;
        return projectsClient.document(document)
                .variable("requester_id", requester_id)
                .retrieve("findAllProjectsByRequester")
                .toEntityList(Project.class)
                .flatMapMany(Flux::fromIterable);
    }

    @QueryMapping
    public Flux<Project> findAllProjects() {
        String document = """
                query FindAllProjects {
                    findAllProjects {
                        id
                        name
                        objective
                        summaryScope
                        targetAudience
                        expectedStartDate
                        status
                        requesterId { id }
                        groupId { id }
                    }
                }
            """;
        return projectsClient.document(document)
                .retrieve("findAllProjects")
                .toEntityList(Project.class)
                .flatMapMany(Flux::fromIterable);
    }

    @SchemaMapping(typeName = "ProjectDTO", field = "requesterId")
    public Mono<User> getRequester(Project project) {
        if (project.getRequesterId() == null || project.getRequesterId().getId() == null) {
            return Mono.empty();
        }
        UUID requesterId = project.getRequesterId().getId();

        String userDocument = """
            query FindUserById($id: ID!) {
                findUserById(id: $id) {
                    id
                    name
                    email
                    password
                    affiliatedSchool
                    role
                }
            }
        """;
        return usersClient.document(userDocument)
                .variable("id", requesterId)
                .retrieve("findUserById")
                .toEntity(User.class);
    }

    @SchemaMapping(typeName = "ProjectDTO", field = "groupId")
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
    public Mono<Project> saveProject(@Argument CreateProjectInput input) {
        String document = """
            mutation SaveProject($input: CreateProjectInput!) {
                saveProject(input: $input!) {
                    id
                    name
                    objective
                    summaryScope
                    targetAudience
                    expectedStartDate
                    status
                    requesterId { id }
                    groupId { id }
                }
            }
        """;
        return projectsClient.document(document)
                .variable("input", input)
                .retrieve("saveProject")
                .toEntity(Project.class);
    }
}
