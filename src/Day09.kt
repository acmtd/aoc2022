import kotlin.math.absoluteValue
import kotlin.math.sign

data class Move(val length: Int, val x: Int, val y: Int) {
    companion object {
        fun of(line: String): Move {
            val (dir, amount) = line.split(" ")

            return when (dir) {
                "R" -> Move(amount.toInt(), 1, 0)
                "L" -> Move(amount.toInt(), -1, 0)
                "D" -> Move(amount.toInt(), 0, -1)
                else -> Move(amount.toInt(), 0, 1)
            }
        }
    }
}

data class Knot(val name: String, val child: Knot?) {
    val visitedPositions = mutableSetOf("[0,0]")
    private var x = 0
    private var y = 0

    fun move(move: Move) {
        for (i in 1..move.length) {
            translate(move.x, move.y)
        }
    }

    private fun translate(deltaX: Int, deltaY: Int) {
        x += deltaX
        y += deltaY

        visitedPositions.add("[$x,$y]")
        child?.keepUp(this)
    }

    private fun keepUp(end: Knot) {
        val deltaX = (this.x - end.x)
        val deltaY = (this.y - end.y)

        // either in same position or adjacent horizontally/vertically
        if ((deltaX.absoluteValue + deltaY.absoluteValue <= 1)) return

        // diagonally adjacent
        if (deltaX.absoluteValue == 1 && deltaY.absoluteValue == 1) return

        // not adjacent so need to make a one-step move to get closer
        translate(-deltaX.sign, -deltaY.sign)
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val moves = input.map { Move.of(it) }

        val tail = Knot("Tail", null)
        val head = Knot("Head", tail)

        moves.forEach { head.move(it) }

        return tail.visitedPositions.size
    }

    fun part2(input: List<String>): Int {
        val moves = input.map { Move.of(it) }

        val knots = linkedSetOf<Knot>()

        (10 downTo 1).map { knots.add(Knot("K${it - 1}", knots.lastOrNull())) }

        moves.forEach { knots.last().move(it) }

        return knots.first().visitedPositions.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    val testInput2 = readInput("Day09_test2")
    check(part1(testInput) == 13)
    check(part2(testInput) == 1)
    check(part2(testInput2) == 36)

    val input = readInput("Day09")
    part1(input).println() // 6391
    part2(input).println()
}
