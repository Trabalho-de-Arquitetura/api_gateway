package com.api_gateway.controller;

import com.api_gateway.dto.Group;
import com.api_gateway.dto.User;
import com.api_gateway.dto.input.CreateGroupInput;
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
import java.util.stream.Collectors;

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
    public Mono<Group> groupById(@Argument UUID id) {
        // Note que esta query ao groups-service só pede IDs para coordinator e students
        // Os objetos completos User serão resolvidos pelos @SchemaMapping abaixo.
        String document = """
            query GroupById($id: ID!) {
                groupById(id: $id) {
                    id
                    name
                    availableForProjects
                    coordinator { id }
                    students { id }
                }
            }
        """;
        return groupsClient.document(document)
                .variable("id", id)
                .retrieve("groupById")
                .toEntity(Group.class);
    }

    @QueryMapping
    public Flux<Group> allGroups() {
        String document = """
            query AllGroups {
                allGroups {
                    id
                    name
                    availableForProjects
                    coordinator { id }
                    students { id }
                }
            }
        """;
        return groupsClient.document(document)
                .retrieve("allGroups")
                .toEntityList(Group.class)
                .flatMapMany(Flux::fromIterable);
    }


    // Resolver para o campo 'coordinator' do tipo 'Group'
    @SchemaMapping(typeName = "Group", field = "coordinator")
    public Mono<User> getCoordinator(Group group) {
        // 'group' é o objeto retornado pela query principal ao groups-service.
        // Ele deve ter o ID do coordenador (group.getCoordinator().getId()).
        if (group.getCoordinatorId() == null || group.getCoordinatorId().getId() == null) {
            return Mono.empty(); // Ou lançar erro se coordenador for obrigatório
        }
        UUID coordinatorId = group.getCoordinatorId().getId();

        String userDocument = """
            query UserById($id: ID!) {
                userById(id: $id) {
                    id
                    name
                    email
                    affiliatedSchool
                    role
                }
            }
        """;
        return usersClient.document(userDocument)
                .variable("id", coordinatorId)
                .retrieve("userById")
                .toEntity(User.class);
    }

    // Resolver para o campo 'students' do tipo 'Group'
    @SchemaMapping(typeName = "Group", field = "students")
    public Flux<User> getStudents(Group group) {
        if (group.getStudentIds() == null || group.getStudentIds().isEmpty()) {
            return Flux.empty();
        }
        List<UUID> studentIds = group.getStudentIds().stream()
                .map(User::getId) // Assumindo que User stub tem getId()
                .collect(Collectors.toList());

        if (studentIds.isEmpty()) return Flux.empty();

        return Flux.fromIterable(studentIds)
                .flatMap(studentId -> {
                    String userDocument = """
                    query UserById($id: ID!) {
                        userById(id: $id) {
                            id
                            name
                            email
                            affiliatedSchool
                            role
                        }
                    }
                """;
                    return usersClient.document(userDocument)
                            .variable("id", studentId)
                            .retrieve("userById")
                            .toEntity(User.class);
                });
        // Se o users-service suportar uma query como "usersByIds(ids: [ID!]): [User]" seria melhor.
    }

    @MutationMapping
    public Mono<Group> createGroup(@Argument CreateGroupInput input) {
        String document = """
            mutation CreateGroup($input: CreateGroupInput!) {
                createGroup(input: $input) {
                    id
                    name
                    availableForProjects
                    coordinator { id } # Retorna ID para ser resolvido depois
                    students { id }    # Retorna IDs para serem resolvidos depois
                }
            }
        """;
        return groupsClient.document(document)
                .variable("input", input)
                .retrieve("createGroup")
                .toEntity(Group.class);
    }
}