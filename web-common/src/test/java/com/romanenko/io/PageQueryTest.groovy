package com.romanenko.io

import org.springframework.web.reactive.function.server.ServerRequest
import spock.lang.Specification

class PageQueryTest extends Specification {

    def "test page query"() {
        expect:
        def pageQuery = new PageQuery(mockRequest(page, pageSize))
        pageQuery.page == expectedPage
        pageQuery.pageSize == expectedPageSize
        pageQuery.calculateSkipAmount() == expectedSkipAmount
        pageQuery.fullRawQueryAmount() == expectedFullRawQueryAmount
        where:
        page | pageSize | expectedPage | expectedPageSize | expectedSkipAmount | expectedFullRawQueryAmount
        "0"  | null     | 0            | 5                | 0                  | 5
        null | "0"      | 0            | 5                | 0                  | 5
        "0"  | "0"      | 0            | 5                | 0                  | 5
        "-1" | "0"      | 0            | 5                | 0                  | 5
        "0"  | "-1"     | 0            | 5                | 0                  | 5
        "1"  | "0"      | 1            | 5                | 5                  | 10
        "0"  | "1"      | 0            | 1                | 0                  | 1
        "1"  | "1"      | 1            | 1                | 1                  | 2
    }


    def mockRequest(page, pageSize) {
        ServerRequest requestMock = Mock(ServerRequest)
        requestMock.queryParam("page") >> Optional.ofNullable(page)
        requestMock.queryParam("pageSize") >> Optional.ofNullable(pageSize)
        return requestMock
    }
}
