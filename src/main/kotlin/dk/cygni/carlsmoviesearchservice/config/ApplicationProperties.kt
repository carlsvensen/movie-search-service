package dk.cygni.carlsmoviesearchservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "dk.cygni.carlsmoviesearchservice.queuename")
class QueueProperties {
    lateinit var movie: String
    lateinit var user: String
}

@Component
@ConfigurationProperties(prefix = "dk.cygni.carlsmoviesearchservice.filelocation")
class FilenameProperties {
    lateinit var movies: String
    lateinit var ratings: String
}