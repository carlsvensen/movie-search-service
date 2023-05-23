package dk.cygni.carlsmoviesearchservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class CarlsMovieSearchServiceApplication

fun main(args: Array<String>) {
    runApplication<CarlsMovieSearchServiceApplication>(*args)
}
