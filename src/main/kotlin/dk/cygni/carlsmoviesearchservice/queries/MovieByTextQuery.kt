package dk.cygni.carlsmoviesearchservice.queries

data class MovieByTextQuery(
    val userid: Long,
    val searchString: String
)
