fun main() {
    fun String.toRange(): IntRange {
        val split = this.split("-")
        return IntRange(split[0].toInt(), split[1].toInt())
    }

    fun convertToRanges(str: String): Pair<IntRange, IntRange> {
        val split = str.split(",")

        return Pair(split[0].toRange(), split[1].toRange())
    }

    fun rangesFullyOverlap(ranges: Pair<IntRange, IntRange>): Boolean {
        val intersect = ranges.first.intersect(ranges.second)

        return (intersect.containsAll(ranges.first.toSet()) || intersect.containsAll(ranges.second.toSet()))
    }

    fun rangesPartiallyOverlap(ranges: Pair<IntRange, IntRange>): Boolean {
        return ranges.first.intersect(ranges.second).isNotEmpty()
    }

    fun part1(input: List<String>): Int {
        return input.map { convertToRanges(it) }
            .count { rangesFullyOverlap(it) }
    }

    fun part2(input: List<String>): Int {
        return input.map { convertToRanges(it) }
            .count { rangesPartiallyOverlap(it) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
