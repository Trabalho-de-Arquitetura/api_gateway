package com.api_gateway.controller;

import com.api_gateway.dto.Group;
import com.api_gateway.dto.User;
import com.api_gateway.dto.input.group.CreateGroupInput;
import com.api_gateway.dto.input.group.UpdateGroupInput;
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

import java.util.List;
import java.util.UUID;

@Controller
public class GroupGatewayController {

    private final HttpGraphQlClient groupsClient;
    private final HttpGraphQlClient usersClient; // Para resolver User dentro de Group

    @Autowired
    public GroupGatewayController(@Qualifier("groupsGraphQlClient") HttpGraphQlClient groupsClient,
                                  @Qualifier("usersGraphQlClient") HttpGraphQlClient usersClient) {
        this.groupsClient = groupsClient;
        this.usersClient = usersClient;
    }

    @QueryMapping
    public Flux<Group> findAllGroupsById(@Argument List<UUID> id) {
        String document = """
            query FindAllGroupsById($id: [ID!]!) {
                findAllGroupsById(id: $id) {
                    id
                    name
                    availableForProjects
                    coordinator {
                        id
                    }
                    students {
                        id
                    }
                }
            }
        """;
        return groupsClient.document(document)
                .variable("id", id)
                .retrieve("findAllGroupsById")
                .toEntityList(Group.class)
                .flatMapMany(Flux::fromIterable);
    }

    @QueryMapping
    public Flux<Group> findAllGroupsByNameIn(@Argument List<String> names) {
        String document = """
            query findAllGroupsByNameIn($names: [String!]!) {
                findAllGroupsByNameIn(names: $names) {
                    id
                    name
                    availableForProjects
                    coordinator {
                        id
                    }
                    students {
                        id
                    }
                }
            }
        """;
        return groupsClient.document(document)
                .variable("names", names)
                .retrieve("findAllGroupsByNameIn")
                .toEntityList(Group.class)
                .flatMapMany(Flux::fromIterable);
    }

    @QueryMapping
    public Flux<Group> findAllGroupByCoordinator(@Argument UUID coordinator_id) {
        String document = """
            query FindAllGroupByCoordinator($coordinator_id: ID!) {
                findAllGroupByCoordinator(coordinator_id: $coordinator_id) {
                    id
                    name
                    availableForProjects
                    coordinator {
                        id
                    }
                    students {
                        id
                    }
                }
            }
        """;
        return groupsClient.document(document)
                .variable("coordinator_id", coordinator_id)
                .retrieve("findAllGroupByCoordinator")
                .toEntityList(Group.class)
                .flatMapMany(Flux::fromIterable);
    }

    @QueryMapping
    public Flux<Group> findAllGroupsByStudentId(@Argument UUID student_id) {
        String document = """
            query FindAllGroupsByStudentId($student_id: ID!) {
                findAllGroupsByStudentId(student_id: $student_id) {
                    id
                    name
                    availableForProjects
                    coordinator {
                        id
                    }
                    students {
                        id
                    }
                }
            }
        """;
        return groupsClient.document(document)
                .variable("student_id", student_id)
                .retrieve("findAllGroupsByStudentId")
                .toEntityList(Group.class)
                .flatMapMany(Flux::fromIterable);
    }

    @QueryMapping
    public Flux<Group> findAllGroups() {
        String document = """
            query FindAllGroups {
                findAllGroups {
                    id
                    name
                    availableForProjects
                    coordinator {
                        id
                    }
                    students {
                        id
                    }
                }
            }
        """;
        return groupsClient.document(document)
                .retrieve("findAllGroups")
                .toEntityList(Group.class)
                .flatMapMany(Flux::fromIterable);
    }

    @SchemaMapping(typeName = "GroupDTO", field = "coordinator")
    public Mono<User> getCoordinator(Group group) {
        if (group.getCoordinator() == null || group.getCoordinator().getId() == null) {
            return Mono.empty();
        }

        UUID coordinatorId = group.getCoordinator().getId();

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
                .variable("id", coordinatorId)
                .retrieve("findUserById")
                .toEntity(User.class);
    }

    @SchemaMapping(typeName = "GroupDTO", field = "students")
    public Flux<User> getStudents(Group group) {
        if (group.getStudents() == null || group.getStudents().isEmpty()) {
            return Flux.empty();
        }

        return Flux.fromIterable(group.getStudents())
                .filter(student -> student != null && student.getId() != null)
                .flatMap(student -> {
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
                            .variable("id", student.getId())
                            .retrieve("findUserById")
                            .toEntity(User.class);
                });
    }

    @MutationMapping
    public Mono<Group> saveGroup(@Argument CreateGroupInput input) {
        String document = """
            mutation SaveGroup($input: CreateGroupInput!) {
                saveGroup(input: $input) {
                    id
                    name
                    availableForProjects
                    coordinator {
                        id
                    }
                    students {
                        id
                    }
                }
            }""";
        return groupsClient.document(document)
                .variable("input", input)
                .retrieve("saveGroup")
                .toEntity(Group.class);
    }

    @MutationMapping
    public Mono<Group> updateGroup(@Argument UpdateGroupInput input) {
        String document = """
            mutation UpdateGroup($input: UpdateGroupInput!) {
                updateGroup(input: $input) {
                    id
                    name
                    availableForProjects
                    coordinator {
                        id
                    }
                    students {
                        id
                    }
                }
            }""";
        return groupsClient.document(document)
                .variable("input", input)
                .retrieve("updateGroup")
                .toEntity(Group.class);
    }
}