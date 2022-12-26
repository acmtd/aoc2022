import kotlin.math.absoluteValue

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
    val minX: Int,
    val maxX: Int,
    val maxY: Int
) {
    private fun contentAt(p: D15GridPosition): D15GridContent {
        return this.grid.getOrDefault(p, defaultContentForRow(p.y))
    }

    private fun defaultContentForRow(y: Int): D15GridContent {
        return D15GridContent.UNKNOWN
    }

    private fun printGrid() {
        for (y in grid.keys.minOf { it.y }..grid.keys.maxOf { it.y }) {
            print("$y\t")
            for (x in grid.keys.minOf { it.x }..grid.keys.maxOf { it.x }) {
                print(contentAt(D15GridPosition(x, y)).toSymbol())
            }
            println("")
        }
    }

    private fun withinExtents(p: D15GridPosition): Boolean {
        return p.x in (minX..maxX) && p.y in (0..maxY)
    }

    companion object {
        fun of(input: List<String>): D15Grid {
            val map = mutableMapOf<D15GridPosition, D15GridContent>()

            val grid = D15Grid(map, 0, 1, 2)

            input.forEach {
                val sensorPos = D15GridPosition.of(it.substringAfter(" at ").substringBefore(":"))
                val beaconPos = D15GridPosition.of(it.substringAfterLast(" at "))

                // each beacon is the closes possible for that sensor
                // need to mark the sensors on a grid
                map[sensorPos] = D15GridContent.SENSOR
                map[beaconPos] = D15GridContent.BEACON

                // calculate taxi-cab distance between the two
                val distance = sensorPos.distanceFrom(beaconPos)

                // start at the sensor and fan out to the beacon distance
                // this gets unworkable when the distances are very high
                for (dx in -distance until distance) {
                    for (dy in -distance until distance) {
                        val p = D15GridPosition(sensorPos.x + dx, sensorPos.y + dy)
                        if (p.distanceFrom(sensorPos) <= distance && grid.contentAt(p) == D15GridContent.UNKNOWN) {
                            map[p] = D15GridContent.NEITHER
                        }
                    }
                }
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
        return D15Grid.of(input).grid.filter { it.key.y == row }
            .filter { it.value == D15GridContent.NEITHER }
            .count()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput, 10) == 26)

    val input = readInput("Day15")
//    part1(input, 2000000).println()
}
