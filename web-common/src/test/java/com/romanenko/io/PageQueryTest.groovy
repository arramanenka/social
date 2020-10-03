package com.romanenko.io

import org.springframework.web.reactive.function.server.ServerRequest
import spock.lang.Specification

class PageQueryTest extends Specification {

    def "test page query"() {
        expect:
        def pageQuery = new PageQuery(mockRequest(skipAmount, amount))
        pageQuery.skipAmount == expectedSkipAmount
        pageQuery.amount == expectedPageSize
        pageQuery.calculateSkipAmount() == expectedSkipAmount
        pageQuery.fullRawQueryAmount() == expectedFullRawQueryAmount
        where:
        skipAmount | amount | expectedPageSize | expectedSkipAmount | expectedFullRawQueryAmount
        "0"        | null   | 5                | 0                  | 5
        null       | "0"    | 5                | 0                  | 5
        "0"        | "0"    | 5                | 0                  | 5
        "-1"       | "0"    | 5                | 0                  | 5
        "0"        | "-1"   | 5                | 0                  | 5
        "5"        | "0"    | 5                | 5                  | 10
        "0"        | "1"    | 1                | 0                  | 1
        "1"        | "1"    | 1                | 1                  | 2
    }


    def mockRequest(skipAmount, amount) {
        ServerRequest requestMock = Mock(ServerRequest)
        requestMock.queryParam("skipAmount") >> Optional.ofNullable(skipAmount)
        requestMock.queryParam("amount") >> Optional.ofNullable(amount)
        return requestMock
    }
}
