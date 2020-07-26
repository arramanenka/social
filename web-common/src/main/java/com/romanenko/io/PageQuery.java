package com.romanenko.io;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.server.ServerRequest;

@RequiredArgsConstructor
public class PageQuery {
    public final int page;
    public final int pageSize;

    public PageQuery(ServerRequest request) throws NumberFormatException {
        page = request.queryParam("page")
                .map(Integer::parseInt)
                .filter(p -> p >= 0)
                .orElse(0);
        pageSize = request.queryParam("pageSize")
                .map(Integer::parseInt)
                .filter(p -> p >= 1)
                .orElse(5);
    }

    public int calculateSkipAmount() {
        return page * pageSize;
    }
}
