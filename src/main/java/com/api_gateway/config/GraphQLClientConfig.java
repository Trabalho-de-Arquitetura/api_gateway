package com.api_gateway.config; // Certifique-se que o package está correto

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.UUID;
import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;

@Configuration
public class GraphQLClientConfig {

    private final ReactorLoadBalancerExchangeFilterFunction lbFunction;

    public GraphQLClientConfig(ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        this.lbFunction = lbFunction;
    }

    @Bean
    public HttpGraphQlClient usersGraphQlClient(
            @Value("${app.service.users.baseurl}") String baseUrl, // Ex: http://users-service
            WebClient.Builder webClientBuilder) { // Injeta um WebClient.Builder padrão
        WebClient webClient = webClientBuilder
                .baseUrl(baseUrl) // Define a URL base (sem /graphql)
                .filter(lbFunction) // Aplica o filtro de load balancing
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
        // HttpGraphQlClient usará esta WebClient; ele adicionará /graphql por padrão ao fazer a requisição
        return HttpGraphQlClient.builder(webClient).url(baseUrl+"/graphql").build();
    }

    @Bean
    public HttpGraphQlClient groupsGraphQlClient(
            @Value("${app.service.groups.baseurl}") String baseUrl,
            WebClient.Builder webClientBuilder) {
        WebClient webClient = webClientBuilder
                .baseUrl(baseUrl)
                .filter(lbFunction)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
        return HttpGraphQlClient.builder(webClient).url(baseUrl+"/graphql").build();
    }

    @Bean
    public HttpGraphQlClient projectsGraphQlClient(
            @Value("${app.service.projects.baseurl}") String baseUrl,
            WebClient.Builder webClientBuilder) {
        WebClient webClient = webClientBuilder
                .baseUrl(baseUrl)
                .filter(lbFunction)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
        return HttpGraphQlClient.builder(webClient).url(baseUrl+"/graphql").build();
    }

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
                .name("UUID")
                .description("Java UUID Scalar")
                .coercing(uuidCoercing)
                .build();
    }

    @Bean
    public GraphQLScalarType dateScalar() {
        return GraphQLScalarType.newScalar()
                .name("Date")
                .description("Data no formato ISO (yyyy-MM-dd)")
                .coercing(new Coercing<LocalDate, String>() {
                    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

                    @Override
                    public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
                        if (dataFetcherResult instanceof LocalDate) {
                            return ((LocalDate) dataFetcherResult).format(formatter);
                        }
                        throw new CoercingSerializeException("Esperado um LocalDate para serialização");
                    }

                    @Override
                    public LocalDate parseValue(Object input) throws CoercingParseValueException {
                        if (input instanceof String) {
                            try {
                                return LocalDate.parse((String) input, formatter);
                            } catch (DateTimeParseException e) {
                                throw new CoercingParseValueException("Erro ao converter valor para LocalDate");
                            }
                        }
                        throw new CoercingParseValueException("Esperado uma String para parseValue");
                    }

                    @Override
                    public LocalDate parseLiteral(Object input) throws CoercingParseLiteralException {
                        if (input instanceof StringValue) {
                            try {
                                return LocalDate.parse(((StringValue) input).getValue(), formatter);
                            } catch (DateTimeParseException e) {
                                throw new CoercingParseLiteralException("Erro ao converter literal para LocalDate");
                            }
                        }
                        throw new CoercingParseLiteralException("Esperado StringValue para parseLiteral");
                    }
                })
                .build();
    }

    // Seu RuntimeWiringConfigurer para registrar os scalars
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer(GraphQLScalarType uuidScalar, GraphQLScalarType dateScalar) {
        return wiringBuilder -> wiringBuilder
                .scalar(uuidScalar)
                .scalar(dateScalar);
    }
}