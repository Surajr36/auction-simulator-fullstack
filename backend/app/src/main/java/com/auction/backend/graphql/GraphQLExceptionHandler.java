package com.auction.backend.graphql;

import com.auction.backend.exception.DomainException;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;

@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex,
            graphql.schema.DataFetchingEnvironment env) {
        if (ex instanceof DomainException) {
            return GraphqlErrorBuilder.newError(env)
                    .message(ex.getMessage())
                    .errorType(ErrorType.ValidationError)
                    .build();
        }
        return null;
    }
}
