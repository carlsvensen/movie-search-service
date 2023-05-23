package dk.cygni.carlsmoviesearchservice.config

import jakarta.jms.ConnectionFactory
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.config.DefaultJmsListenerContainerFactory
import org.springframework.stereotype.Service
import org.springframework.util.ErrorHandler


@EnableJms
@Configuration
class JMSConfig {

    //@Bean
    fun jmsListenerContainerFactory(
        sampleJmsErrorHandler: SampleJmsErrorHandler,
        @Qualifier("jmsConnectionFactory") connectionFactory: ConnectionFactory
    ): DefaultJmsListenerContainerFactory {
        val factory = DefaultJmsListenerContainerFactory()
        factory.setConnectionFactory(connectionFactory)
        factory.setErrorHandler(sampleJmsErrorHandler)

        return factory
    }
}


//@Service
class SampleJmsErrorHandler : ErrorHandler {
    private val logger = KotlinLogging.logger {}

    override fun handleError(t: Throwable) {
        logger.error {"Error Message : ${t.message}, Exception: $t" }
    }
}