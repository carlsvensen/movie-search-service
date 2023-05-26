package dk.cygni.moviesearchservice.config

import com.mongodb.MongoClientSettings
import com.mongodb.event.CommandListener
import com.mongodb.event.CommandStartedEvent
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.boot.autoconfigure.mongo.MongoPropertiesClientSettingsBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Component

@Configuration
class MongoClientConfig {

    @Bean
    fun mongoClientSettings(insertListener: InsertListener): MongoClientSettings =
        MongoClientSettings.builder().addCommandListener(insertListener).build()

    @Bean
    fun mongoPropertiesCustomizer(properties: MongoProperties): MongoPropertiesClientSettingsBuilderCustomizer =
        MongoPropertiesClientSettingsBuilderCustomizer(properties)
}

@Component
class InsertListener(
    private val jmsTemplate: JmsTemplate
) : CommandListener {

    // TODO: Listen to events directly from mongodb

    private val logger = KotlinLogging.logger {}

    override fun commandStarted(event: CommandStartedEvent) {
        if (event.commandName == "insert") {
            if (event.command["insert"]!!.asString().value == "movie") {
                postEventToQueue(
                    MOVIE_QUEUE,
                    event.command["documents"]!!.asArray()[0].asDocument()["tconst"]!!.asString().value
                )
            } else if (event.command["insert"]!!.asString().value == "user") {
                postEventToQueue(
                    USER_QUEUE,
                    event.command["documents"]!!.asArray()[0].asDocument()["userid"]!!.asInt64().value
                )
            }
        }
    }

    private fun postEventToQueue(queueName: String, queueElement: Any) {
        try {
            jmsTemplate.convertAndSend(queueName, queueElement)
        } catch (e: Exception) {
            logger.error(e) { "Error occured when writing event to queue" }
        }
    }
}
