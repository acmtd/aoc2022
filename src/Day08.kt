fun main() {
    fun constructMatrix(input: List<String>): List<List<Int>> {
        return input.map { it.toList().map { ch -> ch.digitToInt() } }
    }

    fun visibleFromTop(matrix: List<List<Int>>, treeI: Int, treeJ: Int): Boolean {
        for (i in 0 until treeI) {
            if (matrix[i][treeJ] >= matrix[treeI][treeJ]) return false
        }

        return true
    }

    fun visibleFromBottom(matrix: List<List<Int>>, treeI: Int, treeJ: Int): Boolean {
        for (i in treeI + 1 until matrix.size) {
            if (matrix[i][treeJ] >= matrix[treeI][treeJ]) return false
        }

        return true
    }

    fun visibleFromLeft(matrix: List<List<Int>>, treeI: Int, treeJ: Int): Boolean {
        for (j in 0 until treeJ) {
            if (matrix[treeI][j] >= matrix[treeI][treeJ]) return false
        }

        return true
    }

    fun visibleFromRight(matrix: List<List<Int>>, treeI: Int, treeJ: Int): Boolean {
        for (j in treeJ + 1 until matrix.size) {
            if (matrix[treeI][j] >= matrix[treeI][treeJ]) return false
        }

        return true
    }

    fun topScore(matrix: List<List<Int>>, treeI: Int, treeJ: Int): Int {
        var counter = 0
        for (i in treeI - 1 downTo 0) {
            counter++
            if (matrix[i][treeJ] >= matrix[treeI][treeJ]) break
        }

        return counter
    }

    fun bottomScore(matrix: List<List<Int>>, treeI: Int, treeJ: Int): Int {
        var counter = 0
        for (i in treeI + 1 until matrix.size) {
            counter++
            if (matrix[i][treeJ] >= matrix[treeI][treeJ]) break
        }

        return counter
    }

    fun leftScore(matrix: List<List<Int>>, treeI: Int, treeJ: Int): Int {
        var counter = 0
        for (j in treeJ - 1 downTo 0) {
            counter++
            if (matrix[treeI][j] >= matrix[treeI][treeJ]) break
        }

        return counter
    }


    fun rightScore(matrix: List<List<Int>>, treeI: Int, treeJ: Int): Int {
        var counter = 0
        for (j in treeJ + 1 until matrix.size) {
            counter++
            if (matrix[treeI][j] >= matrix[treeI][treeJ]) break
        }
        return counter
    }

    fun getHighestScenicScore(matrix: List<List<Int>>): Int {
        var highScore = 0

        // by using 1 and until we don't have to examine the edges (which have a zero score)
        for (i in 1 until matrix.size) {
            for (j in 1 until matrix.size) {
                val score = leftScore(matrix, i, j) * rightScore(matrix, i, j) *
                        topScore(matrix, i, j) * bottomScore(matrix, i, j)

                if (score > highScore) highScore = score
            }
        }
        return highScore
    }

    fun countVisible(matrix: List<List<Int>>): Int {
        val maxIndex = matrix.size - 1

        var counter = 0

        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                // edge trees are always visible
                if (i == 0 || i == maxIndex || j == 0 || j == maxIndex) {
                    counter++
                } else {
                    if (visibleFromLeft(matrix, i, j)) {
                        counter++
                    } else if (visibleFromRight(matrix, i, j)) {
                        counter++
                    } else if (visibleFromTop(matrix, i, j)) {
                        counter++
                    } else if (visibleFromBottom(matrix, i, j)) {
                        counter++
                    }
                }
            }
        }
        return counter
    }

    fun part1(input: List<String>): Int {
        val matrix = constructMatrix(input)
        return countVisible(matrix)
    }

    fun part2(input: List<String>): Int {
        val matrix = constructMatrix(input)
        return getHighestScenicScore(matrix)
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 8)

    val input = readInput("Day08")
    part1(input).println() // 1733
    part2(input).println() // 284648
}
