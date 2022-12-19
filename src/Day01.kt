import java.io.File

fun main() {
    fun caloriesForElf(lines: String): Int {
        return lines.split("\n").sumOf { it.toInt() }
    }

    fun part1(input: String): Int {
        return input.split("\n\n")
            .maxOf { caloriesForElf(it) }
    }

    fun part2(input: String): Int {
        return input
            .split("\n\n")
            .map { caloriesForElf(it) }
            .sortedDescending()
            .take(3)
            .reduce { a, b -> a + b }
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readText("Day01_test")
    check(part1(testInput) == 24000)

    val input = readText("Day01")
    part1(input).println()
    part2(input).println()
}
