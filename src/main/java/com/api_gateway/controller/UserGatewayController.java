package com.api_gateway.controller;

import com.api_gateway.dto.User;
import com.api_gateway.dto.input.user.CreateUserInput;
import com.api_gateway.dto.input.user.UpdateUserInput;
import com.api_gateway.dto.type.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Controller
public class UserGatewayController {

    private final HttpGraphQlClient usersClient;

    @Autowired
    public UserGatewayController(HttpGraphQlClient usersGraphQlClient) { // Nome do bean como definido em GraphQLClientConfig
        this.usersClient = usersGraphQlClient;
    }

    @QueryMapping
    public Mono<User> findUserById(@Argument UUID id) {
        // A query que será enviada para o users-service
        String document = """
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
        return usersClient.document(document)
                .variable("id", id)
                .retrieve("findUserById")
                .toEntity(User.class);
    }

    @QueryMapping
    public Mono<User> findUserByEmail(@Argument String email) {
        String document = """
            query FindUserByEmail($email: String!) {
                findUserByEmail(email: $email) {
                    id name email password affiliatedSchool role
                }
            }
        """;
        return usersClient.document(document)
                .variable("email", email)
                .retrieve("findUserByEmail")
                .toEntity(User.class);
    }

    @QueryMapping
    public Flux<User> findUsersByRole(@Argument UserRole role) {
        String document = """
            query FindUsersByRole($role: UserRole!) {
                findUsersByRole(role: $role) {
                    id name email password affiliatedSchool role
                }
            }
        """;

        return usersClient.document(document)
                .variable("role", role)
                .retrieve("findUsersByRole")
                .toEntityList(User.class)
                .flatMapMany(Flux::fromIterable);
    }

    @QueryMapping
    public Flux<User> findAllUsers() { // Use Flux para listas
        String document = """
            query FindAllUsers {
                findAllUsers {
                    id name email password affiliatedSchool role
                }
            }
        """;
        return usersClient.document(document)
                .retrieve("findAllUsers")
                .toEntityList(User.class) // Converte para lista
                .flatMapMany(Flux::fromIterable); // Converte Mono<List<User>> para Flux<User>
    }

    @MutationMapping
    public Mono<User> saveUser(@Argument CreateUserInput input) {
        String document = """
            mutation SaveUser($input: CreateUserInput!) {
                saveUser(input: $input) {
                    id name email password affiliatedSchool role
                }
            }
        """;
        // O input DTO do gateway deve ser compatível com o input DTO do microsserviço
        return usersClient.document(document)
                .variable("input", input) // Spring converterá o DTO para um Map se necessário
                .retrieve("saveUser")
                .toEntity(User.class);
    }

    @MutationMapping
    public Mono<User> updateUser(@Argument UpdateUserInput input) {
        String document = """
                mutation UpdateUser($input: UpdateUserInput!) {
                    updateUser(input: $input) {
                        id name email password affiliatedSchool role
                    }
                }
        """;

        return usersClient.document(document)
                .variable("input", input)
                .retrieve("updateUser")
                .toEntity(User.class);
    }

    @MutationMapping
    public Mono<Boolean> deleteUser(@Argument UUID id) {
        String document = """
            mutation DeleteUser($id: ID!) {
                deleteUser(id: $id)
            }
        """;

        return usersClient.document(document)
                .variable("id", id)
                .retrieve("deleteUser")
                .toEntity(Boolean.class);
    }
}