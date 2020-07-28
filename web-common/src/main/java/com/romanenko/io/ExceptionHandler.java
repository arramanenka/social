package com.romanenko.io;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.stream.Collectors;

/**
 * Credit for exception handler example goes to https://stackoverflow.com/a/61485244/11948342
 */
@Component
@Order(-2)
public class ExceptionHandler extends AbstractErrorWebExceptionHandler {
    private final ResponseSupplier responseSupplier;

    public ExceptionHandler(
            ErrorAttributes errorAttributes,
            ResourceProperties resourceProperties,
            ApplicationContext applicationContext,
            ObjectProvider<ViewResolver> viewResolvers,
            ServerCodecConfigurer serverCodecConfigurer,
            ResponseSupplier responseSupplier
    ) {
        super(errorAttributes, resourceProperties, applicationContext);
        setViewResolvers(viewResolvers.orderedStream().collect(Collectors.toList()));
        setMessageWriters(serverCodecConfigurer.getWriters());
        setMessageReaders(serverCodecConfigurer.getReaders());
        this.responseSupplier = responseSupplier;
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), serverRequest -> {
            Throwable error = getError(serverRequest);
            return responseSupplier.error(error);
        });
    }
}
