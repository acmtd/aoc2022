data class Item(var worryLevel: Int) {
    fun operate(op: Operation, test: Int, worryEases: Boolean): Boolean {
        worryLevel = op.applyTo(worryLevel)
        if (worryEases) worryLevel /= 3
        return (worryLevel % test == 0)
    }
}

data class Operation(val op: String, val amount: Int?) {
    fun applyTo(worryLevel: Int): Int {
        if (op == "*") return worryLevel * amount!!
        if (op == "+") return worryLevel + amount!!

        return worryLevel * worryLevel
    }

    companion object {
        fun of(str: String): Operation {
            val (op, amount) = str.split(" ")

            if (amount == "old") return Operation("sq", null)
            return Operation(op, amount.toInt())
        }
    }
}

data class Monkey(
    val items: MutableList<Item>,
    var operation: Operation,
    var divisibleTest: Int,
    var throwTo: List<Int>
) {
    var inspectedItems = 0

    fun operate(item: Item, worryEases: Boolean): Boolean {
        inspectedItems++
        return item.operate(operation, divisibleTest, worryEases)
    }

    companion object {
        fun of(lines: List<String>): Monkey {
            val startItems = lines[1].substringAfter(": ")
                .split(",")
                .map { Item(it.trim().toInt()) }
                .toMutableList()

            val operation = Operation.of(lines[2].substringAfter("old "))
            val divisibleTest = lines[3].substringAfter("divisible by ").toInt()
            val throwTo = setOf(4, 5)
                .map { lines[it].substringAfter("monkey ").toInt() }

            return Monkey(startItems, operation, divisibleTest, throwTo)
        }
    }
}

fun main() {
    fun processRound(monkeys: List<Monkey>, worryEases: Boolean) {
        monkeys.forEach { monkey ->
            monkey.items.forEach { item ->
                if (monkey.operate(item, worryEases)) {
                    monkeys[monkey.throwTo[0]].items.add(item)
                } else {
                    monkeys[monkey.throwTo[1]].items.add(item)
                }
            }

            monkey.items.clear()
        }
    }

    fun part1(input: String): Int {
        val monkeys = input
            .split("\n\n")
            .map { Monkey.of(it.lines()) }

        repeat(20) {
            processRound(monkeys, true)
        }

        return monkeys.map { it.inspectedItems }.sortedDescending().take(2).reduce { a, b -> a * b }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readText("Day11_test")
    check(part1(testInput) == 10605)

    val input = readText("Day11")
    part1(input).println() // 110888
}
