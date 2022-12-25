import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("data", "$name.txt")
    .readLines()

fun readText(name: String) = File("data", "$name.txt")
    .readText()

fun randomString(length: Int): String {
    val allowedChars = ('A'..'Z') + ('a'..'z')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

fun isNumeric(toCheck: String): Boolean {
    return toCheck.all { char -> char.isDigit() }
}
/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)
