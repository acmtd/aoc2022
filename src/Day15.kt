import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

data class Exclusion(val pos: D15GridPosition, val dist: Int) {
    private fun minX() = pos.x - dist
    private fun maxX() = pos.x + dist
    private fun rangesForDistance(d: Int, minValue: Int, maxValue: Int): Set<D15GridRange> {
        val xRange = (max(minValue, minX() + d)..(min(maxValue, maxX() - d)))

        return setOf(
            D15GridRange(xRange, pos.y - d),
            D15GridRange(xRange, pos.y + d)
        )
    }

    fun xRanges(min: Int, max: Int): Set<D15GridRange> {
        return (0..dist).flatMap { rangesForDistance(it, min, max) }.toSet()
    }
}

data class D15GridPosition(val x: Int, val y: Int) {
    fun distanceFrom(otherPos: D15GridPosition) = (x - otherPos.x).absoluteValue + (y - otherPos.y).absoluteValue

    companion object {
        fun of(str: String): D15GridPosition {
            val (x, y) = str.split(",")
                .map { x -> x.replace("[xy= ]".toRegex(), "").toInt() }

            return D15GridPosition(x, y)
        }
    }
}

data class D15GridRange(val xRange: IntRange, val yPos: Int)

data class D15Grid(
    val grid: MutableMap<D15GridPosition, D15GridContent>,
    val exclusionDistances: Set<Exclusion>
) {
    fun calculateExclusionsForRow(row: Int): Set<D15GridRange> {
        return exclusionDistances
            .filter { (it.pos.y - row).absoluteValue <= it.dist }
            .map { exclusionPositionsForRow(it, row) }
            .toSet()
    }

    private fun exclusionPositionsForRow(it: Exclusion, y: Int): D15GridRange {
        // can this exclusion affect this row, and if so, on what columns
        val dy = (it.pos.y - y).absoluteValue

        // if the exclusion is on the same row the possible x values are -dist to dist
        // for every row distant, those exclusions get one row narrower
        val maxDX = (it.dist - dy)

        return D15GridRange((it.pos.x - maxDX..it.pos.x + maxDX), y)
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

fun firstNonContiguous(entry: Map.Entry<Int, MutableSet<D15GridRange>>, maxCoord: Int): D15GridPosition? {
    val set = entry.value.sortedBy { it.xRange.first() }.toMutableList()

    var target = 0

    while (set.isNotEmpty()) {
        val item = set.removeFirst()
        if (item.xRange.first() > target) return D15GridPosition(target, item.yPos)

        target = max(target, item.xRange.last() + 1)
    }

    // at this point there are no more items left - it's theoretically possible though that
    // there could be open space beyond the last item
    if (target >= maxCoord) return null

    return D15GridPosition(target, set.first().yPos)
}

enum class D15GridContent {
    SENSOR, BEACON, NEITHER;
}

fun main() {
    fun part1(input: List<String>, y: Int): Int {
        val grid = D15Grid.of(input)

        grid.calculateExclusionsForRow(y).forEach { range ->
            range.xRange
                .map { D15GridPosition(it, y) }
                .filter { !grid.grid.keys.contains(it) }
                .forEach { grid.grid[it] = D15GridContent.NEITHER }
        }

        return grid.grid.filter { it.key.y == y }
            .filter { it.value == D15GridContent.NEITHER }
            .count()
    }

    fun part2(input: List<String>, maxCoord: Int): Long {
        val grid = D15Grid.of(input)

        val rangeMap = mutableMapOf<Int, MutableSet<D15GridRange>>()

        grid.exclusionDistances.flatMap {
            it.xRanges(0, maxCoord).filter { range -> range.yPos in (0..maxCoord) }
        }.forEach { r ->
            val set = rangeMap[r.yPos] ?: mutableSetOf()
            set.add(r)
            rangeMap[r.yPos] = set
        }

        val position = rangeMap.entries.firstNotNullOf { firstNonContiguous(it, maxCoord) }
        return (position.x.toLong() * 4000000.toLong()) + position.y.toLong()
    }

    val testInput = readInput("Day15_test")
    check(part1(testInput, 10) == 26)
    check(part2(testInput, 20) == 56000011.toLong())

    val input = readInput("Day15")
    part1(input, 2000000).println() // 5112034
    part2(input, 4000000).println()  // 13172087230812
}
