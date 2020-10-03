package com.romanenko.io;

import org.springframework.web.reactive.function.server.ServerRequest;

public class PageQuery {
    public final int skipAmount;
    public final int amount;

    public PageQuery(ServerRequest request) throws NumberFormatException {
        this(
                request.queryParam("skipAmount")
                        .map(Integer::parseInt)
                        .orElse(0),
                request.queryParam("amount")
                        .map(Integer::parseInt)
                        .orElse(5)
        );
    }

    public PageQuery(int skipAmount, int amount) {
        this.skipAmount = Math.max(skipAmount, 0);
        this.amount = amount < 1 ? 5 : amount;
    }

    public int calculateSkipAmount() {
        return skipAmount;
    }

    public int fullRawQueryAmount() {
        return skipAmount + amount;
    }

    public int calculatePage() {
        return skipAmount / amount;
    }
}
