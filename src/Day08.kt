fun main() {
    fun makeTreeMatrix(input: List<String>): List<List<Int>> {
        return input.map { it.toList().map { ch -> ch.digitToInt() } }
    }

    fun isVisible(trees: List<Int>, height: Int): Boolean {
        return !trees.any { it >= height }
    }

    fun score(trees: List<Int>, height: Int): Int {
        var counter = 0
        for (tree in trees) {
            counter++
            if (tree >= height) break
        }

        return counter
    }

    fun treeLists(allTrees: List<List<Int>>, x: Int, y: Int) = listOf(
        allTrees.subList(0, x).asReversed().map { it[y] },
        allTrees.subList(x + 1, allTrees.size).map { it[y] },
        allTrees[x].subList(0, y).asReversed(),
        allTrees[x].subList(y + 1, allTrees.size)
    )

    fun getHighestScenicScore(allTrees: List<List<Int>>): Int {
        var highScore = 0

        // by using 1 and until we don't have to examine the edges (which have a zero score)
        for (x in 1 until allTrees.size) {
            for (y in 1 until allTrees.size) {
                val combinedScore = treeLists(allTrees, x, y)
                    .map { score(it, allTrees[x][y]) }
                    .reduce { a, b -> a * b }

                if (combinedScore > highScore) highScore = combinedScore
            }
        }
        return highScore
    }

    fun countVisible(allTrees: List<List<Int>>): Int {
        val maxIndex = allTrees.size - 1

        var counter = 0

        for (x in allTrees.indices) {
            for (y in allTrees[x].indices) {
                // edge trees are always visible
                if (x == 0 || x == maxIndex || y == 0 || y == maxIndex) {
                    counter++
                } else {
                    if (treeLists(allTrees, x, y).map { isVisible(it, allTrees[x][y]) }.any { it }) counter += 1
                }
            }
        }
        return counter
    }

    fun part1(input: List<String>): Int {
        return countVisible(makeTreeMatrix(input))
    }

    fun part2(input: List<String>): Int {
        return getHighestScenicScore(makeTreeMatrix(input))
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 8)

    val input = readInput("Day08")
    part1(input).println() // 1733
    part2(input).println() // 284648
}
