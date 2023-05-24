package dk.cygni.carlsmoviesearchservice.service

import dk.cygni.carlsmoviesearchservice.aggregate.MovieAggregate
import dk.cygni.carlsmoviesearchservice.commands.movie.CreateMovieCommand
import dk.cygni.carlsmoviesearchservice.commands.movie.UpdateRatingCommand
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

@Service
class FileReaderService(private val movieAggregate: MovieAggregate) {

    private val logger = KotlinLogging.logger {}

    private val movieState: FileReaderState = FileReaderState("MOVIEFILE")
    private val ratingState: FileReaderState = FileReaderState("RATINGFILE")

    fun readMovieFile(filename: String) {
        movieState.startReading()

        thread(start = true) {
            try {
                File("./$filename")
                    .readLines()
                    .drop(1) // Discards headers
                    .forEach { line -> handleMovieLine(line) }

            } catch (e: Exception) {
                logger.error { "Error while reading movie file $filename. Exception: $e" }
                throw e
            } finally {
                movieState.finishReading()
            }
        }
    }

    fun readRatingFile(filename: String) {
        ratingState.startReading()

        thread(start = true) {
            try {
                File("./$filename")
                    .readLines()
                    .drop(1) // Discards headers
                    .forEach { line -> handleRatingLine(line) }

            } catch (e: Exception) {
                logger.error { "Error while reading rating file $filename. Exception: $e" }
                throw e
            } finally {
                ratingState.finishReading()
            }
        }
    }

    private fun handleMovieLine(line: String) {
        line.split("\t").run {
            movieAggregate.handleCreateMovieCommand(
                CreateMovieCommand(
                    tconst = this[0],
                    titleType = readLineAndDiscardEmptyPlaceholder(this[1]),
                    primaryTitle = readLineAndDiscardEmptyPlaceholder(this[2]),
                    originalTitle = readLineAndDiscardEmptyPlaceholder(this[3]),
                    isAdult = this[4] == "1",
                    startYear = readLineAndDiscardEmptyPlaceholder(this[5]),
                    endYear = readLineAndDiscardEmptyPlaceholder(this[6]),
                    runtimeMinutes = convertRuntimeMinutes(this[7]),
                    genres = this[8].split(",").map { readLineAndDiscardEmptyPlaceholder(it) }
                )
            )
        }
        movieState.incrementCounterAndlogProgress()
    }

    private fun handleRatingLine(line: String) {
        line.split("\t").run {
            movieAggregate.handleUpdateRatingCommand(
                UpdateRatingCommand(
                    tconst = this[0],
                    averageRating = this[1].toDouble(),
                    numOfVotes = this[2].toLong()
                )
            )
        }
       ratingState.incrementCounterAndlogProgress()
    }

    private fun readLineAndDiscardEmptyPlaceholder(value: String): String = if (value == "\\N") "" else value

    private fun convertRuntimeMinutes(runtimeString: String?): Int? =
        try {
            runtimeString?.toInt()
        } catch (e: NumberFormatException) {
            null
        }
}

private class FileReaderState(private val filetype: String) {
    private val logger = KotlinLogging.logger {}

    private val readingFile: AtomicBoolean = AtomicBoolean(false)
    private var counter: Int = 0
    private var startTime: Long = 0
    private var startTimeBatch: Long = 0

    fun startReading() {
        if (!readingFile.compareAndSet(false, true)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Already reading file")
        }
        startTime = System.currentTimeMillis()
        startTimeBatch = System.currentTimeMillis()
    }

    fun finishReading() {
        readingFile.compareAndSet(true, false)
        logger.info { "$filetype - Finished reading $counter elements in ${(System.currentTimeMillis() - startTime) / 1000} sec" }
    }

    fun incrementCounterAndlogProgress() {
        counter++
        if (counter % 1000 == 0) {
            logger.info {
                "$filetype - Handled 1000 elements in ${System.currentTimeMillis() - startTimeBatch} ms, " +
                        "total count $counter, total time ${(System.currentTimeMillis() - startTime) / 1000} sec"
            }
            startTimeBatch = System.currentTimeMillis()
        }
    }
}