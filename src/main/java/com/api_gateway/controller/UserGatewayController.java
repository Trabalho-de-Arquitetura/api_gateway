package com.api_gateway.controller;

import com.api_gateway.dto.User;
import com.api_gateway.dto.input.CreateUserInput;
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
    public Mono<User> userById(@Argument UUID id) {
        // A query que será enviada para o users-service
        String document = """
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
        return usersClient.document(document)
                .variable("id", id)
                .retrieve("userById")
                .toEntity(User.class);
    }

    @QueryMapping
    public Mono<User> userByEmail(@Argument String email) {
        String document = """
            query UserByEmail($email: String!) {
                userByEmail(email: $email) {
                    id name email affiliatedSchool role
                }
            }
        """;
        return usersClient.document(document)
                .variable("email", email)
                .retrieve("userByEmail")
                .toEntity(User.class);
    }

    @QueryMapping
    public Flux<User> allUsers() { // Use Flux para listas
        String document = """
            query AllUsers {
                allUsers {
                    id name email affiliatedSchool role
                }
            }
        """;
        return usersClient.document(document)
                .retrieve("allUsers")
                .toEntityList(User.class) // Converte para lista
                .flatMapMany(Flux::fromIterable); // Converte Mono<List<User>> para Flux<User>
    }

    @MutationMapping
    public Mono<User> createUser(@Argument CreateUserInput input) {
        String document = """
            mutation CreateUser($input: CreateUserInput!) {
                createUser(input: $input) {
                    id name email affiliatedSchool role
                }
            }
        """;
        // O input DTO do gateway deve ser compatível com o input DTO do microsserviço
        return usersClient.document(document)
                .variable("input", input) // Spring converterá o DTO para um Map se necessário
                .retrieve("createUser")
                .toEntity(User.class);
    }
}