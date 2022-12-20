fun main() {
    fun splitIntoCompartments(str: String): List<String> {
        return listOf(
            str.substring(0, str.length / 2),
            str.substring(str.length / 2)
        )
    }

    fun commonItems(list: List<String>): Set<Char> {
        // given a list of strings, find any characters that appear in all the items
        var commonChars = list.take(1)[0].toSet()

        list.forEach {
            commonChars = commonChars.intersect(it.toSet())
        }

        return commonChars
    }

    fun valueOfItem(item: Char): Int {
        // ascii a-z runs from 97-122, we need 1-26
        // ascii A-Z runs from 65-90, we need 27-52
        if (item >= 'a') return item.code - 96

        return item.code - 38
    }

    fun part1(input: List<String>): Int {
        return input.map { splitIntoCompartments(it) }
            .map { commonItems(it).first() }
            .sumOf { valueOfItem(it) }
    }

    fun part2(input: List<String>): Int {
        return input.chunked(3)
            .map { commonItems(it).first() }
            .sumOf { valueOfItem(it) }
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
