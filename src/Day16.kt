fun main() {
    data class Valve(val name: String, val flowRate: Int, val connectedTo: List<String>) {
        override fun toString(): String {
            return "$name [$flowRate]"
        }
    }

    data class MinuteScore(val minutesRemaining: Int, val score: Int) {
        fun afterOpen(valveOpened: Valve, travelTime: Int): MinuteScore {
            val minutesToGo = minutesRemaining - travelTime

            return MinuteScore(
                minutesToGo, score + (minutesToGo * valveOpened.flowRate)
            )
        }

        fun inferiorTo(other: MinuteScore): Boolean {
            return (score < other.score)
        }
    }

    data class ValveState(val current: Valve, val closed: Set<Valve>) {
        fun open(valve: Valve): ValveState {
            return ValveState(valve, closed.minus(valve))
        }
    }


    data class State(val score: MinuteScore, val valveState: ValveState, val route: List<Pair<Valve, List<Valve>>>) {
        fun moveAndOpen(target: Valve, routing: List<Pair<Valve, Map<Valve, Valve?>>>): State {
            val route = routeTo(target, routing)

            return State(
                score.afterOpen(target, route.size),
                valveState.open(target),
                this.route.plus(Pair(target, route))
            )
        }

        fun routeTo(end: Valve, routing: List<Pair<Valve, Map<Valve, Valve?>>>) =
            shortestPath(routing.first { it.first == valveState.current }.second, valveState.current, end)

        fun possibleNextMoves(routing: List<Pair<Valve, Map<Valve, Valve?>>>): List<Valve> {
            return valveState.closed.filter { routeTo(it, routing).size <= score.minutesRemaining }
        }
    }

    fun parse(input: List<String>): List<Valve> {
        return input.map {
            val words = it.split(" ", ",").filter { x -> x.isNotEmpty() }

            val startValve = words[1]
            val flowRate = words[4].removePrefix("rate=").removeSuffix(";").toInt()
            val connectedTo = words.subList(9, words.size)

            Valve(startValve, flowRate, connectedTo)
        }.sortedBy { it.name }
    }

    fun configureRoutingGraph(valves: List<Valve>): Pair<Map<Pair<Valve, Valve>, Int>, Map<Valve, Set<Valve>>> {
        val weights =
            valves.flatMap { a -> valves.filter { it.name in a.connectedTo }.map { b -> Pair(a, b) } }.distinct()
                .associateWith { 1 }
        val edges = valves.associateWith { valves.filter { x -> x.name in it.connectedTo }.toSet() }
        return Pair(weights, edges)
    }

    fun configureRouting(allValves: List<Valve>): List<Pair<Valve, Map<Valve, Valve?>>> {
        val (weights, edges) = configureRoutingGraph(allValves)
        return allValves.map { it to dijkstra(Graph(allValves.toSet(), edges, weights), it) }
    }

    fun runUsingBFS(input: List<String>): Int {
        val allValves = parse(input)
        val routing = configureRouting(allValves)

        val valvesWithFlow = allValves.filter { it.flowRate > 0 }.toSet()
        val startPosition = allValves.first()

        val initialState = State(MinuteScore(30, 0), ValveState(startPosition, valvesWithFlow), listOf())

        // set up initial moves from the start position
        val queue = ArrayDeque<Pair<State, Valve>>()

        queue.addAll(initialState.possibleNextMoves(routing)
            .sortedByDescending { it.flowRate }
            .map { initialState to it })

        var maxScore = 0
        var maxScoreState: State = initialState

        val scoreMap = hashMapOf<ValveState, MinuteScore>()

        while (queue.isNotEmpty()) {
            val (state, target) = queue.removeFirst()

            val newState = state.moveAndOpen(target, routing)

            val priorScore = scoreMap.getOrDefault(newState.valveState, newState.score)

            // ensure that this is nota less optimal way to reach a state we already know about
            if (!newState.score.inferiorTo(priorScore)) {
                scoreMap[newState.valveState] = newState.score

                // check for new high score
                if (newState.score.score > maxScore) {
                    maxScore = newState.score.score
                    maxScoreState = newState
                }

                queue.addAll(newState.possibleNextMoves(routing)
                    .sortedByDescending { it.flowRate }
                    .map { newState to it })
            }
        }

        println("Max score state: $maxScoreState")
        return maxScore
    }

    fun part1(input: List<String>): Int {
        return runUsingBFS(input)
    }


    val testInput = readInput("Day16_test")
    check(part1(testInput) == 1651)
//    check(part2(testInput) == 1707)

    val input = readInput("Day16")
    part1(input).println() // 1775
}
