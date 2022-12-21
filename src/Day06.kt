fun main() {
    fun detectUniqueSequence(input: String, length: Int): Int {
        var startIndex = 0

        while (input.substring(startIndex, startIndex + length).toList().distinct().size < length) startIndex++

        return startIndex + length
    }

    fun part1(input: String): Int {
        return detectUniqueSequence(input, 4)
    }

    fun part2(input: String): Int {
        return detectUniqueSequence(input, 14)
    }

    // test if implementation meets criteria from the description, like:
    val testInputs = arrayOf(
        "mjqjpqmgbljsphdztnvjfqwrcgsmlb",
        "bvwbjplbgvbhsrlpgdmjqwftvncz",
        "nppdvjthqldpwncqszvftbrmjlhg",
        "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg",
        "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw"
    )

    val testOutputsPart1 = arrayOf(7, 5, 6, 10, 11)
    val testOutputsPart2 = arrayOf(19, 23, 23, 29, 26)

    testInputs.indices.forEach { check(part1(testInputs[it]) == testOutputsPart1[it]) }
    testInputs.indices.forEach { check(part2(testInputs[it]) == testOutputsPart2[it]) }

    val input = readText("Day06")
    part1(input).println()
    part2(input).println()
}
