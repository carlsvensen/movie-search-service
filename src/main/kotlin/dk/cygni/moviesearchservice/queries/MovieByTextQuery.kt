package dk.cygni.moviesearchservice.queries

data class MovieByTextQuery(
    val userid: Long,
    val searchString: String
)
