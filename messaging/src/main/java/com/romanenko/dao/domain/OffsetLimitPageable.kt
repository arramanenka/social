package com.romanenko.dao.domain

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

class OffsetLimitPageable(
        private val offsetAmount: Int,
        private val amount: Int,
        private val sort: Sort
) : Pageable {

    override fun getPageNumber() = offsetAmount / amount

    override fun getPageSize() = amount

    override fun getOffset() = offsetAmount.toLong()

    override fun getSort() = sort

    override fun next() = OffsetLimitPageable(pageSize, (offset + pageSize).toInt(), sort)

    override fun previousOrFirst(): Pageable {
        if (hasPrevious()) {
            return OffsetLimitPageable(pageSize, offsetAmount - pageSize, sort)
        }
        return first()
    }

    override fun first() = OffsetLimitPageable(pageSize, 0, sort)

    override fun hasPrevious() = offsetAmount > amount
}
