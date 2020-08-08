package com.romanenko.dao.mongo

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.EnableMongoAuditing

@Configuration
@EnableMongoAuditing
@ConditionalOnProperty(name = ["message.storage"], havingValue = "mongo", matchIfMissing = true)
open class AuditingConfig