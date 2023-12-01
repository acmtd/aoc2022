fun main() {

    fun visualize(rows: MutableList<String>) {
        rows.reversed().dropLast(1).forEach {
            println("|$it|")
        }
        println("+-------+")
    }

    fun applyJet(direction: Char, rows: MutableList<String>) {
        if (direction == '<') {
            println("Move left!")
            // jet of gas pushes rock left
            val canMove = rows.filter { row -> row.contains("@") }
                .all { row -> row.startsWith(".") && !row.contains("#@") }

            if (canMove) {
                rows.indices.filter { idx -> rows[idx].contains("@") }
                    .map { idx -> rows[idx] = rows[idx].substring(1) + "." }
            }
        } else {
            println("Move right!")
            // jet of gas pushes rock right
            val canMove = rows.filter { row -> row.contains("@") }
                .all { row -> row.endsWith(".") && !row.contains("@#") }

            if (canMove) {
                rows.indices.filter { idx -> rows[idx].contains("@") }
                    .map { idx -> rows[idx] = "." + rows[idx].substring(0, rows[idx].length - 1) }
            }
        }
    }

    fun part1(input: String, shapes: List<String>): Int {
        val queue = ArrayDeque<Char>()

        val rows = mutableListOf<String>()
        rows.add("-------") // floor

        repeat(5) { round ->
            var shapeToFall = round % shapes.size

            val nextShape = shapes[shapeToFall]

            repeat(3) {
                rows.add(".......")
            }

            val shapeRows = nextShape.split("\n")

            shapeRows.reversed().forEach { line ->
                val leftPart = "..${line.replace("#", "@")}"
                rows.add(leftPart.padEnd(7, '.'))
            }

            println("Round " + round + " release:")
            visualize(rows)

            // now need to flow the moves
            var atRest = false

            while (!atRest) {
//                visualize(rows)

                if (queue.isEmpty()) queue.addAll(input.toList())

                val direction = queue.removeFirst()

                applyJet(direction, rows)

                // now move down, if we can
                val rowIdx = rows.indices.last { idx -> !rows[idx].contains("@") }

                var reachedEnd = false

                if (rows[rowIdx] == ".......") {
                    // blank row, nice and easy
//                    println("Shape moves down 1 unit")
                    rows.removeAt(rowIdx)
                } else if (rows[rowIdx].contains("#")) {
                    val comparison = rows[rowIdx].zip(rows[rowIdx + 1])

                    if (rows[rowIdx + 1].contains("#")) {
                        // can't do a merge as both rows have settled rock, if all @ has air below then let the @ move
                        // into the next row - but then we need to let the next row do the same thing
                        if (comparison.filter { pair -> pair.second == '@' }.all { pair -> pair.first == '.' }) {

                        }
                    } else {
                        // need to merge the two rows
                        // if spots underneath @ are "." then we can let it fall
                        if (comparison.filter { pair -> pair.second == '@' }.all { pair -> pair.first == '.' }) {
                            println("new shape can merge cleanly into the row below")

                            println("Rows before:\n" + rows[rowIdx] + "\n" + rows[rowIdx + 1])

                            rows[rowIdx + 1] =
                                rows[rowIdx].zip(rows[rowIdx + 1]).map { (a, b) -> if (a == '.') b else a }
                                    .joinToString("")

                            println("Row after:\n" + rows[rowIdx + 1])

                            rows.removeAt(rowIdx)
                        } else {
                            reachedEnd = true
                        }
                    }
                } else {
//                    println("hit the floor")
                    reachedEnd = true
                }

//                visualize(rows)

                if (reachedEnd) {
//                    println("Reached floor or rock")
                    rows.indices.filter { idx -> rows[idx].contains("@") }
                        .map { idx -> rows[idx] = rows[idx].replace("@", "#") }

//                    visualize(rows)

                    atRest = true
//                    visualize(rows)
                }
            }
        }

        // subtract one for the floor
        return rows.size - 1
    }

    val shapes = readText("Day17_shapes").split("\n\n")


    val testInput = readText("Day17_test")
    check(part1(testInput, shapes) == 3068)

    val input = readText("Day17")
//    part1(input, shapes).println()

}