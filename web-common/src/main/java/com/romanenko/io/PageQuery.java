package com.romanenko.io;

import org.springframework.web.reactive.function.server.ServerRequest;

public class PageQuery {
    public final int page;
    public final int pageSize;

    public PageQuery(ServerRequest request) throws NumberFormatException {
        this(
                request.queryParam("page")
                        .map(Integer::parseInt)
                        .orElse(0),
                request.queryParam("pageSize")
                        .map(Integer::parseInt)
                        .orElse(5)
        );
    }

    public PageQuery(int page, int pageSize) {
        this.page = Math.max(page, 0);
        this.pageSize = pageSize < 1 ? 5 : pageSize;
    }

    public int calculateSkipAmount() {
        return page * pageSize;
    }

    public int fullRawQueryAmount() {
        return calculateSkipAmount() + pageSize;
    }
}
