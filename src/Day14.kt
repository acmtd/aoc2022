data class GridPosition(val x: Int, val y: Int) {
    fun possibleNextPositions() = setOf(GridPosition(x, y + 1), GridPosition(x - 1, y + 1), GridPosition(x + 1, y + 1))

    companion object {
        fun of(str: String): GridPosition {
            val (x, y) = str.split(",")
            return GridPosition(x.toInt(), y.toInt())
        }

        fun returnRange(pair: Pair<GridPosition, GridPosition>): Set<GridPosition> {
            // ensure we have the correct order (second greater than first)
            if (pair.second.x < pair.first.x || pair.second.y < pair.first.y) {
                return returnRange(Pair(pair.second, pair.first))
            }

            return (pair.first.x..pair.second.x)
                .map { x -> (pair.first.y..pair.second.y).map { y -> GridPosition(x, y) } }
                .flatten().toSet()
        }
    }
}

data class Grid(
    val grid: MutableMap<GridPosition, GridContent>,
    val minX: Int,
    val maxX: Int,
    val maxY: Int,
    val hasFloor: Boolean
) {
    private fun contentAt(p: GridPosition): GridContent {
        return this.grid.getOrDefault(p, defaultContentForRow(p.y))
    }

    private fun defaultContentForRow(y: Int): GridContent {
        if (y == maxY && hasFloor) return GridContent.FLOOR
        return GridContent.AIR
    }

    private fun nextPosition(p: GridPosition): GridPosition? {
        return p.possibleNextPositions().firstOrNull { contentAt(it) == GridContent.AIR }
    }

    private fun cellsWith(c: GridContent) = grid.values.count { it == c }

    private fun flowSand() {
        val origin = GridPosition(500, 0)

        while (true) {
            var p = origin

            while (contentAt(p) == GridContent.AIR) {
                p = nextPosition(p) ?: break

                if (!withinExtents(p)) return
            }

            this.grid[p] = GridContent.SAND
            if (p == origin) return
        }
    }

    private fun printGrid() {
        val minX = grid.keys.minOf { it.x }
        val maxX = grid.keys.maxOf { it.x }

        for (y in 0..maxY) {
            for (x in minX..maxX) {
                print(contentAt(GridPosition(x, y)).toSymbol())
            }
            println("")
        }
    }

    private fun withinExtents(p: GridPosition): Boolean {
        return p.x in (minX..maxX) && p.y in (0..maxY)
    }

    fun runSimulation(): Int {
        flowSand()

        println("Final grid")
        printGrid()

        return cellsWith(GridContent.SAND)
    }

    companion object {
        fun of(input: List<String>): Grid {
            val rockPattern = getRockPattern(input)

            // get the min/max extents -we know minY is zero as that's where the sand starts but
            // need to get the other extents
            val minX = rockPattern.keys.minOf { it.x }
            val maxX = rockPattern.keys.maxOf { it.x }
            val maxY = rockPattern.keys.maxOf { it.y }

            return Grid(rockPattern.toMutableMap(), minX, maxX, maxY, false)
        }

        fun ofPart2(input: List<String>): Grid {
            val rockPattern = getRockPattern(input)

            // as defined by puzzle, floor is at max(y) + 2 and x-axis extends infinitely
            // this is essentially just allowing any sand to settle at max(y) + 1
            val minX = Integer.MIN_VALUE
            val maxX = Integer.MAX_VALUE
            val maxY = rockPattern.keys.maxOf { it.y } + 2

            return Grid(rockPattern.toMutableMap(), minX, maxX, maxY, true)
        }

        private fun getRockPattern(input: List<String>): Map<GridPosition, GridContent> {
            val rockPattern = input.map {
                it.split(" -> ")
                    .map { pos -> GridPosition.of(pos) }
                    .zipWithNext()
            }
                .flatten()
                .flatMap { pair -> GridPosition.returnRange(pair) }
                .toSet()
                .associateWith { GridContent.ROCK }
            return rockPattern
        }
    }
}

enum class GridContent {
    ROCK, AIR, SAND, FLOOR;

    fun toSymbol(): String {
        if (this == ROCK || this == FLOOR) return "#"
        if (this == SAND) return "o"
        return "."
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return Grid.of(input).runSimulation()
    }

    fun part2(input: List<String>): Int {
        return Grid.ofPart2(input).runSimulation()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 24)
    check(part2(testInput) == 93)

    val input = readInput("Day14")
    part1(input).println() // 901
    part2(input).println() // 24589
}
