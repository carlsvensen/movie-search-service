package dk.cygni.carlsmoviesearchservice.service

import dk.cygni.carlsmoviesearchservice.aggregate.MovieAggregate
import dk.cygni.carlsmoviesearchservice.commands.CreateMovieCommand
import dk.cygni.carlsmoviesearchservice.commands.UpdateRatingCommand
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

@Service
class FileReaderService(
    private val movieAggregate: MovieAggregate
) {
    private val logger = KotlinLogging.logger {}

    @Value(value = "#{filenameProperties.movies}")
    private lateinit var movieFilepath: String

    @Value(value = "#{filenameProperties.ratings}")
    private lateinit var ratingFilepath: String

    private val readingMovieFile: AtomicBoolean = AtomicBoolean(false)
    private var counterMovie: Int = 0
    private var startTimeMovie: Long = 0
    private var startTimeBatchMovie: Long = 0

    private val readingRatingFile: AtomicBoolean = AtomicBoolean(false)
    private var counterRating: Int = 0
    private var startTimeRating: Long = 0
    private var startTimeBatchRating: Long = 0

    fun readMovieFile() {
        if (!readingMovieFile.compareAndSet(false, true)) {
            throw IllegalArgumentException("Already reading movie file")
        }

        thread(start = true) {
            try {
                startTimeMovie = System.currentTimeMillis()
                startTimeBatchMovie = System.currentTimeMillis()
                File(movieFilepath).readLines().drop(1).forEach { line -> handleMovieLine(line) }
                logger.info { "Finished reading $counterMovie movies in ${(System.currentTimeMillis() - startTimeMovie) / 1000} sec" }

            } catch (e: Exception) {
                logger.error { "Error while reading movie file ${e.message}" }

            } finally {
                readingMovieFile.compareAndSet(true, false)
            }
        }
    }

    fun readRatingFile() {
        if (!readingRatingFile.compareAndSet(false, true)) {
            throw IllegalArgumentException("Already reading rating file")
        }

        thread(start = true) {
            try {
                startTimeRating = System.currentTimeMillis()
                startTimeBatchRating = System.currentTimeMillis()
                File(ratingFilepath).readLines().drop(1).forEach { line -> handleRatingLine(line) }
                logger.info { "Finished reading $counterRating ratings in ${(System.currentTimeMillis() - startTimeRating) / 1000} sec" }

            } catch (e: Exception) {
                logger.error { "Error while reading rating file ${e.message}" }

            } finally {
                readingRatingFile.set(false)
            }
        }
    }

    private fun handleMovieLine(line: String) {
        counterMovie++
        val seperatedLine = line.split("\t")

        movieAggregate.handleUpdateMovie(
            CreateMovieCommand(
                tconst = seperatedLine[0],
                titleType = readLineAndDiscardEmptyPlaceholder(seperatedLine[1]),
                primaryTitle = readLineAndDiscardEmptyPlaceholder(seperatedLine[2]),
                originalTitle = readLineAndDiscardEmptyPlaceholder(seperatedLine[3]),
                isAdult = seperatedLine[4] == "1",
                startYear = readLineAndDiscardEmptyPlaceholder(seperatedLine[5]),
                endYear = readLineAndDiscardEmptyPlaceholder(seperatedLine[6]),
                runtimeMinutes = convertRuntimeMinutes(seperatedLine[7]),
                genres = seperatedLine[8].split(",").map { readLineAndDiscardEmptyPlaceholder(it) }
            )
        )
        if (counterMovie % 1000 == 0) {
            logger.info {
                "Handled 1000 rows of movies in ${System.currentTimeMillis() - startTimeBatchMovie} ms, " +
                        "total count $counterMovie, total time ${(System.currentTimeMillis() - startTimeMovie) / 1000} sec"
            }
            startTimeBatchMovie = System.currentTimeMillis()
        }
    }

    private fun handleRatingLine(line: String) {
        counterRating++
        val seperatedLine = line.split("\t")

        movieAggregate.handleUpdateRating(
            UpdateRatingCommand(
                seperatedLine[0], seperatedLine[1].toDouble(), seperatedLine[2].toLong()
            )
        )

        if (counterRating % 1000 == 0) {
            logger.info {
                "Handled 1000 rows of ratings in ${System.currentTimeMillis() - startTimeBatchRating} ms, " +
                        "total count $counterRating, total time ${(System.currentTimeMillis() - startTimeRating) / 1000} sec"
            }
            startTimeBatchRating = System.currentTimeMillis()
        }
    }

    private fun readLineAndDiscardEmptyPlaceholder(value: String): String = if (value == "\\N") "" else value

    private fun convertRuntimeMinutes(runtimeString: String?): Int? =
        try {
            runtimeString?.toInt()
        } catch (e: NumberFormatException) {
            null
        }
}