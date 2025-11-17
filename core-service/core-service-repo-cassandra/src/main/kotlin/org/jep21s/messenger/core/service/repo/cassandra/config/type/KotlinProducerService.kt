package org.jep21s.messenger.core.service.repo.cassandra.config.type

import com.datastax.oss.driver.api.mapper.result.MapperResultProducer
import com.datastax.oss.driver.api.mapper.result.MapperResultProducerService

class KotlinProducerService : MapperResultProducerService {
    override fun getProducers(): MutableIterable<MapperResultProducer> =
        mutableListOf(CompletionStageOfUnitProducer())
}