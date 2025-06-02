package com.api_gateway.config; // Certifique-se que o package está correto

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced; // << IMPORTANTE: Adicionar este import
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate; // Mantenha seus scalars como estavam
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.UUID;
import graphql.GraphQLContext; // Para o scalar de Date
import graphql.execution.CoercedVariables; // Para o scalar de Date

@Configuration
public class GraphQLClientConfig {

    // 1. Defina um bean para WebClient.Builder que seja LoadBalanced
    @Bean
    @LoadBalanced // Esta anotação habilita o load balancing
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    // 2. Injete o WebClient.Builder @LoadBalanced nos seus beans HttpGraphQlClient
    // Spring irá injetar o bean 'loadBalancedWebClientBuilder' definido acima.
    @Bean
    public HttpGraphQlClient usersGraphQlClient(
            @Value("${app.service.users.uri}") String uri,
            @LoadBalanced WebClient.Builder webClientBuilder) { // A injeção do builder load balanced
        WebClient webClient = webClientBuilder.baseUrl(uri).build();
        return HttpGraphQlClient.builder(webClient).build();
    }

    @Bean
    public HttpGraphQlClient groupsGraphQlClient(
            @Value("${app.service.groups.uri}") String uri,
            @LoadBalanced WebClient.Builder webClientBuilder) {
        WebClient webClient = webClientBuilder.baseUrl(uri).build();
        return HttpGraphQlClient.builder(webClient).build();
    }

    @Bean
    public HttpGraphQlClient projectsGraphQlClient(
            @Value("${app.service.projects.uri}") String uri,
            @LoadBalanced WebClient.Builder webClientBuilder) {
        WebClient webClient = webClientBuilder.baseUrl(uri).build();
        return HttpGraphQlClient.builder(webClient).build();
    }

    // Seu bean para o scalar UUID (se você o tiver e quiser mantê-lo)
    @Bean
    public GraphQLScalarType uuidScalar() {
        Coercing<UUID, String> uuidCoercing = new Coercing<UUID, String>() {
            @Override
            public String serialize(Object dataFetcherResult, GraphQLContext context, Locale locale) throws CoercingSerializeException {
                if (dataFetcherResult instanceof UUID) {
                    return dataFetcherResult.toString();
                }
                throw new CoercingSerializeException("Expected a UUID object.");
            }

            @Override
            public UUID parseValue(Object input, GraphQLContext context, Locale locale) throws CoercingParseValueException {
                try {
                    if (input instanceof String) {
                        return UUID.fromString((String) input);
                    }
                    throw new CoercingParseValueException("Expected a String for UUID.");
                } catch (IllegalArgumentException e) {
                    throw new CoercingParseValueException("Invalid UUID format: " + input, e);
                }
            }

            public UUID parseLiteral(Value input, CoercedVariables variables, GraphQLContext context, Locale locale) throws CoercingParseLiteralException {
                if (input instanceof StringValue) {
                    try {
                        return UUID.fromString(((StringValue) input).getValue());
                    } catch (IllegalArgumentException e) {
                        throw new CoercingParseLiteralException("Invalid UUID format: " + ((StringValue) input).getValue(), e);
                    }
                }
                throw new CoercingParseLiteralException("Expected a StringValue for UUID.");
            }
        };

        return GraphQLScalarType.newScalar()
                .name("UUID") // Deve corresponder ao nome no seu schema do gateway
                .description("Java UUID Scalar")
                .coercing(uuidCoercing)
                .build();
    }


    // Seu bean para o scalar Date
    @Bean
    public GraphQLScalarType dateScalar() {
        return GraphQLScalarType.newScalar()
                .name("Date")
                .description("Data no formato ISO (yyyy-MM-dd)")
                .coercing(new Coercing<LocalDate, String>() {
                    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

                    @Override
                    public String serialize(Object dataFetcherResult, GraphQLContext context, Locale locale) throws CoercingSerializeException {
                        if (dataFetcherResult instanceof LocalDate) {
                            return ((LocalDate) dataFetcherResult).format(formatter);
                        }
                        throw new CoercingSerializeException("Data inválida para serialização: " + dataFetcherResult.getClass().getName());
                    }

                    @Override
                    public LocalDate parseValue(Object input, GraphQLContext context, Locale locale) throws CoercingParseValueException {
                        try {
                            if (input instanceof String) {
                                return LocalDate.parse((String) input, formatter);
                            }
                            throw new CoercingParseValueException("Formato de valor inválido para LocalDate: " + input.getClass().getName());
                        } catch (DateTimeParseException e) {
                            throw new CoercingParseValueException("Erro ao fazer parse do valor para LocalDate", e);
                        }
                    }

                    public LocalDate parseLiteral(Value input, CoercedVariables variables, GraphQLContext context, Locale locale) throws CoercingParseLiteralException {
                        if (input instanceof StringValue) {
                            try {
                                return LocalDate.parse(((StringValue) input).getValue(), formatter);
                            } catch (DateTimeParseException e) {
                                throw new CoercingParseLiteralException("Erro ao converter literal para LocalDate", e);
                            }
                        }
                        throw new CoercingParseLiteralException("Valor literal inválido para data");
                    }
                })
                .build();
    }

    // Seu RuntimeWiringConfigurer para registrar os scalars
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer(GraphQLScalarType uuidScalar, GraphQLScalarType dateScalar) {
        return wiringBuilder -> wiringBuilder
                .scalar(uuidScalar) // Registra o scalar UUID
                .scalar(dateScalar); // Registra o scalar Date
    }
}