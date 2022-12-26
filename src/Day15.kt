import kotlin.math.absoluteValue

data class Exclusion(val pos: D15GridPosition, val dist: Int)

data class D15GridPosition(val x: Int, val y: Int) {
    fun distanceFrom(otherPos: D15GridPosition): Int {
        return (x - otherPos.x).absoluteValue + (y - otherPos.y).absoluteValue
    }

    companion object {
        fun of(str: String): D15GridPosition {
            val (x, y) = str.split(",")
                .map { x -> x.replace("[xy= ]".toRegex(), "") }

            return D15GridPosition(x.toInt(), y.toInt())
        }
    }
}

data class D15Grid(
    val grid: MutableMap<D15GridPosition, D15GridContent>,
    val exclusionDistances: Set<Exclusion>
) {
    fun contentAt(p: D15GridPosition): D15GridContent {
        return this.grid.getOrDefault(p, D15GridContent.UNKNOWN)
    }

    fun printGrid(startRow: Int, endRow: Int) {
        for (y in startRow..endRow) {
            print("$y\t")
            for (x in grid.keys.minOf { it.x }..grid.keys.maxOf { it.x }) {
                print(contentAt(D15GridPosition(x, y)).toSymbol())
            }
            println("")
        }
    }

    fun calculateExclusionsForRow(row: Int): Set<D15GridPosition> {
        return exclusionDistances.flatMap { exclusionPositions(it, row) }.toSet()
    }

    private fun exclusionPositions(it: Exclusion, y: Int): List<D15GridPosition> {
        // can this exclusion affect this row, and if so, on what columns
        val dy = (it.pos.y - y).absoluteValue

        if (dy > it.dist) {
            println("Row $y cannot be affected by exclusion zone $it as it is too far away ($dy)")
        }

        // if the exclusion is on the same row the possible x values are -dist to dist
        // for every row distant, those exclusions get one row narrower
        val maxDX = (it.dist - dy)

        return (-maxDX..maxDX).map { dx -> D15GridPosition(it.pos.x + dx, y) }
    }

    companion object {
        fun of(input: List<String>): D15Grid {
            val map = mutableMapOf<D15GridPosition, D15GridContent>()
            val exclusionDistances = mutableSetOf<Exclusion>()

            val grid = D15Grid(map, exclusionDistances)

            input.forEach {
                val sensorPos = D15GridPosition.of(it.substringAfter(" at ").substringBefore(":"))
                val beaconPos = D15GridPosition.of(it.substringAfterLast(" at "))

                // each beacon is the closes possible for that sensor
                // need to mark the sensors on a grid
                map[sensorPos] = D15GridContent.SENSOR
                map[beaconPos] = D15GridContent.BEACON

                // calculate taxi-cab distance between the two
                val distance = sensorPos.distanceFrom(beaconPos)
                exclusionDistances.add(Exclusion(sensorPos, distance))
            }

            return grid
        }
    }
}

enum class D15GridContent {
    SENSOR, BEACON, NEITHER, UNKNOWN;

    fun toSymbol(): String {
        if (this == SENSOR) return "S"
        if (this == BEACON) return "B"
        if (this == NEITHER) return "#"
        return "."
    }
}

fun main() {
    fun part1(input: List<String>, row: Int): Int {
        val grid = D15Grid.of(input)

        grid.calculateExclusionsForRow(row).forEach {
            if (grid.contentAt(it) == D15GridContent.UNKNOWN) grid.grid[it] = D15GridContent.NEITHER
        }

        return grid.grid.filter { it.key.y == row }
            .filter { it.value == D15GridContent.NEITHER }
            .count()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput, 10) == 26)

    val input = readInput("Day15")
    part1(input, 2000000).println() // 5112034
}
