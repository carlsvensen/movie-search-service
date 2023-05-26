package dk.cygni.moviesearchservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class MovieSearchServiceApplication

fun main(args: Array<String>) {
    runApplication<MovieSearchServiceApplication>(*args)
}
