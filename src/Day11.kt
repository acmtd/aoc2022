data class Item(var worryLevel: Long) {
    fun operate(op: Operation, test: Long, worryEases: Boolean, modulo: Long): Boolean {
        worryLevel = op.applyTo(worryLevel)

        if (worryEases) {
            worryLevel /= 3
        } else {
            worryLevel %= modulo
        }
        return (worryLevel.mod(test) == 0.toLong())
    }
}

data class Operation(val op: String, val amount: Long?) {
    fun applyTo(worryLevel: Long): Long {
        if (op == "*") return worryLevel * amount!!
        if (op == "+") return worryLevel + amount!!

        return worryLevel * worryLevel
    }

    companion object {
        fun of(str: String): Operation {
            val (op, amount) = str.split(" ")

            if (amount == "old") return Operation("sq", null)
            return Operation(op, amount.toLong())
        }
    }
}

data class Monkey(
    val items: MutableList<Item>,
    var operation: Operation,
    var divisibleTest: Long,
    var throwTo: List<Int>
) {
    var inspectedItems: Long = 0

    fun operate(item: Item, worryEases: Boolean, modulo: Long): Boolean {
        inspectedItems++
        return item.operate(operation, divisibleTest, worryEases, modulo)
    }

    companion object {
        fun of(lines: List<String>): Monkey {
            val startItems = lines[1].substringAfter(": ")
                .split(",")
                .map { Item(it.trim().toLong()) }
                .toMutableList()

            val operation = Operation.of(lines[2].substringAfter("old "))
            val divisibleTest = lines[3].substringAfter("divisible by ").toLong()
            val throwTo = setOf(4, 5)
                .map { lines[it].substringAfter("monkey ").toInt() }

            return Monkey(startItems, operation, divisibleTest, throwTo)
        }
    }
}

fun main() {
    fun processRound(monkeys: List<Monkey>, worryEases: Boolean, modulo: Long) {
        monkeys.forEach { monkey ->
            monkey.items.forEach { item ->
                if (monkey.operate(item, worryEases, modulo)) {
                    monkeys[monkey.throwTo[0]].items.add(item)
                } else {
                    monkeys[monkey.throwTo[1]].items.add(item)
                }
            }

            monkey.items.clear()
        }
    }

    fun part1(input: String): Long {
        val monkeys = input
            .split("\n\n")
            .map { Monkey.of(it.lines()) }

        val modulo = monkeys.map { it.divisibleTest }
            .reduce { a, b -> a * b }

        repeat(20) {
            processRound(monkeys, true, modulo)
        }

        return monkeys.map { it.inspectedItems }.sortedDescending().take(2).reduce { a, b -> a * b }.toLong()
    }

    fun part2(input: String): Long {
        val monkeys = input
            .split("\n\n")
            .map { Monkey.of(it.lines()) }

        val modulo = monkeys.map { it.divisibleTest }
            .reduce { a, b -> a * b }

        repeat(10000) {
            processRound(monkeys, false, modulo)
        }

        return monkeys.map { it.inspectedItems }.sortedDescending().take(2).reduce { a, b -> a * b }.toLong()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readText("Day11_test")
    check(part1(testInput) == 10605.toLong())
    check(part2(testInput) == 2713310158)

    val input = readText("Day11")
    part1(input).println() // 110888
    part2(input).println() // 25590400731
}
