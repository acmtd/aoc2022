fun main() {
    // construct a timeline that maps a cycle number to the
    // value of the x register at that time
    val timeline = hashMapOf<Int, Int>()

    fun processInstructions(input: List<String>) {
        timeline.clear() // ensure data is cleared between parts/tests

        var cycle = 1
        var register = 1
        timeline[cycle] = register

        input.forEach {
            when (it) {
                "noop" -> {
                    cycle++
                }

                else -> {
                    val (_, increment) = it.split(" ")

                    register += increment.toInt()
                    cycle += 2

                    timeline[cycle] = register
                }
            }
        }
    }

    fun registerValue(cycle: Int): Int {
        return timeline[cycle] ?: registerValue(cycle - 1)
    }

    fun part1(input: List<String>): Int {
        processInstructions(input)

        return listOf(20, 60, 100, 140, 180, 220).sumOf { it * registerValue(it) }
    }

    fun part2(input: List<String>) {
        processInstructions(input)

        (0 until 240).forEach {
            val rowPosition = it % 40

            val spriteMiddle = registerValue(it + 1)
            val visible = rowPosition in (spriteMiddle - 1..spriteMiddle + 1)

            print(if (visible) "#" else ".")

            if (rowPosition == 39) println("")
        }
    }

    val testInput = readInput("Day10_test")
    check(part1(testInput) == 13140)
    part2(testInput)

    val input = readInput("Day10")
    part1(input).println() // 14760
    part2(input) // EFGERURE
}
