typealias Routing<T, U> = List<Pair<T, Map<T, U>>>

fun main() {
    data class Valve(val name: String, val flowRate: Int, val connectedTo: List<String>) {
        override fun toString() = "$name [$flowRate]"
    }

    data class UniquePosition(val playerPositions: Set<Valve>, val closedValves: Set<Valve>)

    data class Player(val current: Valve, val remaining: Int)

    data class Move(val player: Player, val target: Valve)

    data class State(val score: Int, val players: List<Player>, val closedValves: Set<Valve>) {
        fun makeMoves(moves: List<Move>, routing: Routing<Valve, Valve?>): State {
            var state = this

            moves.forEach { move -> state = state.makeSingleMove(move, routing) }

            return state
        }

        private fun makeSingleMove(move: Move, routing: Routing<Valve, Valve?>): State {
            val timeTaken = routeTo(move.player.current, move.target, routing).size

            val newPlayer = Player(move.target, move.player.remaining - timeTaken)
            val moveScore = newPlayer.remaining * move.target.flowRate

            return State(
                this.score + moveScore,
                players.minus(move.player).plus(newPlayer),
                closedValves.minus(move.target)
            )
        }

        fun possibleMovesForPlayer(player: Player, routing: Routing<Valve, Valve?>) =
            closedValves.filter { routeTo(player.current, it, routing).size <= player.remaining }
                .sortedByDescending { it.flowRate }
                .map { Move(player, it) }

        private fun routeTo(from: Valve, to: Valve, routing: Routing<Valve, Valve?>) =
            shortestPath(routing.first { it.first == from }.second, from, to)

        fun asPosition() = UniquePosition(players.map { it.current }.toSet(), closedValves)
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

    fun configureRouting(allValves: List<Valve>): Routing<Valve, Valve?> {
        val (weights, edges) = configureRoutingGraph(allValves)
        return allValves.map { it to dijkstra(Graph(allValves.toSet(), edges, weights), it) }
    }

    fun possibleMoves(state: State, routing: Routing<Valve, Valve?>): List<List<Move>> {
        val allPossibleMoves =
            state.players.map { state.possibleMovesForPlayer(it, routing) }.filter { it.isNotEmpty() }

        return if (allPossibleMoves.isEmpty()) {
            allPossibleMoves
        } else if (allPossibleMoves.size == 1) {
            allPossibleMoves.first().map { listOf(it) }
        } else {
            allPossibleMoves[0].cartesianProduct(allPossibleMoves[1]) { a, b -> listOf(a, b) }.filter { it[0].target != it[1].target }
        }
    }

    fun runSimulation(minutes: Int, players: Int, input: List<String>): Int {
        if (players > 2) error("More than 2 players not supported")

        val allValves = parse(input)
        val routing = configureRouting(allValves)

        val valvesWithFlow = allValves.filter { it.flowRate > 0 }.toSet()
        val startPosition = allValves.first()

        val initialState = State(0, List(players) { Player(startPosition, minutes) }, valvesWithFlow)

        // set up initial moves from the start position
        val queue = ArrayDeque<Pair<State, List<Move>>>()
        queue.addAll(possibleMoves(initialState, routing).map { initialState to it })

        var maxScore = 0
        var maxScoreState = initialState

        val scoreMap = hashMapOf<UniquePosition, Int>()

        while (queue.isNotEmpty()) {
            val (state, moves) = queue.removeFirst()

            val newState = state.makeMoves(moves, routing)
            val position = newState.asPosition()
            val priorScore = scoreMap.getOrDefault(position, -1)

            // ensure that this is not a less optimal way to reach a state we already know about
            if (newState.score > priorScore) {
                scoreMap[position] = newState.score

                // check for new high score
                if (newState.score > maxScore) {
                    maxScore = newState.score
                    maxScoreState = newState
                }

                queue.addAll(possibleMoves(newState, routing).map { newState to it })
            }
        }

        println("Max score state: $maxScoreState")
        return maxScore
    }

    fun part1(input: List<String>): Int {
        return runSimulation(30, 1, input)
    }

    fun part2(input: List<String>): Int {
        return runSimulation(26, 2, input)
    }

    val testInput = readInput("Day16_test")
    check(part1(testInput) == 1651)
    check(part2(testInput) == 1707)

    val input = readInput("Day16")
    part1(input).println() // 1775
    part2(input).println() // 2351
}
