fun main() {
    fun initializeStacks(parts: List<String>): ArrayList<List<String>> {
        val stackData = parts[0].split("\n")
            .dropLast(1)
            .asReversed()
            .map { it.chunked(4) }
            .toList()

        val stacks = arrayListOf<List<String>>()

        for (i in 1..stackData.first().size) {
            stacks.add(ArrayList())
        }

        stackData.forEach {
            for (i in it.indices) {
                if (it[i].trim().isNotEmpty()) stacks[i] = stacks[i].plus(it[i].substring(1, 2))
            }
        }

        return stacks
    }

    fun processMove(stacks: ArrayList<List<String>>, move: String, reverseOrder: Boolean) {
        val regex = "move (\\d+) from (\\d+) to (\\d+)".toRegex()
        val matchResult = regex.find(move)

        if (matchResult != null) {
            val numberToMove = matchResult.groupValues[1].toInt()
            val fromStack = matchResult.groupValues[2].toInt()
            val toStack = matchResult.groupValues[3].toInt()

            var cratesToTransfer = stacks[fromStack - 1].takeLast(numberToMove)

            if (reverseOrder) cratesToTransfer = cratesToTransfer.asReversed()

            stacks[fromStack - 1] = stacks[fromStack - 1].dropLast(numberToMove)
            stacks[toStack - 1] = stacks[toStack - 1].plus(cratesToTransfer)
        }
    }

    fun part1(input: String): String {
        val parts = input.split("\n\n")
        val stacks = initializeStacks(parts)

        parts[1].split("\n").forEach { processMove(stacks, it, true) }

        return stacks.joinToString("") { it.topItem().toString() }
    }

    fun part2(input: String): String {
        val parts = input.split("\n\n")
        val stacks = initializeStacks(parts)

        parts[1].split("\n").forEach { processMove(stacks, it, false) }

        return stacks.joinToString("") { it.topItem().toString() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readText("Day05_test")
    check(part1(testInput) == "CMZ")
    check(part2(testInput) == "MCD")

    val input = readText("Day05")
    part1(input).println()
    part2(input).println()
}

private fun <E> List<E>.topItem(): Any? {
    if (this.isEmpty()) return " "
    return this.last()
}
