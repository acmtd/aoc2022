enum class CompareResult {
    RIGHT_ORDER, WRONG_ORDER, SAME
}

data class ListStruct(var group: String, var data: MutableMap<String, String>) {
    fun items() = groupAsString().split(",").filter { it.isNotBlank() }
    fun forGroup(otherGroup: String) = ListStruct(otherGroup, this.data)
    private fun groupAsString() = data.getOrDefault(group, "")
}

fun main() {
    val regex = "\\[([A-Za-z0-9,]*)]".toRegex()

    fun makeGroup(groups: MutableMap<String, String>, str: String): String {
        val groupName = randomString(4)
        groups[groupName] = str
        return groupName
    }

    fun assemble(str: String, groups: MutableMap<String, String>): String {
        val matchResult = regex.findAll(str)

        var newString = str

        matchResult.forEach {
            val groupName = makeGroup(groups, it.groupValues[1])
            newString = newString.replace(it.groupValues[0], groupName)
        }

        if (str == newString || !newString.contains("[")) return newString

        return assemble(newString, groups)
    }

    fun parse(initialGroup: String): ListStruct {
        val map = mutableMapOf<String, String>()
        val topLevelID = assemble(initialGroup, map)
        return ListStruct(topLevelID, map)
    }

    fun compare(left: ListStruct, right: ListStruct): CompareResult {
        val leftQueue = ArrayDeque(left.items())
        val rightQueue = ArrayDeque(right.items())

        println("Compare L: $leftQueue with R: $rightQueue")

        while (leftQueue.isNotEmpty() && rightQueue.isNotEmpty()) {
            val leftItem = leftQueue.removeFirst()
            val rightItem = rightQueue.removeFirst()

            if (isNumeric(leftItem) && isNumeric(rightItem)) {
                println("- Compare $leftItem vs $rightItem")
                if (leftItem.toInt() < rightItem.toInt()) {
                    println("\t- Left side is smaller, so inputs are in the right order")
                    return CompareResult.RIGHT_ORDER
                } else if (leftItem.toInt() > rightItem.toInt()) {
                    println("\t- Right side is smaller, so inputs are not in the right order")
                    return CompareResult.WRONG_ORDER
                }
            } else {
                val newLeft = if (isNumeric(leftItem)) {
                    println("Construct new group for numeric left item $leftItem")
                    ListStruct(makeGroup(left.data, leftItem), left.data)
                } else {
                    left.forGroup(leftItem)
                }

                val newRight = if (isNumeric(rightItem)) {
                    println("Construct new group for numeric right item $rightItem")
                    ListStruct(makeGroup(right.data, rightItem), right.data)
                } else {
                    right.forGroup(rightItem)
                }

                val listCompareResult = compare(newLeft, newRight)
                if (listCompareResult != CompareResult.SAME) return listCompareResult
            }
        }

        // if we get here, at least oe of the queues is empty
        if (rightQueue.size > 0) {
            println("\t- Left side ran out of items, so inputs are in the right order")
            return CompareResult.RIGHT_ORDER
        } else if (leftQueue.size > 0) {
            println("\t- Right side ran out of items, so inputs are not in the right order")
            return CompareResult.WRONG_ORDER
        }

        // this should not really happen - it implies both items evaluated as being equal
        return CompareResult.SAME
    }

    fun processBlock(index: Int, blocks: List<String>): Pair<Int, CompareResult> {
        println("== PAIR ${index + 1} ==")
        println("- Compare ${blocks[index].replace("\n", " vs ")}")
        val (left, right) = blocks[index].split("\n").map { str -> parse(str) }

        var result = compare(left, right)
        println("- Outcome:$result")
        return Pair(index + 1, result)
    }

    fun part1(input: String): Int {
        val blocks = input.split("\n\n")

        return blocks.indices
            .map { idx -> processBlock(idx, blocks) }
            .filter { it.second == CompareResult.RIGHT_ORDER }
            .sumOf { it.first }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readText("Day13_test")
    check(part1(testInput) == 13)

    val input = readText("Day13")
    part1(input).println()
//    part2(input).println()
}
